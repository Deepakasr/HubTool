package com.tool.hub.controller;

import com.tool.hub.service.ImageToolService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/image")
public class ImageToolController {

    private final ImageToolService imageToolService;

    public ImageToolController(ImageToolService imageToolService) {
        this.imageToolService = imageToolService;
    }

    @PostMapping("/compress")
    public ResponseEntity<byte[]> compressImage(
        @RequestParam("file") MultipartFile file,
        @RequestParam("quality") float quality
    ) throws Exception {
        byte[] compressed = imageToolService.compressImage(file, quality);

        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=compressed.jpg"
            )
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(compressed);
    }

    @PostMapping("/resize")
    public ResponseEntity<?> resizeImage(
        @RequestParam("file") MultipartFile file,
        @RequestParam("width") int width,
        @RequestParam("height") int height
    ) {
        try {
            byte[] resized = imageToolService.resizeImage(file, width, height);

            return ResponseEntity.ok()
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=resized.jpg"
                )
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resized);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
