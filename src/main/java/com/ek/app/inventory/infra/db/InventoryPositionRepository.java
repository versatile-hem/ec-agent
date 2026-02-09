package com.ek.app.inventory.infra.db;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ek.app.productcatalog.infra.db.Product;

@Repository
public interface InventoryPositionRepository extends JpaRepository<InventoryPosition, Long> {

        Optional<InventoryPosition> findByProduct(Product product);

        @Modifying
        @Query(value = """
                        UPDATE inventory_position
                        SET on_hand_qty = on_hand_qty + :stock
                        WHERE product_id = :productId
                        """, nativeQuery = true)
        void addOnHandQty(@Param("productId") Long productId,
                        @Param("stock") BigDecimal stock);

        @Modifying
        @Query(value = """
                        UPDATE inventory_position
                        SET on_hand_qty = on_hand_qty - :stock
                        WHERE product_id = :productId
                        """, nativeQuery = true)
        void removeOnHandQty(@Param("productId") Long productId,
                        @Param("stock") BigDecimal stock);
}
