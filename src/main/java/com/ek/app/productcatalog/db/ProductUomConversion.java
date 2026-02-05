package com.ek.app.productcatalog.db;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "product_uom_conversion")
public class ProductUomConversion {

    @EmbeddedId
    private ProductUomConversionId id;

    @MapsId("fromUomId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_uom_id", nullable = false)
    private ProductUom fromUom;

    @MapsId("toUomId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_uom_id", nullable = false)
    private ProductUom toUom;

    @Column(name = "multiplier", precision = 18, scale = 6, nullable = false)
    private BigDecimal multiplier; // multiply quantity in fromUom to get toUom
}

