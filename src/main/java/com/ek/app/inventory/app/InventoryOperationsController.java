package com.ek.app.inventory.app;

import java.math.BigDecimal;
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
import com.ek.app.inventory.app.dto.InventoryBalanceResponseDto;
import com.ek.app.inventory.app.dto.StockInRequestDto;
import com.ek.app.inventory.domain.InventoryMovementDto;
import com.ek.app.inventory.domain.InventoryService;
import com.ek.app.inventory.domain.InventoryType;

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

        inventoryService.updateStock(movement);
        BigDecimal qty = inventoryService.findInventoryPosition(request.getProductId().intValue()).getOnHandQty();

        return InventoryBalanceResponseDto.builder()
                .productId(request.getProductId())
                .quantity(qty)
                .build();
    }

    @GetMapping("/inventory/{productId}")
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
