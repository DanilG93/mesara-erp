package com.mesara.app.controller.api;

import com.mesara.app.domain.Product;
import com.mesara.app.dto.ProductRequest;
import com.mesara.app.dto.ProductResponse;
import com.mesara.app.mapper.ProductMapper;
import com.mesara.app.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllActive().stream()
                .map(productMapper::toResponse)
                .toList();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        Product product = productService.getById(id);
        ProductResponse response = productMapper.toResponse(product);

        return ResponseEntity.ok(response);

    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        Product saveProduct = productService.createProductFromRequest(request);
        ProductResponse response = productMapper.toResponse(saveProduct);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                         @Valid @RequestBody ProductRequest request) {

        Product updatedProduct = productService.getById(id);
        ProductResponse response = productMapper.toResponse(updatedProduct);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.softDelete(id);

        return ResponseEntity.noContent().build();
    }


}
