package com.mesara.app.repository;

import com.mesara.app.domain.ProductStock;
import com.mesara.app.domain.Product;
import com.mesara.app.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {

    Optional<ProductStock> findByStoreAndProduct(Store store, Product product);
}