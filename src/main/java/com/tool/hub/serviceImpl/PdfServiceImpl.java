package com.tool.hub.serviceImpl;

import com.tool.hub.service.PdfService;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfServiceImpl implements PdfService {

    public byte[] mergePdf(MultipartFile[] files) throws Exception {
        PDFMergerUtility pdfMerger = new PDFMergerUtility();

        // Empty validation
        if (files == null || files.length < 2) {
            throw new RuntimeException("Please upload at least 2 PDF files");
        }
        PDFMergerUtility merger = new PDFMergerUtility();

        for (MultipartFile file : files) {
            // Empty file validation
            if (file.isEmpty()) {
                throw new RuntimeException("File cannot be empty");
            }

            // Only PDF validation
            String contentType = file.getContentType();

            if (contentType == null || !contentType.equals("application/pdf")) {
                throw new RuntimeException("Only PDF files are allowed");
            }

            pdfMerger.addSource(file.getInputStream());
        }
        ByteArrayOutputStream mergedStream = new ByteArrayOutputStream();

        pdfMerger.setDestinationStream(mergedStream);

        pdfMerger.mergeDocuments(null);
        return mergedStream.toByteArray();
    }

    @Override
    public byte[] splitPdf(MultipartFile file, int startPage, int endPage)
        throws Exception {
        // Empty validation-----
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please upload a PDF file");
        }

        // Only PDF validation----
        String contentType = file.getContentType();

        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new RuntimeException("Only PDF files are allowed");
        }
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            int totalPages = document.getNumberOfPages();

            // Range validation ----
            if (startPage < 1 || endPage > totalPages || startPage > endPage) {
                throw new RuntimeException("Invaild page range");
            }

            PDDocument newDocument = new PDDocument();

            for (int i = startPage - 1; i < endPage; i++) {
                newDocument.addPage(document.getPage(i));
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            newDocument.save(outputStream);
            newDocument.close();
            return outputStream.toByteArray();
        }
    }

    @Override
    public byte[] compressPdf(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please upload a PDF file");
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new RuntimeException("Only PDF files are allowed");
        }

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            for (PDPage page : document.getPages()) {
                PDResources resources = page.getResources();

                for (COSName name : resources.getXObjectNames()) {
                    PDXObject xObject = resources.getXObject(name);

                    if (xObject instanceof PDImageXObject image) {
                        BufferedImage bufferedImage = image.getImage();

                        // Resize the image to half its original size, with a minimum of 1x1
                        int width = Math.max(bufferedImage.getWidth() / 2, 1);

                        int height = Math.max(bufferedImage.getHeight() / 2, 1);

                        BufferedImage resized = new BufferedImage(
                            width,
                            height,
                            BufferedImage.TYPE_INT_RGB
                        );
                        Graphics2D g2d = resized.createGraphics();

                        g2d.drawImage(bufferedImage, 0, 0, width, height, null);

                        g2d.dispose();

                        // Strong JPEG compression
                        ByteArrayOutputStream imgOut =
                            new ByteArrayOutputStream();

                        ImageWriter writer =
                            ImageIO.getImageWritersByFormatName("jpg").next();

                        ImageWriteParam param = writer.getDefaultWriteParam();

                        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

                        param.setCompressionQuality(0.4f);

                        ImageOutputStream ios = ImageIO.createImageOutputStream(
                            imgOut
                        );

                        writer.setOutput(ios);

                        writer.write(
                            null,
                            new IIOImage(resized, null, null),
                            param
                        );

                        ios.close();
                        writer.dispose();

                        PDImageXObject compressedImage =
                            JPEGFactory.createFromByteArray(
                                document,
                                imgOut.toByteArray()
                            );

                        resources.put(name, compressedImage);
                    }
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            document.save(outputStream);

            return outputStream.toByteArray();
        }
    }

    @Override
    public byte[] pdfToJpg(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please upload a PDF file");
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new RuntimeException("Only PDF files are allowed");
        }

        try (
            PDDocument document = PDDocument.load(file.getInputStream());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zipOut = new ZipOutputStream(baos);
        ) {
            PDFRenderer renderer = new PDFRenderer(document);

            for (int i = 0; i < document.getNumberOfPages(); i++) {
                // Strong quality render
                BufferedImage image = renderer.renderImageWithDPI(
                    i,
                    300,
                    ImageType.RGB
                );

                ByteArrayOutputStream imageBytes = new ByteArrayOutputStream();

                ImageWriter writer = ImageIO.getImageWritersByFormatName(
                    "jpg"
                ).next();

                ImageWriteParam param = writer.getDefaultWriteParam();

                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

                param.setCompressionQuality(0.95f);

                ImageOutputStream ios = ImageIO.createImageOutputStream(
                    imageBytes
                );

                writer.setOutput(ios);

                writer.write(null, new IIOImage(image, null, null), param);

                ios.close();
                writer.dispose();

                // Add to ZIP
                ZipEntry entry = new ZipEntry("page-" + (i + 1) + ".jpg");

                zipOut.putNextEntry(entry);
                zipOut.write(imageBytes.toByteArray());

                zipOut.closeEntry();
            }

            zipOut.finish();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
