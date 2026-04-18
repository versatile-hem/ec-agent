package com.ek.app.productcatalog;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ek.app.productcatalog.db.Product;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/products")
@Tag(name = "User API", description = "Product management endpoints")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // 1️⃣ Add Product
    @PostMapping
    public ProductDto addProduct(@RequestBody ProductDto product) {
        return service.addProduct(product);
    }

    // 2️⃣ Get Product by ID
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return service.getProductById(id);
    }

    // 3️⃣ Search Product by Name
    @GetMapping("/search")
    public List<Product> searchProduct(@RequestParam String name) {
        return service.searchByName(name);
    }
    
 // 3️⃣ Search Product by Name
    @PostMapping("/list")
    public List<Product> listProduct(@RequestParam String name) {
        return service.searchByName(name);
    }
}
