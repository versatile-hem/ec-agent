package com.ek.app.productcatalog.domain;

import java.util.List;

import com.ek.app.productcatalog.app.UpdateProductInput;
import com.ek.app.productcatalog.infra.db.Product;

public interface ProductService {

    Product getProductById(Long productId);

    Product createProduct(CreateProductInput input);

    List<Product> searchByName(String name);

    Product updateProduct(UpdateProductInput input);

    boolean deleteProduct(Long productId);

    Product getById(Long id);

    List<ProductDto> listAll();

    ProductDto addProduct(ProductDto product);

}
