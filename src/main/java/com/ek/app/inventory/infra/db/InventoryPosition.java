package com.ek.app.inventory.infra.db;

import java.math.BigDecimal;

import com.ek.app.productcatalog.db.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "inventory_position")
public class InventoryPosition {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id; // surrogate PK (allows nullable location/lot)

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;
 
	@Column(name = "on_hand_qty", precision = 18, scale = 6, nullable = false)
	private BigDecimal onHandQty = BigDecimal.ZERO;

	@Column(name = "reserved_qty", precision = 18, scale = 6, nullable = false)
	private BigDecimal reservedQty = BigDecimal.ZERO;
}
