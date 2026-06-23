package com.tool.hub.serviceImpl;

import com.tool.hub.service.JpgToPdfService;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class JpgToPdfServiceImpl implements JpgToPdfService {

    @Override
    public byte[] convertJpgToPdf(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new RuntimeException("Please upload image files");
        }

        try (
            PDDocument document = new PDDocument();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }

                String fileName = file.getOriginalFilename();

                if (
                    fileName == null ||
                    (!fileName.toLowerCase().endsWith(".jpg") &&
                        !fileName.toLowerCase().endsWith(".jpeg") &&
                        !fileName.toLowerCase().endsWith(".png"))
                ) {
                    throw new RuntimeException(
                        "Only JPG and PNG files are allowed"
                    );
                }

                BufferedImage image = ImageIO.read(file.getInputStream());

                if (image == null) {
                    throw new RuntimeException(
                        "Invalid image: " +
                            fileName +
                            ". Use standard JPG or PNG."
                    );
                }

                float width = image.getWidth();

                float height = image.getHeight();

                PDPage page = new PDPage(new PDRectangle(width, height));

                document.addPage(page);

                PDImageXObject pdImage = LosslessFactory.createFromImage(
                    document,
                    image
                );

                try (
                    PDPageContentStream contentStream = new PDPageContentStream(
                        document,
                        page
                    )
                ) {
                    contentStream.drawImage(pdImage, 0, 0, width, height);
                }
            }

            document.save(outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(
                "JPG to PDF conversion failed : " + e.getMessage()
            );
        }
    }

    @Override
    public byte[] protectPdf(MultipartFile file, String password)
        throws Exception {
        // File validation
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please upload a PDF file");
        }

        // Password validation
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Password is required");
        }
        // PDF validation
        String contentType = file.getContentType();

        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new RuntimeException("Only PDF files are allowed");
        }

        try (
            PDDocument document = PDDocument.load(file.getBytes());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            AccessPermission permission = new AccessPermission();

            StandardProtectionPolicy policy = new StandardProtectionPolicy(
                "owner123",
                password,
                permission
            );

            policy.setEncryptionKeyLength(128);

            policy.setPermissions(permission);

            document.protect(policy);

            document.setAllSecurityToBeRemoved(false);
            document.save(outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to protect PDF : " + e.getMessage()
            );
        }
    }

    @Override
    public byte[] unlockPdf(MultipartFile file, String password)
        throws Exception {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please upload a PDF file");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Password is required");
        }

        try (
            PDDocument document = PDDocument.load(
                file.getBytes(),
                password.trim()
            );
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            // remove protection
            document.setAllSecurityToBeRemoved(true);

            document.save(outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Wrong password or invalid PDF");
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to unlock PDF : " + e.getMessage()
            );
        }
    }

    @Override
    public byte[] watermarkPdf(MultipartFile file, String text)
        throws Exception {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please upload a PDF file");
        }

        if (text == null || text.trim().isEmpty()) {
            throw new RuntimeException("Watermark text is required");
        }

        try (
            PDDocument document = PDDocument.load(file.getBytes());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            for (PDPage page : document.getPages()) {
                PDRectangle pageSize = page.getMediaBox();
                float width = pageSize.getWidth();
                float height = pageSize.getHeight();

                try (
                    PDPageContentStream contentStream = new PDPageContentStream(
                        document,
                        page,
                        PDPageContentStream.AppendMode.APPEND,
                        true,
                        true
                    )
                ) {
                    // TRANSPARENCY (important)
                    PDExtendedGraphicsState graphicsState =
                        new PDExtendedGraphicsState();

                    graphicsState.setNonStrokingAlphaConstant(0.12f); // opacity

                    contentStream.setGraphicsStateParameters(graphicsState);

                    // very light gray
                    contentStream.setNonStrokingColor(new Color(180, 180, 180));

                    contentStream.beginText();

                    contentStream.setFont(PDType1Font.HELVETICA, 85);

                    // diagonal + center
                    contentStream.setTextMatrix(
                        Matrix.getRotateInstance(
                            Math.toRadians(45),
                            width / 2.3f,
                            height / 2.3f
                        )
                    );

                    contentStream.newLineAtOffset(-170, 0);

                    contentStream.showText(text);

                    contentStream.endText();

                    contentStream.close();
                }
            }

            document.save(outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to watermark PDF : " + e.getMessage()
            );
        }
    }
}
