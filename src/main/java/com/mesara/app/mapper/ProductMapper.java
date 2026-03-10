package com.mesara.app.mapper;

import com.mesara.app.domain.Product;
import com.mesara.app.dto.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory() != null ? product.getCategory().getName() : "Uncategorized",
                product.getUnit(),
                product.getPrice(),
                product.isActive()
        );
    }
}
