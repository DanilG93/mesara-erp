package com.mesara.app.service;


import com.mesara.app.domain.Category;
import com.mesara.app.domain.Product;
import com.mesara.app.dto.ProductRequest;
import com.mesara.app.exception.ResourceNotFoundException;
import com.mesara.app.repository.CategoryRepository;
import com.mesara.app.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public List<Product> getAllActive() {
        return productRepository.findAllByActiveTrue();
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    @Transactional
    public void save(Product product) {

        productRepository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public void softDelete(Long id) {
        Product product = getById(id);
        product.setActive(false);
        productRepository.save(product);
    }


    @Transactional
    public Product createProductFromRequest(ProductRequest request) {
        Product product = new Product();
        product.setName(request.name());

        if (request.unit() != null && !request.unit().isBlank()) {
            product.setUnit(request.unit());
        }
        if (request.price() != null) {
            product.setPrice(request.price());
        }

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.categoryId()));

            product.setCategory(category);
        }

        product.setActive(true);
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProductFromRequest(Long id, ProductRequest request) {
        Product product = getById(id);

        product.setName(request.name());

        if (request.unit() != null && !request.unit().isBlank()) {
            product.setUnit(request.unit());
        }

        if (request.price() != null) {
            product.setPrice(request.price());
        }

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.categoryId()));
            product.setCategory(category);
        }

        return productRepository.save(product);
    }
}
