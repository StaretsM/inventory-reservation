package ru.rdp.reservation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.rdp.reservation.dto.ProductDto;
import ru.rdp.reservation.exception.product.ProductNotFoundException;
import ru.rdp.reservation.model.Product;
import ru.rdp.reservation.model.ReservationStatus;
import ru.rdp.reservation.repository.ProductRepository;
import ru.rdp.reservation.repository.ReservationRepository;
import ru.rdp.reservation.service.product.ProductServiceImpl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();

        testProduct = new Product();
        testProduct.setId(productId);
        testProduct.setName("Test Product");
        testProduct.setStock(50);
    }

    @Test
    void getProductById_ReturnsProductDto_WhenProductExists() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        ProductDto result = productService.getProductById(productId);

        assertNotNull(result);
        assertEquals(productId, result.id());
        assertEquals("Test Product", result.name());
        assertEquals(50, result.stock());

        verify(productRepository, times(1)).findById(productId);
    }


    @Test
    void getProductById_ThrowsProductNotFoundException_WhenProductDoesNotExist() {
        UUID wrongId = UUID.randomUUID();
        when(productRepository.findById(wrongId)).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
                () -> productService.getProductById(wrongId));

        assertTrue(exception.getMessage().contains("Failed to get product details"));
        assertTrue(exception.getMessage().contains(wrongId.toString()));
    }

    @Test
    void getTopReservedProducts_ReturnsListOfProductDto_WhenCalled() {
        Product secondProduct = new Product();
        secondProduct.setId(UUID.randomUUID());
        secondProduct.setName("Test Product 2");
        secondProduct.setStock(100);

        List<Product> mockProducts = List.of(testProduct, secondProduct);

        when(reservationRepository.findTopReserved(
                any(Instant.class),
                any(Pageable.class),
                eq(ReservationStatus.CONFIRMED)
        )).thenReturn(mockProducts);

        List<ProductDto> result = productService.getTopReservedProducts();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("Test Product", result.get(0).name());
        assertEquals("Test Product 2", result.get(1).name());

        verify(reservationRepository, times(1)).findTopReserved(any(), any(), any());
    }
}
