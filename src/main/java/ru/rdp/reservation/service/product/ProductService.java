package ru.rdp.reservation.service.product;

import ru.rdp.reservation.dto.ProductDto;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    /**
     * Получение информации о продукте по его ID.
     */
    ProductDto getProductById(UUID id);

    /**
     * Возвращает топ-5 продуктов по количеству подтверждённых резервов за последние 24 часа.
     */
    List<ProductDto> getTopReservedProducts();
}
