package com.ek.app.nexo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ek.app.nexo.dto.InventoryDTO;
import com.ek.app.nexo.dto.StockInRequestDTO;
import com.ek.app.nexo.service.StockInService;

import jakarta.validation.Valid;

@RestController("nexoStockInController")
@RequestMapping("/api/stock-in")
public class StockInController {

    private final StockInService stockInService;

    public StockInController(StockInService stockInService) {
        this.stockInService = stockInService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<InventoryDTO> stockIn(@Valid @RequestBody List<StockInRequestDTO> request) {
        return stockInService.stockIn(request);
    }
}
