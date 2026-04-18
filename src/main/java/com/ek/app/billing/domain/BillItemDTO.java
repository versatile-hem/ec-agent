package com.ek.app.billing.domain;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillItemDTO {

    private Long productId;

    @Schema(example = "Industrial Label Roll")
    private String productName;

    @Schema(example = "2")
    private BigDecimal quantity;

    @Schema(example = "600")
    private BigDecimal unitPrice;

    private BigDecimal lineTotal;

    @Schema(example = "SKU-1001")
    private String sku;

    private String product_title;

    private String barcode;

    private String category;

    private String hsn;

    private String tax_code;

    private BigDecimal gst;

    private java.math.BigDecimal weightGrams;

    private java.math.BigDecimal mrp;

    private Long availableStock;

    private BigDecimal taxableValue ;

    private BigDecimal tax ;

    private BigDecimal finalAmount;

    

}
