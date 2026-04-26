package com.ek.app.inventory.app.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * End-of-Day batch operations request
 * Purpose: Process multiple inventory operations at end of day
 * 
 * Updates:
 * - inventory_movement table (tracks all movements)
 * - inventory_position table (final balances)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndOfDayBatchRequestDto {

    @NotEmpty(message = "At least one operation is required")
    @Valid
    private List<DailyOperationRequestDto> operations;

    private String notes; // Optional batch notes (e.g., "Daily settlement", "Reconciliation")
}
