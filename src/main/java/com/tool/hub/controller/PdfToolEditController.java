package com.tool.hub.controller;

import com.tool.hub.service.PdfToolEditService;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/pdf")
//@CrossOrigin(origins = "http://localhost:5173")
public class PdfToolEditController {

    @Autowired
    private PdfToolEditService pdfToolEditService;

    @PostMapping("/rotate")
    public ResponseEntity<?> rotatePdf(
        @RequestParam("file") MultipartFile file,
        @RequestParam("angle") int angle
    ) {
        try {
            byte[] rotatedPdf = pdfToolEditService.rotatePdf(file, angle);
            return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=rotated.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(rotatedPdf);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/page-number")
    public ResponseEntity<?> addPageNumber(
        @RequestParam("file") MultipartFile file
    ) {
        try {
            byte[] pdfWithPageNumber = pdfToolEditService.addPageNumber(file);
            return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=page-number.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfWithPageNumber);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/remove-pages")
    public ResponseEntity<?> removePages(
        @RequestParam("file") MultipartFile file,
        @RequestParam("pages") String pages
    ) {
        try {
            byte[] updatedPdf = pdfToolEditService.removePage(file, pages);

            return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=updated.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(updatedPdf);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/extract-pages")
    public ResponseEntity<?> extractPages(
        @RequestParam("file") MultipartFile file,
        @RequestParam("pages") String pages
    ) {
        try {
            byte[] extractedPdf = pdfToolEditService.extractPages(file, pages);

            return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=extracted.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(extractedPdf);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/word-to-pdf")
    public ResponseEntity<?> wordToPdf(
        @RequestParam("file") MultipartFile file
    ) {
        try {
            byte[] pdf = pdfToolEditService.wordToPdf(file);

            return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=converted.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/pdf-to-word")
    public ResponseEntity<?> pdfToWord(
        @RequestParam("file") MultipartFile file
    ) {
        try {
            byte[] word = pdfToolEditService.pdfToWord(file);

            return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=converted.docx"
                )
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(word);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/excel-to-pdf")
    public ResponseEntity<?> excelToPdf(
        @RequestParam("file") MultipartFile file
    ) {
        try {
            byte[] pdf = pdfToolEditService.excelToPdf(file);

            return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=excel.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/ppt-to-pdf")
    public ResponseEntity<?> pptToPdf(
        @RequestParam("file") MultipartFile file
    ) {
        try {
            byte[] pdf = pdfToolEditService.pptToPdf(file);

            return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=excel.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/html-to-pdf")
    public ResponseEntity<?> htmlToPdf(
        @RequestParam("file") MultipartFile file
    ) {
        try {
            byte[] pdf = pdfToolEditService.htmlToPdf(file);

            return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; " + "filename=html.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
