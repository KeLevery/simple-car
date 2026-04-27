package com.simplecar.service.impl;

import com.simplecar.service.UploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UploadServiceImpl implements UploadService {

    @Value("${upload.path}")
    private String uploadPath;

    public Map<String, Object> upload(MultipartFile file) throws IOException {
        File pathFile = new File(uploadPath);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;
        Path path = Paths.get(uploadPath + fileName);
        Files.write(path, file.getBytes());

        String relativeUrl = "/uploads/" + fileName;
        Map<String, Object> data = new HashMap<>();
        data.put("fileName", relativeUrl);
        data.put("url", relativeUrl);
        return data;
    }
}
