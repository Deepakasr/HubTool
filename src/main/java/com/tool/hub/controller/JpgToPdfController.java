package com.tool.hub.controller;

import com.tool.hub.service.JpgToPdfService;
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
@RequestMapping("/jpg-api")
//@CrossOrigin(origins = "http://localhost:5173")
public class JpgToPdfController {

    @Autowired
    private JpgToPdfService jpgToPdfService;

    @PostMapping("/jpg-to-pdf")
    public ResponseEntity<byte[]> convertJpgToPdf(
        @RequestParam("files") MultipartFile[] files
    ) {
        byte[] pdfBytes = jpgToPdfService.convertJpgToPdf(files);

        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=converted.pdf"
            )
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes);
    }

    @PostMapping("/protect-pdf")
    public ResponseEntity<?> protectPdf(
        @RequestParam("file") MultipartFile file,
        @RequestParam("password") String password
    ) {
        try {
            byte[] protectedPdf = jpgToPdfService.protectPdf(file, password);

            return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=protected.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(protectedPdf);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                "Failed to protect PDF: " + e.getMessage()
            );
        }
    }

    @PostMapping("/unlock")
    public ResponseEntity<?> unlockPdf(
        @RequestParam("file") MultipartFile file,
        @RequestParam("password") String password
    ) {
        try {
            byte[] unlockedPdf = jpgToPdfService.unlockPdf(file, password);

            return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=unlocked.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(unlockedPdf);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Watermark a PDF with text API -----
    @PostMapping("/watermark")
    public ResponseEntity<?> watermarkPdf(
        @RequestParam("file") MultipartFile file,
        @RequestParam("text") String text
    ) {
        try {
            byte[] watermarkedPdf = jpgToPdfService.watermarkPdf(file, text);

            return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=watermarked.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(watermarkedPdf);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
