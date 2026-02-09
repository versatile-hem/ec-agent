package com.ek.app.productcatalog.app;

import java.util.List;
import java.util.UUID;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.ek.app.productcatalog.domain.CreateProductInput;
import com.ek.app.productcatalog.domain.ProductDto;
import com.ek.app.productcatalog.domain.ProductService;
import com.ek.app.productcatalog.infra.db.Product;

@Controller
public class ProductGraphQLController {
	

private final ProductService service;

    public ProductGraphQLController(ProductService service) {
        this.service = service;
    }

    @QueryMapping
    public Product productById(@Argument Long productId) {
        return service.getById(productId);
    }

    @QueryMapping
    public List<ProductDto> products() {
        return service.listAll();
    }

    @MutationMapping
    public Product createProduct(@Argument CreateProductInput input) {
        return service.createProduct(input);
    }

    @MutationMapping
    public Product updateProduct(@Argument UpdateProductInput input) {
        return service.updateProduct(input);
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument Long productId) {
        return service.deleteProduct(productId);
    }


}
