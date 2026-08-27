package com.proyectoexamen.backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ProductImageService {
    String store(MultipartFile file);
    Resource load(String filename);
}
