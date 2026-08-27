package com.proyectoexamen.backend.service.impl;

import com.proyectoexamen.backend.exception.BusinessException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import com.proyectoexamen.backend.service.ProductImageService;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductImageServiceImpl implements ProductImageService {
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final Map<String, String> EXTENSIONS = Map.of(
        "image/jpeg", ".jpg", "image/png", ".png", "image/webp", ".webp"
    );

    private final Path productsDirectory;

    public ProductImageServiceImpl(@Value("${app.upload.products-dir:uploads/products}") String directory) {
        productsDirectory = Path.of(directory).toAbsolutePath().normalize();
    }

    @Override
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED_TYPES.contains(contentType)) {
            throw new BusinessException("La imagen debe ser JPG, PNG o WEBP");
        }
        if (file.getSize() > 5L * 1024 * 1024) {
            throw new BusinessException("La imagen no puede superar los 5 MB");
        }
        String filename = UUID.randomUUID() + EXTENSIONS.get(contentType);
        try {
            Files.createDirectories(productsDirectory);
            Path destination = productsDirectory.resolve(filename).normalize();
            if (!destination.getParent().equals(productsDirectory)) {
                throw new BusinessException("Nombre de imagen no valido");
            }
            try (InputStream input = file.getInputStream()) {
                Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
            }
            return filename;
        } catch (IOException ex) {
            throw new BusinessException("No se pudo guardar la imagen del producto");
        }
    }

    @Override
    public Resource load(String filename) {
        String safeName = Path.of(filename).getFileName().toString();
        try {
            Path file = productsDirectory.resolve(safeName).normalize();
            if (file.getParent().equals(productsDirectory) && Files.isRegularFile(file)) {
                Resource resource = new UrlResource(file.toUri());
                if (resource.isReadable()) return resource;
            }
        } catch (Exception ignored) {
            // Se intenta a continuacion con las ilustraciones incluidas en el proyecto.
        }
        Resource demo = new ClassPathResource("static/img/products/" + safeName);
        if (demo.exists() && demo.isReadable()) return demo;
        throw new BusinessException("Imagen no encontrada");
    }
}
