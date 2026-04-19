package com.ek.app.nexo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ek.app.nexo.dto.InventoryDTO;
import com.ek.app.nexo.service.InventoryService;

@RestController("nexoInventoryController")
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{productId}")
    public List<InventoryDTO> getByProduct(@PathVariable Long productId) {
        return inventoryService.getInventoryByProduct(productId);
    }

    @GetMapping
    public List<InventoryDTO> getByProductFromQuery(@RequestParam Long productId) {
        return inventoryService.getInventoryByProduct(productId);
    }
}
