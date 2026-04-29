package ru.rdp.reservation.service.product;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rdp.reservation.dto.ProductDto;
import ru.rdp.reservation.exception.product.ProductNotFoundException;
import ru.rdp.reservation.model.Product;
import ru.rdp.reservation.model.ReservationStatus;
import ru.rdp.reservation.repository.ProductRepository;
import ru.rdp.reservation.repository.ReservationRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;

    public ProductServiceImpl(ProductRepository productRepository, ReservationRepository reservationRepository) {
        this.productRepository = productRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public ProductDto getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("Failed to get product details: Product with ID '%s' not found", id)
                ));

        return mapToDto(product);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProductDto> getTopReservedProducts() {
        Instant sinceTime = Instant.now().minus(24, ChronoUnit.HOURS);
        Pageable limit =  PageRequest.of(0, 10);
        List<Product> topReserved = reservationRepository.findTopReserved(sinceTime, limit, ReservationStatus.CONFIRMED);

        return topReserved.stream()
                .map(this::mapToDto)
                .toList();
    }

    private ProductDto mapToDto (Product product){
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getStock()
        );
    }
}
