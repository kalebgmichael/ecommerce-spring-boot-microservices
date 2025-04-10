package com.productcnit.product.service.controller;


import com.productcnit.product.service.dto.ProductRequest;
import com.productcnit.product.service.dto.ProductResponse;
import com.productcnit.product.service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequest productRequest)
    {

        productService.createProduct(productRequest);
    }
    @GetMapping("/get")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts()
    {
       return productService.getAllProduct();

    }
    @GetMapping("/response")
    @ResponseStatus(HttpStatus.OK)
    public String getAllProducts1()
    {
        return "response from product-service";

    }
}
