package com.ek.app.productcatalog.infra.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "product_uom",
       indexes = @Index(name = "idx_uom_code", columnList = "code", unique = true))
public class ProductUom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uom_id", updatable = false, nullable = false)
    private Long uomId;

    @Column(name = "code", length = 16, nullable = false, unique = true)
    private String code; // e.g., UNIT, BOX, KG

    @Column(name = "name", length = 64, nullable = false)
    private String name;
}

