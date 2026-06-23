package com.tool.hub.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageToolService {
    // Image Compress method ---
    byte[] compressImage(MultipartFile file, float quality) throws Exception;

    // Image Resize method ---
    byte[] resizeImage(MultipartFile file, int width, int height)
        throws Exception;
}
