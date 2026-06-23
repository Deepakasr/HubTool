package com.tool.hub.service;

import org.springframework.web.multipart.MultipartFile;

public interface PdfToolEditService {
    // Rotate pdf tool ----

    byte[] rotatePdf(MultipartFile file, int angle) throws Exception;

    byte[] addPageNumber(MultipartFile file) throws Exception;

    byte[] removePage(MultipartFile file, String pages) throws Exception;

    byte[] extractPages(MultipartFile file, String pages) throws Exception;

    byte[] wordToPdf(MultipartFile file) throws Exception;

    byte[] pdfToWord(MultipartFile file) throws Exception;

    byte[] excelToPdf(MultipartFile file) throws Exception;

    byte[] pptToPdf(MultipartFile file) throws Exception;

    byte[] htmlToPdf(MultipartFile file) throws Exception;
}
