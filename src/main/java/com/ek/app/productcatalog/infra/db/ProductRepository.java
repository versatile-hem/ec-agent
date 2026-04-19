package com.ek.app.productcatalog.infra.db;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    Optional<Product> findBySkuIgnoreCase(String sku);

    Optional<Product> findByBarcode(String barcode);

    @Query("""
            select p
            from Product p
            where lower(p.name) = lower(:name)
               or lower(p.product_title) = lower(:name)
            """)
    List<Product> findByNameOrTitleExactIgnoreCase(@Param("name") String name);
}
