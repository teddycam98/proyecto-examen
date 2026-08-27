package com.proyectoexamen.backend.controller;

import com.proyectoexamen.backend.service.ProductImageService;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/media/productos")
public class ProductImageController {
    private final ProductImageService imageService;

    public ProductImageController(ProductImageService imageService) { this.imageService = imageService; }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> show(@PathVariable String filename) throws IOException {
        Resource image = imageService.load(filename);
        String contentType = image.getURL().openConnection().getContentType();
        MediaType mediaType = contentType == null ? MediaType.APPLICATION_OCTET_STREAM : MediaType.parseMediaType(contentType);
        return ResponseEntity.ok().cacheControl(CacheControl.noCache()).contentType(mediaType).body(image);
    }
}
