package com.ek.app.nexo.dto;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryDTO {

    private Long id;

    private Long productId;

    private BigDecimal quantity;

    private String warehouseId;

    private Instant lastUpdated;

    private String createdBy;

    private Instant createdAt;

    private Instant updatedAt;
}
