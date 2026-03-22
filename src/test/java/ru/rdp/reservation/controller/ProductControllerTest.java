package ru.rdp.reservation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.rdp.reservation.dto.ProductDto;
import ru.rdp.reservation.service.product.ProductService;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;


    @Test
    void getTopReservedProducts_Returns200AndList_WhenCalled() throws Exception {
        ProductDto product1 = new ProductDto(UUID.randomUUID(), "Product 1", 5);
        ProductDto product2 = new ProductDto(UUID.randomUUID(), "Product 2", 10);
        when(productService.getTopReservedProducts()).thenReturn(List.of(product1, product2));

        mockMvc.perform(get("/products/top-reserved")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[1].name").value("Product 2"));
    }
}