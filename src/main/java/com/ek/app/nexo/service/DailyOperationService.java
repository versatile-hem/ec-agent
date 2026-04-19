package com.ek.app.nexo.service;

import com.ek.app.nexo.dto.DailyOperationDTO;
import com.ek.app.nexo.dto.InventoryDTO;

public interface DailyOperationService {

    InventoryDTO process(DailyOperationDTO request);
}
