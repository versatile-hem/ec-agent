package com.ek.app.productcatalog.infra.db;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "bom", uniqueConstraints = @UniqueConstraint(name = "uq_bom_finished_version", columnNames = {
		"finished_product_id", "version" }))
public class Bom extends BaseAuditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "bom_id", updatable = false, nullable = false)
	private Long bomId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "finished_product_id", nullable = false)
	private Product finishedProduct;

	@Column(name = "version", length = 16, nullable = false)
	private String version = "v1";

	@Column(name = "is_active", nullable = false)
	private boolean active = true;

	@OneToMany(mappedBy = "bom", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<BomItem> items = new ArrayList<>();
}
