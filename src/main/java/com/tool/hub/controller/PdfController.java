package com.tool.hub.controller;

import com.tool.hub.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/pdf-api")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:5173")
public class PdfController {

    private final PdfService pdfService;

    @PostMapping("/merge")
    public ResponseEntity<?> mergePdf(
        @RequestParam("files") MultipartFile[] files
    ) {
        try {
            byte[] mergedPdf = pdfService.mergePdf(files);
            return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=merged.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(mergedPdf);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/split")
    public ResponseEntity<?> splitPdf(
        @RequestParam("file") MultipartFile file,
        @RequestParam("startPage") int startPage,
        @RequestParam("endPage") int endPage
    ) {
        try {
            byte[] splitPdf = pdfService.splitPdf(file, startPage, endPage);
            return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=splite.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(splitPdf);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/compress")
    public ResponseEntity<?> compressPdf(
        @RequestParam("file") MultipartFile file
    ) {
        try {
            byte[] compressedPdf = pdfService.compressPdf(file);

            return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=compressed.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(compressedPdf);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/pdf-to-jpg")
    public ResponseEntity<?> pdfToJpg(
        @RequestParam("file") MultipartFile file
    ) {
        try {
            byte[] jpgBytes = pdfService.pdfToJpg(file);

            return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=pdf-to-jpg.zip"
                )
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(jpgBytes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
