package com.ek.app.inventory.app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * End-of-Day batch operations response
 * Contains summary of processed operations and final inventory positions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndOfDayBatchResponseDto {

    private LocalDateTime processedAt;
    
    private int totalOperationsProcessed;
    
    private int successfulOperations;
    
    private int failedOperations;
    
    private List<OperationResultDto> results;
    
    private List<InventoryFinalPositionDto> finalPositions;
    
    private String status; // SUCCESS, PARTIAL_SUCCESS, FAILED
    
    /**
     * Individual operation result
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperationResultDto {
        private Long productId;
        private String productName;
        private BigDecimal quantity;
        private String operationType; // ORDER or RETURN
        private boolean success;
        private String errorMessage;
        private LocalDateTime processedTime;
    }
    
    /**
     * Final inventory position after all operations
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryFinalPositionDto {
        private Long productId;
        private String productName;
        private BigDecimal previousBalance;
        private BigDecimal adjustmentQuantity;
        private String adjustmentType; // IN or OUT
        private BigDecimal newBalance;
        private Long movementId;
    }
}
