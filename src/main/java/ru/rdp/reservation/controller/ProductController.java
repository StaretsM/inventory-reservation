package ru.rdp.reservation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.rdp.reservation.dto.ProductDto;

import java.util.List;
import java.util.UUID;

@RequestMapping("/products")
public interface ProductController {

    @GetMapping("/{id}")
    ProductDto getProductById(
            @PathVariable UUID id
    );

    @GetMapping("/top-reserved")
    List<ProductDto> getTopReservedProducts();
}
