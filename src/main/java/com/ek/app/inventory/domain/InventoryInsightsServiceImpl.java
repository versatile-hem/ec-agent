package com.ek.app.inventory.domain;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ek.app.inventory.app.dto.ProductQuantityInsightDto;
import com.ek.app.inventory.infra.db.InventoryPositionRepository;

@Service
public class InventoryInsightsServiceImpl implements InventoryInsightsService {

    private final InventoryPositionRepository inventoryPositionRepository;
    private final BigDecimal defaultLowStockThreshold;

    public InventoryInsightsServiceImpl(
            InventoryPositionRepository inventoryPositionRepository,
            @Value("${inventory.low-stock-threshold:10}") BigDecimal defaultLowStockThreshold) {
        this.inventoryPositionRepository = inventoryPositionRepository;
        this.defaultLowStockThreshold = defaultLowStockThreshold;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductQuantityInsightDto> getLowStockProducts(BigDecimal threshold) {
        BigDecimal effectiveThreshold = threshold == null ? defaultLowStockThreshold : threshold;
        return inventoryPositionRepository.findLowStockProducts(effectiveThreshold).stream()
                .map(row -> ProductQuantityInsightDto.builder()
                        .productId(row.getProductId())
                        .productName(row.getProductName())
                        .quantity(row.getQuantity())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    // Total stock value = SUM(product.mrp * inventory_position.on_hand_qty) across all products.
    // COALESCE in repository query ensures 0 is returned when there are no inventory rows.
    public BigDecimal getTotalStockValue() {
        return inventoryPositionRepository.getTotalStockValueByPrice();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductQuantityInsightDto> getTopProducts(int limit) {
        int effectiveLimit = limit <= 0 ? 5 : limit;
        return inventoryPositionRepository.findTopProductsByQuantity(PageRequest.of(0, effectiveLimit)).stream()
                .map(row -> ProductQuantityInsightDto.builder()
                        .productId(row.getProductId())
                        .productName(row.getProductName())
                        .quantity(row.getQuantity())
                        .build())
                .toList();
    }
}
