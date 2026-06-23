package com.tool.hub.service;

import org.springframework.web.multipart.MultipartFile;

public interface JpgToPdfService {
    byte[] convertJpgToPdf(MultipartFile[] files);

    byte[] protectPdf(MultipartFile file, String password) throws Exception;

    byte[] unlockPdf(MultipartFile file, String password) throws Exception;

    byte[] watermarkPdf(MultipartFile file, String text) throws Exception;
}
