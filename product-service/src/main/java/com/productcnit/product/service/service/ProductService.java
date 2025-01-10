package com.productcnit.product.service.service;

import com.productcnit.product.service.dto.ProductRequest;
import com.productcnit.product.service.dto.ProductResponse;
import com.productcnit.product.service.model.Product;
import com.productcnit.product.service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    public void createProduct(ProductRequest productRequest)
    {
        Product product = Product.builder() // mapping the productRequest to product
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build(); //create instance of product

        productRepository.save(product);
        log.info("product {} is saved",product.getId());

    }

    public List<ProductResponse> getAllProduct() {
       List<Product> products =  productRepository.findAll();

      return products.stream().map(this::mapToProductResponse).toList();
        //map productRepository
        // to product
    }

    private  ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
