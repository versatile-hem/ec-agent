package com.ek.app.nexo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ek.app.nexo.entity.Product;

public interface NexoProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByBarcode(String barcode);

    boolean existsBySkuIgnoreCase(String sku);

    boolean existsByBarcodeIgnoreCase(String barcode);

    boolean existsBySkuIgnoreCaseAndIdNot(String sku, Long id);

    boolean existsByBarcodeIgnoreCaseAndIdNot(String barcode, Long id);
}
