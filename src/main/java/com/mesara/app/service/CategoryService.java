package com.mesara.app.service;

import com.mesara.app.domain.Category;
import com.mesara.app.dto.CategoryRequest;
import com.mesara.app.exception.ResourceNotFoundException;
import com.mesara.app.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public List<Category> getAllActive(){
        return categoryRepository.findAllByActiveTrue();
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));
    }

    @Transactional
    public void save(Category category) {
        categoryRepository.save(category);
    }

    @Transactional
    public void softDelete(Long id) {
        Category category = getById(id);
        category.setActive(false);
        categoryRepository.save(category);
    }

    @Transactional
    public Category createCategoryFromRequest(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.name());
        category.setActive(true);

        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategoryFromRequest(Long id, CategoryRequest request) {
        Category updateCategory = getById(id);
        updateCategory.setName(request.name());

        return categoryRepository.save(updateCategory);
    }


}