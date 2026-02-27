package com.mesara.app.service;


import com.mesara.app.domain.Product;
import com.mesara.app.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public List<Product> getAllActive() {
        return productRepository.findAllByActiveTrue();
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artikal nije pronađen sa ID-om: " + id));
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
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proizvod nije nađen"));
        product.setActive(false); // Postavljamo na 0
        productRepository.save(product);
    }
}
