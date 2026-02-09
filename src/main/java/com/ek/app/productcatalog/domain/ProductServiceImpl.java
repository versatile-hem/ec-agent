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
import com.vaadin.flow.data.provider.DataProvider;

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
       Product saved = repository.save(dtoToEntity(product));
		return entityToDto(saved);
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
		BeanUtils.copyProperties(product
		, dto);

		InventoryPosition inventoryPosition = inventoryPostion.findByProduct(product).orElse(null);
		if(inventoryPosition != null) {
			dto.setAvailableStock(inventoryPosition.getOnHandQty().longValue()	);
		}
		return dto;
	}

	public Product dtoToEntity(ProductDto dto) {
		Product entity = new Product();
		BeanUtils.copyProperties(dto
		, entity);
		return entity;
	}

    public DataProvider<Product, Void> findAll() {
        
		throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }


	


}