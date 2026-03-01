package com.ek.app.productcatalog;

import java.util.List;

import com.ek.app.productcatalog.db.Product;
import com.ek.app.productcatalog.dtos.CreateProductInput;
import com.ek.app.productcatalog.dtos.UpdateProductInput;

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
