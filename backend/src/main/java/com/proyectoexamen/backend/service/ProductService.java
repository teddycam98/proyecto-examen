package com.proyectoexamen.backend.service;

import com.proyectoexamen.backend.entity.Product;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    List<Product> search(String term);
    List<Product> findActive();
    List<Product> findLowStock();
    Product findById(Long id);
    Product save(Product form, MultipartFile imageFile);
    void toggle(Long id);
    long countActive();
}
