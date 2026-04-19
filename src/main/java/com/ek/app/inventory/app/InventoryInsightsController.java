package com.ek.app.inventory.app;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ek.app.inventory.app.dto.ProductQuantityInsightDto;
import com.ek.app.inventory.app.dto.StockValueResponseDto;
import com.ek.app.inventory.domain.InventoryInsightsService;

@RestController
@RequestMapping("/api/inventory")
public class InventoryInsightsController {

    private final InventoryInsightsService inventoryInsightsService;

    public InventoryInsightsController(InventoryInsightsService inventoryInsightsService) {
        this.inventoryInsightsService = inventoryInsightsService;
    }

    @GetMapping("/low-stock")
    public List<ProductQuantityInsightDto> lowStock(
            @RequestParam(required = false) BigDecimal threshold) {
        return inventoryInsightsService.getLowStockProducts(threshold);
    }

    @GetMapping("/stock-value")
    public StockValueResponseDto stockValue() {
        return StockValueResponseDto.builder()
                .totalValue(inventoryInsightsService.getTotalStockValue())
                .build();
    }

    @GetMapping("/top-products")
    public List<ProductQuantityInsightDto> topProducts(
            @RequestParam(required = false, defaultValue = "5") int limit) {
        return inventoryInsightsService.getTopProducts(limit);
    }
}
