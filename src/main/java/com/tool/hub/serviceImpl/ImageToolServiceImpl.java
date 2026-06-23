package com.tool.hub.serviceImpl;

import com.tool.hub.service.ImageToolService;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageToolServiceImpl implements ImageToolService {

    @Override
    public byte[] compressImage(MultipartFile file, float quality)
        throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Thumbnails.of(file.getInputStream())
            .scale(1.0)
            .outputQuality(quality)
            .toOutputStream(output);

        return output.toByteArray();
    }

    @Override
    public byte[] resizeImage(MultipartFile file, int width, int height)
        throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Thumbnails.of(file.getInputStream())
            .size(width, height)
            .outputQuality(1.0)
            .toOutputStream(output);

        return output.toByteArray();
    }
}
