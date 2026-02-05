package com.ek.app.inventory.domain;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ek.app.inventory.infra.db.InventoryMovement;
import com.ek.app.inventory.infra.db.InventoryMovementRepository;
import com.ek.app.inventory.infra.db.InventoryPosition;
import com.ek.app.inventory.infra.db.InventoryPositionRepository;
import com.ek.app.productcatalog.db.ProductRepository;

import jakarta.transaction.Transactional;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;

    private final InventoryPositionRepository inventoryPositionRepository;

    @Autowired
    private InventoryMovementRepository inventoryRepository;

    InventoryServiceImpl(InventoryPositionRepository inventoryPositionRepository, ProductRepository productRepository) {
        this.inventoryPositionRepository = inventoryPositionRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public InventoryMovementDto addInventory(InventoryMovementDto itemDto) {

        InventoryMovement saved = inventoryRepository.save(toEntity(itemDto));
        return toDto(saved);
    }

    private InventoryMovementDto toDto(InventoryMovement i) {
        InventoryMovementDto dto = new InventoryMovementDto();
        BeanUtils.copyProperties(i, dto);
        return dto;
    }

    private InventoryMovement toEntity(InventoryMovementDto i) {
        InventoryMovement entity = new InventoryMovement();
        BeanUtils.copyProperties(i, entity);
        return entity;
    }

    @Override
    public InventoryMovementDto updateInventory(InventoryMovementDto itemDto) {
        throw new UnsupportedOperationException("Unimplemented method 'updateInventory'");
    }

    @Override
    public InventoryMovementDto getById(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'getById'");
    }

    @Override
    public List<InventoryMovementDto> listAll(int page, int size) {
        return this.inventoryRepository.findAll()
                .stream().map(this::entityToDto).toList();
    }

    @Override
    public List<InventoryMovementDto> search(String skuLike, String nameLike, String locationLike, Boolean active,
            int page,
            int size) {
        throw new UnsupportedOperationException("Unimplemented method 'search'");
    }

    @Override
    public void delete(String id) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public InventoryMovementDto adjustStock(String id, int delta) {
        throw new UnsupportedOperationException("Unimplemented method 'adjustStock'");
    }

    @Override
    public InventoryMovementDto reserveStock(String id, int qty) {
        throw new UnsupportedOperationException("Unimplemented method 'reserveStock'");
    }

    @Override
    public InventoryMovementDto releaseReserved(String id, int qty) {
        throw new UnsupportedOperationException("Unimplemented method 'releaseReserved'");
    }

    @Override
    public void transferStock(String fromId, String toId, int qty) {
        throw new UnsupportedOperationException("Unimplemented method 'transferStock'");
    }

    @Override
    public InventoryMovementDto upsertBySkuLocation(String sku, String name, String location, int qtyDelta, String uom,
            String metadata) {
        throw new UnsupportedOperationException("Unimplemented method 'upsertBySkuLocation'");
    }

    @Override
    @Transactional
    public void updateStock(InventoryMovementDto movementDto, Long productId, Integer qty, InventoryType type) {
        movementDto.setProductId(productId);
        this.inventoryRepository.save(dtoToEntity(movementDto));
        switch (type) {
            case IN:
                this.inventoryPositionRepository.addOnHandQty(productId, qty);
                break;
            case OUT:
                this.inventoryPositionRepository.removeOnHandQty(productId, qty);
            case DAMAGE:
                this.inventoryPositionRepository.removeOnHandQty(productId, qty);
            case RETURN:
                this.inventoryPositionRepository.addOnHandQty(productId, qty);
            case ADJUST:
                this.inventoryPositionRepository.addOnHandQty(productId, qty); 
                break;
            default:
                break;
        }
    }

    private InventoryMovementDto entityToDto(InventoryMovement InventoryMovement) {
        InventoryMovementDto dto = new InventoryMovementDto();
        BeanUtils.copyProperties(InventoryMovement, dto);
        return dto;

    }

    private InventoryMovement dtoToEntity(InventoryMovementDto InventoryMovementDto) {
        if (InventoryMovementDto.getProductId() == null) {
            throw new RuntimeException("Product Id missing");
        }
        InventoryMovement inventoryMovement = new InventoryMovement();
        BeanUtils.copyProperties(InventoryMovementDto, inventoryMovement);
        inventoryMovement.setProduct(this.productRepository.findById(InventoryMovementDto.getProductId()).get());
        return inventoryMovement;
    }

    @Override
    public InventoryPosition findInventoryPosition(int product_id) {
        throw new UnsupportedOperationException("Unimplemented method 'findInventoryPosition'");
    }

    @Override
    public List<InventoryMovementDto> searchStockFlow(Long product_id) {
        org.springframework.data.domain.Pageable pageable = Pageable.ofSize(10);
        Page<InventoryMovement> page = this.inventoryRepository.findByProduct_Id(product_id, pageable);
        return page.get().map(e -> {
            InventoryMovementDto dto = new InventoryMovementDto();
            BeanUtils.copyProperties(e, dto);
            return dto;
        }).toList();
    }




}
