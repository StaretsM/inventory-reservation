package ru.rdp.reservation.controller;

import org.springframework.web.bind.annotation.RestController;
import ru.rdp.reservation.dto.ProductDto;
import ru.rdp.reservation.service.product.ProductService;

import java.util.List;
import java.util.UUID;

@RestController
public class ProductControllerImpl implements ProductController{
    private final ProductService productService;

    public ProductControllerImpl(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public ProductDto getProductById(UUID id) {
        return productService.getProductById(id);
    }

    @Override
    public List<ProductDto> getTopReservedProducts() {
        return productService.getTopReservedProducts();
    }
}
