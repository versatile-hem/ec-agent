package com.ek.app.nexo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ek.app.nexo.dto.DailyOperationDTO;
import com.ek.app.nexo.dto.InventoryDTO;
import com.ek.app.nexo.service.DailyOperationService;

import jakarta.validation.Valid;

@RestController("nexoDailyOperationController")
@RequestMapping("/api/daily-operations")
public class DailyOperationController {

    private final DailyOperationService dailyOperationService;

    public DailyOperationController(DailyOperationService dailyOperationService) {
        this.dailyOperationService = dailyOperationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryDTO process(@Valid @RequestBody DailyOperationDTO request) {
        return dailyOperationService.process(request);
    }
}
