package com.proyectoexamen.backend.service;

import com.proyectoexamen.backend.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> findAll();
    List<Category> findActive();
    Category findById(Long id);
    Category save(Category category);
    void toggle(Long id);
}
