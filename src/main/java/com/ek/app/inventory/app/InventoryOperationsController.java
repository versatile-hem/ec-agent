package com.ek.app.inventory.app;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ek.app.inventory.app.dto.DailyOperationRequestDto;
import com.ek.app.inventory.app.dto.EndOfDayBatchRequestDto;
import com.ek.app.inventory.app.dto.EndOfDayBatchResponseDto;
import com.ek.app.inventory.app.dto.InventoryBalanceResponseDto;
import com.ek.app.inventory.app.dto.StockInRequestDto;
import com.ek.app.inventory.domain.InventoryMovementDto;
import com.ek.app.inventory.domain.InventoryService;
import com.ek.app.inventory.domain.InventoryType;
import com.ek.app.inventory.infra.db.InventoryPosition;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class InventoryOperationsController {

    private final InventoryService inventoryService;

    public InventoryOperationsController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/stock-in")
    @ResponseStatus(HttpStatus.CREATED)
    public List<InventoryBalanceResponseDto> stockIn(@Valid @RequestBody List<StockInRequestDto> requests) {
        return requests.stream().map(request -> {
            InventoryMovementDto movement = new InventoryMovementDto();
            movement.setProductId(request.getProductId());
            movement.setMovementType(InventoryType.IN);
            movement.setQuantity(request.getQuantity());
            movement.setReference("supplier=" + request.getSupplier() + ",batch=" + request.getBatchNumber());
            movement.setMovementTime(request.getMovementTime() != null ? request.getMovementTime() : LocalDateTime.now());
            inventoryService.updateStock(movement);

            BigDecimal qty = inventoryService.findInventoryPosition(request.getProductId().intValue()).getOnHandQty();
            return InventoryBalanceResponseDto.builder()
                    .productId(request.getProductId())
                    .quantity(qty)
                    .build();
        }).toList();
    }

    @PostMapping("/daily-operations")
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryBalanceResponseDto dailyOperation(@Valid @RequestBody DailyOperationRequestDto request) {
        InventoryMovementDto movement = new InventoryMovementDto();
        movement.setProductId(request.getProductId());
        movement.setMovementType(request.getType() == DailyOperationRequestDto.OperationType.ORDER
                ? InventoryType.OUT
                : InventoryType.RETURN);
        movement.setQuantity(request.getQuantity());
        movement.setReference("courier=" + request.getCourier());
        movement.setSalesChannel(request.getChannel());
        movement.setMovementTime(request.getMovementTime() != null ? request.getMovementTime() : LocalDateTime.now());

        inventoryService.updateStock(movement);
        BigDecimal qty = inventoryService.findInventoryPosition(request.getProductId().intValue()).getOnHandQty();

        return InventoryBalanceResponseDto.builder()
                .productId(request.getProductId())
                .quantity(qty)
                .build();
    }

    @PostMapping("/end-of-day-operations")
    @ResponseStatus(HttpStatus.CREATED)
    public EndOfDayBatchResponseDto processEndOfDayOperations(
            @Valid @RequestBody EndOfDayBatchRequestDto batchRequest) {
        
        LocalDateTime processedAt = LocalDateTime.now();
        List<EndOfDayBatchResponseDto.OperationResultDto> results = new ArrayList<>();
        List<EndOfDayBatchResponseDto.InventoryFinalPositionDto> finalPositions = new ArrayList<>();
        
        int successfulCount = 0;
        int failedCount = 0;
        
        // Process each operation in the batch
        for (DailyOperationRequestDto operation : batchRequest.getOperations()) {
            Long productId = operation.getProductId();
            String productName = "Unknown";
            BigDecimal previousBalance = BigDecimal.ZERO;
            BigDecimal adjustmentQuantity = operation.getQuantity();
            
            try {
                // Store previous balance before update
                InventoryPosition prevPosition = inventoryService.findInventoryPosition(productId.intValue());
                previousBalance = prevPosition != null ? prevPosition.getOnHandQty() : BigDecimal.ZERO;
                
                // Get product name for response
                // Note: This would normally fetch from product repository, but using inventory service for now
                
                // Create and execute movement
                InventoryMovementDto movement = new InventoryMovementDto();
                movement.setProductId(productId);
                movement.setMovementType(operation.getType() == DailyOperationRequestDto.OperationType.ORDER
                        ? InventoryType.OUT
                        : InventoryType.RETURN);
                movement.setQuantity(adjustmentQuantity);
                movement.setReference("courier=" + operation.getCourier());
                movement.setSalesChannel(operation.getChannel());
                movement.setMovementTime(operation.getMovementTime() != null ? operation.getMovementTime() : LocalDateTime.now());
                
                // Execute stock update  
                inventoryService.updateStock(movement);
                
                // Get new balance after update (using fresh query, not cached)
                InventoryPosition newPosition = inventoryService.getLatestInventoryPosition(productId.intValue());
                BigDecimal newBalance = newPosition != null ? newPosition.getOnHandQty() : BigDecimal.ZERO;
                
                // Record success
                results.add(EndOfDayBatchResponseDto.OperationResultDto.builder()
                        .productId(productId)
                        .productName(productName)
                        .quantity(adjustmentQuantity)
                        .operationType(operation.getType().toString())
                        .success(true)
                        .processedTime(LocalDateTime.now())
                        .build());
                
                // Add final position
                finalPositions.add(EndOfDayBatchResponseDto.InventoryFinalPositionDto.builder()
                        .productId(productId)
                        .productName(productName)
                        .previousBalance(previousBalance)
                        .adjustmentQuantity(adjustmentQuantity)
                        .adjustmentType(movement.getMovementType().toString())
                        .newBalance(newBalance)
                        .build());
                
                successfulCount++;
                
            } catch (Exception e) {
                failedCount++;
                results.add(EndOfDayBatchResponseDto.OperationResultDto.builder()
                        .productId(productId)
                        .productName(productName)
                        .quantity(adjustmentQuantity)
                        .operationType(operation.getType().toString())
                        .success(false)
                        .errorMessage(e.getMessage() != null ? e.getMessage() : "Unknown error")
                        .processedTime(LocalDateTime.now())
                        .build());
            }
        }
        
        // Determine overall status
        String status;
        if (failedCount == 0) {
            status = "SUCCESS";
        } else if (successfulCount > 0) {
            status = "PARTIAL_SUCCESS";
        } else {
            status = "FAILED";
        }
        
        // Build and return response
        return EndOfDayBatchResponseDto.builder()
                .processedAt(processedAt)
                .totalOperationsProcessed(batchRequest.getOperations().size())
                .successfulOperations(successfulCount)
                .failedOperations(failedCount)
                .status(status)
                .results(results)
                .finalPositions(finalPositions)
                .build();
    }


    @GetMapping("/inventory/{productId:\\d+}")
    public InventoryBalanceResponseDto getInventory(@PathVariable Long productId) {
        BigDecimal qty = inventoryService.findInventoryPosition(productId.intValue()).getOnHandQty();
        return InventoryBalanceResponseDto.builder()
                .productId(productId)
                .quantity(qty)
                .build();
    }

    @GetMapping("/inventory")
    public InventoryBalanceResponseDto getInventoryByQuery(@RequestParam Long productId) {
        BigDecimal qty = inventoryService.findInventoryPosition(productId.intValue()).getOnHandQty();
        return InventoryBalanceResponseDto.builder()
                .productId(productId)
                .quantity(qty)
                .build();
    }
}
