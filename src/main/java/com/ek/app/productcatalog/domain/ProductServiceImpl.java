package com.ek.app.productcatalog.domain;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ek.app.inventory.infra.db.InventoryPosition;
import com.ek.app.inventory.infra.db.InventoryPositionRepository;
import com.ek.app.productcatalog.app.UpdateProductInput;
import com.ek.app.productcatalog.infra.db.Product;
import com.ek.app.productcatalog.infra.db.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private  ProductRepository repository;

	@Autowired
	private InventoryPositionRepository inventoryPostion;

	public void ProductService(ProductRepository repository) {
		this.repository = repository;
	}

	@Override
	public ProductDto addProduct(ProductDto product) {
		Product saved = repository.save(dtoToEntity(product, new Product()));
		return entityToDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public ProductDto getProductDtoById(Long productId) {
		Product product = repository.findById(productId)
				.orElseThrow(() -> new RuntimeException("Product not found"));
		return entityToDto(product);
	}

	@Override
	@Transactional(readOnly = true)
	public ProductDto getProductByBarcode(String barcode) {
		Product product = repository.findByBarcode(barcode)
				.orElseThrow(() -> new RuntimeException("Product not found for barcode: " + barcode));
		return entityToDto(product);
	}

	@Override
	@Transactional
	public ProductDto updateProduct(Long productId, ProductDto product) {
		Product existing = repository.findById(productId)
				.orElseThrow(() -> new RuntimeException("Product not found"));

		Product updated = dtoToEntity(product, existing);
		updated.setProductId(existing.getProductId());
		return entityToDto(repository.save(updated));
	}

	@Override
	public Product getProductById(Long productId) {
		return repository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
	}

	@Override
	public List<Product> searchByName(String name) {
		return repository.findByNameContainingIgnoreCase(name);
	}

	@Override
	public Product createProduct(CreateProductInput input) {
		Product p = new Product();
		// If you want to generate server-side:
		// p.setProductId(Long.randomLong());
		// p.setProductId(input.getProductId());
		p.setName(input.getName());
	//	p.setDescription(input.getDescription());
	//	p.setBaseUnit(input.getBaseUnit());
		// createdAt is set in @PrePersist
		return repository.save(p);
	}

	@Override
	public Product updateProduct(UpdateProductInput input) {
		Product existing = repository.findById(input.getProductId())
				.orElseThrow(() -> new IllegalArgumentException("Product not found: " + input.getProductId()));

		if (input.getName() != null)
			existing.setName(input.getName());
		/*if (input.getDescription() != null)
			existing.setDescription(input.getDescription());
		if (input.getBaseUnit() != null)
			existing.setBaseUnit(input.getBaseUnit()); */

		return repository.save(existing);
	}

	@Override
	public boolean deleteProduct(Long productId) {
		if (!repository.existsById(productId))
			return false;
		repository.deleteById(productId);
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public Product getById(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProductDto> listAll() {
		return repository.findAll()
                .stream()
                .map(this::entityToDto)
                .toList();
 
	}

	 
	public ProductDto entityToDto(Product product) {
		ProductDto dto = new ProductDto();
		dto.setProductId(product.getProductId());
		dto.setName(product.getName());
		dto.setSku(product.getSku());
		dto.setProduct_title(product.getProduct_title());
		dto.setBarcode(product.getBarcode());
		dto.setCategory(product.getCategory());
		dto.setHsn(product.getHsn());
		dto.setHsnCode(product.getHsn());
		dto.setTax_code(product.getTax_code());
		dto.setWeightGrams(product.getWeightGrams());
		dto.setMrp(product.getMrp());
		dto.setPrice(product.getMrp());
		dto.setCreatedAt(product.getCreatedAt());
		dto.setUpdatedAt(product.getUpdatedAt());

		InventoryPosition inventoryPosition = inventoryPostion.findByProduct(product).orElse(null);
		if(inventoryPosition != null) {
			dto.setAvailableStock(inventoryPosition.getOnHandQty().longValue()	);
		}
		return dto;
	}

	public Product dtoToEntity(ProductDto dto, Product entity) {
		entity.setName(dto.getName() != null ? dto.getName() : dto.getProduct_title());
		entity.setSku(dto.getSku());
		entity.setProduct_title(dto.getProduct_title() != null ? dto.getProduct_title() : dto.getName());
		entity.setBarcode(dto.getBarcode());
		entity.setCategory(dto.getCategory());
		entity.setHsn(dto.getHsnCode() != null ? dto.getHsnCode() : dto.getHsn());
		entity.setTax_code(dto.getTax_code());
		entity.setWeightGrams(dto.getWeightGrams());
		entity.setMrp(dto.getPrice() != null ? dto.getPrice() : dto.getMrp());
		return entity;
	}

}