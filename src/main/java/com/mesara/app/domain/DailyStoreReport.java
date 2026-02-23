package com.mesara.app.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Table(name = "daily_store_reports")
public class DailyStoreReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    private Date reportDate;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalRevenue;

    @Column(length = 500)
    private String note;
}