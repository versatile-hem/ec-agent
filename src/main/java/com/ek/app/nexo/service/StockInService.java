package com.ek.app.nexo.service;

import java.util.List;

import com.ek.app.nexo.dto.InventoryDTO;
import com.ek.app.nexo.dto.StockInRequestDTO;

public interface StockInService {

    List<InventoryDTO> stockIn(List<StockInRequestDTO> requests);
}
