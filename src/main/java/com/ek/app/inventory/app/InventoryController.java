package com.ek.app.inventory.app;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ek.app.inventory.domain.InventoryMovementDto;
import com.ek.app.inventory.domain.InventoryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/v1/inventory")
@Validated
public class InventoryController {

@Autowired
private InventoryService inventoryService;

     

    // ------------------- Create -------------------
    @PostMapping
    public ResponseEntity<InventoryMovementDto> add(@Valid @RequestBody InventoryMovementDto req) {
        InventoryMovementDto created = inventoryService.addInventory(req);
        return ResponseEntity.created(URI.create("/api/v1/inventory/" + created.getId())).body(created);
    }

    // ------------------- Update (partial) -------------------
    @PatchMapping("/{id}")
    public InventoryMovementDto update(@PathVariable String id, @Valid @RequestBody InventoryMovementDto req) {
        return inventoryService.updateInventory(req);
    }

    // ------------------- Read -------------------
    @GetMapping("/{id}")
    public InventoryMovementDto getById(@PathVariable String id) {
        return inventoryService.getById(id);
    }

    @GetMapping
    public List<InventoryMovementDto> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        return inventoryService.listAll(page, size);
    }

    @GetMapping("/search")
    public List<InventoryMovementDto> search(
            @RequestParam(required = false) String skuLike,
            @RequestParam(required = false) String nameLike,
            @RequestParam(required = false) String locationLike,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
        return inventoryService.search(skuLike, nameLike, locationLike, active, page, size);
    }

    // ------------------- Delete -------------------
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        inventoryService.delete(id);
    }
 

     

}
