package com.ek.app.productcatalog.infra.db;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "listing",
       uniqueConstraints = @UniqueConstraint(name = "uq_listing_channel_sku", columnNames = {"channel", "listing_sku"}),
       indexes = @Index(name = "idx_listing_product", columnList = "product_id"))
public class Listing extends BaseAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "listing_id", updatable = false, nullable = false)
    private Long listingId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "channel", length = 32, nullable = false) // e.g., WEBSITE, AMAZON
    private String channel;

    @Column(name = "listing_sku", length = 64, nullable = false)
    private String listingSku;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "price_currency", length = 3, nullable = false)
    private String priceCurrency = "INR";

    @Column(name = "price_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal priceAmount = BigDecimal.ZERO;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}

