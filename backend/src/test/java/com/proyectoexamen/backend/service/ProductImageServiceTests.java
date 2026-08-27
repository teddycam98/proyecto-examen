package com.proyectoexamen.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.proyectoexamen.backend.exception.BusinessException;
import com.proyectoexamen.backend.service.impl.ProductImageServiceImpl;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

class ProductImageServiceTests {

    @TempDir
    Path temporaryDirectory;

    @Test
    void storesAnAllowedImageWithASafeGeneratedName() throws Exception {
        ProductImageService service = new ProductImageServiceImpl(temporaryDirectory.toString());
        MockMultipartFile image = new MockMultipartFile("imageFile", "foto.png", "image/png", new byte[]{1, 2, 3});

        String filename = service.store(image);

        assertThat(filename).endsWith(".png").doesNotContain("foto");
        assertThat(Files.readAllBytes(temporaryDirectory.resolve(filename))).containsExactly(1, 2, 3);
    }

    @Test
    void rejectsFilesThatAreNotImages() {
        ProductImageService service = new ProductImageServiceImpl(temporaryDirectory.toString());
        MockMultipartFile file = new MockMultipartFile("imageFile", "notas.txt", "text/plain", "texto".getBytes());

        assertThatThrownBy(() -> service.store(file))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("JPG, PNG o WEBP");
    }
}
