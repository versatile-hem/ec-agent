package com.ek.app.inventory.domain;

import java.math.BigDecimal;
import java.util.List;

import com.ek.app.inventory.app.dto.ProductQuantityInsightDto;

public interface InventoryInsightsService {

    List<ProductQuantityInsightDto> getLowStockProducts(BigDecimal threshold);

    BigDecimal getTotalStockValue();

    List<ProductQuantityInsightDto> getTopProducts(int limit);
}
