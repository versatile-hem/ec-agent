package com.ek.app.nexo.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ek.app.nexo.dto.ProductDTO;
import com.ek.app.nexo.entity.Product;
import com.ek.app.nexo.exception.ResourceNotFoundException;
import com.ek.app.nexo.repository.NexoProductRepository;
import com.ek.app.nexo.service.ProductService;

@Service("nexoProductServiceImpl")
public class ProductServiceImpl implements ProductService {

    private final NexoProductRepository productRepository;

    public ProductServiceImpl(NexoProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ProductDTO create(ProductDTO request) {
        validateUnique(request.getSku(), request.getBarcode(), null);
        Product entity = toEntity(request, new Product());
        return toDto(productRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getAll() {
        return productRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getById(Long id) {
        return toDto(findProduct(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getByBarcode(String barcode) {
        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for barcode: " + barcode));
        return toDto(product);
    }

    @Override
    @Transactional
    public ProductDTO update(Long id, ProductDTO request) {
        Product existing = findProduct(id);
        validateUnique(request.getSku(), request.getBarcode(), id);
        Product updated = toEntity(request, existing);
        return toDto(productRepository.save(updated));
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    private void validateUnique(String sku, String barcode, Long id) {
        boolean skuExists = id == null
                ? productRepository.existsBySkuIgnoreCase(sku)
                : productRepository.existsBySkuIgnoreCaseAndIdNot(sku, id);
        if (skuExists) {
            throw new IllegalArgumentException("SKU already exists: " + sku);
        }

        boolean barcodeExists = id == null
                ? productRepository.existsByBarcodeIgnoreCase(barcode)
                : productRepository.existsByBarcodeIgnoreCaseAndIdNot(barcode, id);
        if (barcodeExists) {
            throw new IllegalArgumentException("Barcode already exists: " + barcode);
        }
    }

    private Product toEntity(ProductDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setSku(dto.getSku());
        entity.setBarcode(dto.getBarcode());
        entity.setHsnCode(dto.getHsnCode());
        entity.setUnit(dto.getUnit());
        entity.setPrice(dto.getPrice());
        return entity;
    }

    private ProductDTO toDto(Product entity) {
        ProductDTO dto = new ProductDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSku(entity.getSku());
        dto.setBarcode(entity.getBarcode());
        dto.setHsnCode(entity.getHsnCode());
        dto.setUnit(entity.getUnit());
        dto.setPrice(entity.getPrice());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
