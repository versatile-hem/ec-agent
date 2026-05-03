package com.ek.app.inventory.app.dto;

import java.util.List;

import com.ek.app.inventory.infra.db.StockMovementReference;
import com.ek.app.inventory.infra.db.StockMovementType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStockMovementRequest {

    @NotNull(message = "Type (IN/OUT) is required")
    private StockMovementType type;

    @NotNull(message = "Reference is required")
    private StockMovementReference reference;

    private String notes;

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<StockMovementItemRequest> items;
}
