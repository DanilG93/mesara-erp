package com.mesara.app.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(optional = true) // Dozvoljava da veza ne postoji
    @JoinColumn(name = "category_id", nullable = true) // Dozvoljava NULL u bazi
    private Category category;

    private String unit = "kg";

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    private boolean active = true;
}