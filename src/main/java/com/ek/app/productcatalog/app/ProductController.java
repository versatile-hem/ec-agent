package com.ek.app.productcatalog.app;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ek.app.productcatalog.domain.ProductDto;
import com.ek.app.productcatalog.domain.ProductService;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/products")
@Tag(name = "User API", description = "Product management endpoints")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductDto> getAllProducts() {
        return service.listAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto addProduct(@Valid @RequestBody ProductDto product) {
        return service.addProduct(product);
    }

    @GetMapping("/{id}")
    public ProductDto getProduct(@PathVariable Long id) {
        return service.getProductDtoById(id);
    }

    @GetMapping("/barcode/{barcode}")
    public ProductDto getByBarcode(@PathVariable String barcode) {
        return service.getProductByBarcode(barcode);
    }

    @PutMapping("/{id}")
    public ProductDto update(@PathVariable Long id, @Valid @RequestBody ProductDto product) {
        return service.updateProduct(id, product);
    }

    @GetMapping("/search")
    public List<?> searchProduct(@RequestParam String name) {
        return service.searchByName(name);
    }
}
