package com.ek.app.inventory.app.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.ek.app.inventory.infra.db.StockMovementReference;
import com.ek.app.inventory.infra.db.StockMovementType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementResponse {

    private Long id;

    private StockMovementType type;

    private StockMovementReference reference;

    private String notes;

    private String createdBy;

    private LocalDateTime createdAt;

    private List<StockMovementItemResponse> items;
}
