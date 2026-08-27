package com.proyectoexamen.backend.service.impl;

import com.proyectoexamen.backend.exception.BusinessException;
import com.proyectoexamen.backend.exception.ResourceNotFoundException;
import com.proyectoexamen.backend.entity.Category;
import com.proyectoexamen.backend.repository.CategoryRepository;
import com.proyectoexamen.backend.service.CategoryService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;

    public CategoryServiceImpl(CategoryRepository repository) { this.repository = repository; }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() { return repository.findAllByOrderByNameAsc(); }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findActive() { return repository.findByActiveTrueOrderByNameAsc(); }

    @Override
    @Transactional(readOnly = true)
    public Category findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada"));
    }

    @Override
    @Transactional
    public Category save(Category category) {
        boolean duplicate = category.getId() == null
            ? repository.existsByNameIgnoreCase(category.getName())
            : repository.existsByNameIgnoreCaseAndIdNot(category.getName(), category.getId());
        if (duplicate) throw new BusinessException("Ya existe una categoria con ese nombre");
        return repository.save(category);
    }

    @Override
    @Transactional
    public void toggle(Long id) {
        Category category = findById(id);
        category.setActive(!category.isActive());
    }
}
