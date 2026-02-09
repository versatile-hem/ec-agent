package com.ek.app.productcatalog.domain;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductDto {

    private Long productId;

    private String sku;

    private String product_title;

    private String barcode;

    private String category;

    private String hsn;

    private String tax_code;

    private java.math.BigDecimal weightGrams;

    private java.math.BigDecimal mrp;

    private Long availableStock;

}
