package com.simplecar.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface UploadService {
    Map<String, Object> upload(MultipartFile file) throws IOException;
}
