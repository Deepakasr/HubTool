package com.tool.hub.serviceImpl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.tool.hub.service.PdfToolEditService;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfToolEditServiceImpl implements PdfToolEditService {

    @Override
    public byte[] rotatePdf(MultipartFile file, int angle) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please upload a PDF file");
        }

        // only 90, 180, 270 degrees ====

        if (angle != 90 && angle != 180 && angle != 270) {
            throw new RuntimeException(
                "Invalid angle. Only 90, 180, 270 degrees are supported"
            );
        }

        try (
            PDDocument document = PDDocument.load(file.getInputStream());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            for (PDPage page : document.getPages()) {
                int currentRotation = page.getRotation();
                page.setRotation(currentRotation + angle);
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to rotate PDF: " + e.getMessage()
            );
        }
    }

    @Override
    public byte[] addPageNumber(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please upload a PDF file");
        }

        try (
            PDDocument document = PDDocument.load(file.getInputStream());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            int totalPages = document.getNumberOfPages();
            for (int i = 0; i < totalPages; i++) {
                PDPage page = document.getPage(i);

                PDRectangle pageSize = page.getMediaBox();
                float width = pageSize.getWidth();

                try (
                    PDPageContentStream contentStream = new PDPageContentStream(
                        document,
                        page,
                        PDPageContentStream.AppendMode.APPEND,
                        true,
                        true
                    )
                ) {
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 11);
                    contentStream.newLineAtOffset(width / 2 - 15, 20);
                    contentStream.showText("Page " + (i + 1));
                    contentStream.endText();
                    contentStream.close();
                }
            }

            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to add page number: " + e.getMessage()
            );
        }
    }

    @Override
    public byte[] removePage(MultipartFile file, String pages)
        throws Exception {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please upload PDF");
        }

        if (pages == null || pages.trim().isEmpty()) {
            throw new RuntimeException("Please enter page numbers");
        }

        try (
            PDDocument document = PDDocument.load(file.getInputStream());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            String[] splitPages = pages.split(",");

            List<Integer> pageIndexes = new ArrayList<>();

            for (String p : splitPages) {
                int pageNumber = Integer.parseInt(p.trim());

                pageIndexes.add(pageNumber - 1);
            }

            // remove reverse order
            Collections.sort(pageIndexes, Collections.reverseOrder());

            for (Integer index : pageIndexes) {
                if (index >= 0 && index < document.getNumberOfPages()) {
                    document.removePage(index);
                }
            }

            document.save(outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to remove pages : " + e.getMessage()
            );
        }
    }

    @Override
    public byte[] extractPages(MultipartFile file, String pages)
        throws Exception {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please upload PDF");
        }

        if (pages == null || pages.trim().isEmpty()) {
            throw new RuntimeException("Please enter page numbers");
        }

        try (
            PDDocument sourceDocument = PDDocument.load(file.getInputStream());
            PDDocument newDocument = new PDDocument();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            String[] splitPages = pages.split(",");

            for (String p : splitPages) {
                int pageNumber = Integer.parseInt(p.trim()) - 1;

                if (
                    pageNumber >= 0 &&
                    pageNumber < sourceDocument.getNumberOfPages()
                ) {
                    newDocument.importPage(sourceDocument.getPage(pageNumber));
                }
            }

            if (newDocument.getNumberOfPages() == 0) {
                throw new RuntimeException("No valid pages selected");
            }

            newDocument.save(outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to extract pages : " + e.getMessage()
            );
        }
    }

    @Override
    public byte[] wordToPdf(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please upload Word file");
        }

        String fileName = file.getOriginalFilename();

        if (
            fileName == null ||
            !(fileName.endsWith(".docx") || fileName.endsWith(".doc"))
        ) {
            throw new RuntimeException("Only .doc or .docx allowed");
        }

        try (
            InputStream inputStream = file.getInputStream();
            XWPFDocument wordDocument = new XWPFDocument(inputStream);
            PDDocument pdfDocument = new PDDocument();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            PDPage page = new PDPage();

            pdfDocument.addPage(page);

            PDPageContentStream content = new PDPageContentStream(
                pdfDocument,
                page
            );

            content.beginText();

            content.setFont(PDType1Font.HELVETICA, 12);

            content.setLeading(16f);

            content.newLineAtOffset(50, 700);

            for (XWPFParagraph paragraph : wordDocument.getParagraphs()) {
                String text = paragraph.getText();

                if (text != null && !text.isBlank()) {
                    content.showText(text);

                    content.newLine();
                }
            }

            content.endText();
            content.close();

            pdfDocument.save(outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(
                "Word to PDF conversion failed : " + e.getMessage()
            );
        }
    }

    @Override
    public byte[] pdfToWord(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please upload PDF file");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || !fileName.toLowerCase().endsWith(".pdf")) {
            throw new RuntimeException("Only PDF file allowed");
        }

        try (
            PDDocument pdfDocument = PDDocument.load(file.getInputStream());
            XWPFDocument wordDocument = new XWPFDocument();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            PDFTextStripper stripper = new PDFTextStripper();

            String extractedText = stripper.getText(pdfDocument);

            XWPFParagraph paragraph = wordDocument.createParagraph();

            XWPFRun run = paragraph.createRun();

            run.setText(extractedText);

            wordDocument.write(outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(
                "Word to PDF conversion failed : " + e.getMessage()
            );
        }
    }

    @Override
    public byte[] excelToPdf(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please upload Excel file");
        }
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".xlsx")) {
            throw new RuntimeException("Only .xlsx supported");
        }
        try (
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            PDDocument pdfDocument = new PDDocument();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            Sheet sheet = workbook.getSheetAt(0);
            PDPage page = new PDPage(PDRectangle.A4);
            pdfDocument.addPage(page);
            PDPageContentStream content = new PDPageContentStream(
                pdfDocument,
                page
            );
            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 10);
            content.setLeading(14f);
            content.newLineAtOffset(40, 780);
            for (Row row : sheet) {
                StringBuilder rowText = new StringBuilder();
                for (Cell cell : row) {
                    rowText.append(cell.toString());
                    rowText.append(" ");
                }
                content.showText(rowText.toString());
                content.newLine();
            }
            content.endText();
            content.close();
            pdfDocument.save(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Excel to PDF failed : " + e.getMessage()
            );
        }
    }

    @Override
    public byte[] pptToPdf(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please upload a valid PPT file");
        }

        String fileName = file.getOriginalFilename();

        if (
            fileName == null ||
            !(fileName.endsWith(".ppt") || fileName.endsWith(".pptx"))
        ) {
            throw new RuntimeException("Only .ppt and .pptx files allowed");
        }

        try (
            InputStream inputStream = file.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PDDocument document = new PDDocument()
        ) {
            // ===== PPTX =====
            if (fileName.toLowerCase().endsWith(".pptx")) {
                XMLSlideShow ppt = new XMLSlideShow(inputStream);

                Dimension pgsize = ppt.getPageSize();

                for (XSLFSlide slide : ppt.getSlides()) {
                    addSlideToPdf(slide, document, pgsize);
                }
            }
            // ===== PPT =====
            else {
                HSLFSlideShow ppt = new HSLFSlideShow(inputStream);

                Dimension pgsize = ppt.getPageSize();

                for (HSLFSlide slide : ppt.getSlides()) {
                    addSlideToPdf(slide, document, pgsize);
                }
            }

            document.save(outputStream);

            return outputStream.toByteArray();
        }
    }

    private void addSlideToPdf(
        Object slide,
        PDDocument document,
        Dimension pgsize
    ) throws Exception {
        BufferedImage img = new BufferedImage(
            pgsize.width,
            pgsize.height,
            BufferedImage.TYPE_INT_RGB
        );

        Graphics2D graphics = img.createGraphics();

        graphics.setPaint(Color.WHITE);
        graphics.fillRect(0, 0, pgsize.width, pgsize.height);

        graphics.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        graphics.setRenderingHint(
            RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY
        );

        // Draw slide
        if (slide instanceof XSLFSlide) {
            ((XSLFSlide) slide).draw(graphics);
        } else if (slide instanceof HSLFSlide) {
            ((HSLFSlide) slide).draw(graphics);
        }

        graphics.dispose();

        PDPage page = new PDPage(new PDRectangle(pgsize.width, pgsize.height));

        document.addPage(page);

        PDImageXObject image = LosslessFactory.createFromImage(document, img);

        try (
            PDPageContentStream content = new PDPageContentStream(
                document,
                page
            )
        ) {
            content.drawImage(image, 0, 0, pgsize.width, pgsize.height);
        }
    }

    @Override
    public byte[] htmlToPdf(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please upload HTML file");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || !fileName.toLowerCase().endsWith(".html")) {
            throw new RuntimeException("Only HTML files allowed");
        }

        try (
            InputStream inputStream = file.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        ) {
            String htmlContent = new String(
                inputStream.readAllBytes(),
                StandardCharsets.UTF_8
            );

            // Fix broken HTML
            Document document = Jsoup.parse(htmlContent);

            document
                .outputSettings()
                .syntax(Document.OutputSettings.Syntax.xml);

            htmlContent = document.html();

            PdfRendererBuilder builder = new PdfRendererBuilder();

            builder.useFastMode();

            builder.withHtmlContent(htmlContent, null);

            builder.toStream(outputStream);

            builder.run();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("HTML to PDF failed: " + e.getMessage());
        }
    }
}
