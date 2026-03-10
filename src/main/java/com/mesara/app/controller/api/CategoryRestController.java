package com.mesara.app.controller.api;

import com.mesara.app.domain.Category;
import com.mesara.app.dto.CategoryRequest;
import com.mesara.app.dto.CategoryResponse;
import com.mesara.app.mapper.CategoryMapper;
import com.mesara.app.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/categories")
@RequiredArgsConstructor
public class CategoryRestController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;


    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {

        List<CategoryResponse> categories = categoryService.getAllActive().stream()
                .map(categoryMapper::toResponse).toList();

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {

        Category category = categoryService.getById(id);
        CategoryResponse response = categoryMapper.toResponse(category);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {

        Category newCategory = categoryService.createCategoryFromRequest(request);
        CategoryResponse response = categoryMapper.toResponse(newCategory);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id,
                                                           @Valid @RequestBody CategoryRequest request) {

        Category updateCategory = categoryService.getById(id);
        CategoryResponse response = categoryMapper.toResponse(updateCategory);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {

        categoryService.softDelete(id);

        return ResponseEntity.noContent().build();
    }
}
