package com.tool.hub.service;

import java.lang.Exception;
import org.springframework.web.multipart.MultipartFile;

public interface PdfService {
    byte[] mergePdf(MultipartFile[] files) throws Exception;

    byte[] splitPdf(MultipartFile file, int startPage, int endPage)
        throws Exception;

    byte[] compressPdf(MultipartFile file) throws Exception;

    byte[] pdfToJpg(MultipartFile file) throws Exception;
}
