package ru.rdp.reservation.service.reservation;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import ru.rdp.reservation.config.ReservationProperties;
import ru.rdp.reservation.dto.CreateReservationRequest;
import ru.rdp.reservation.dto.ReservationResponse;
import ru.rdp.reservation.exception.product.InsufficientStockException;
import ru.rdp.reservation.exception.product.ProductNotFoundException;
import ru.rdp.reservation.exception.reservation.InvalidReservationStatusException;
import ru.rdp.reservation.exception.reservation.ReservationNotFoundException;
import ru.rdp.reservation.exception.reservation.ReservationTimeExpiredException;
import ru.rdp.reservation.model.Product;
import ru.rdp.reservation.model.Reservation;
import ru.rdp.reservation.model.ReservationStatus;
import ru.rdp.reservation.repository.ProductRepository;
import ru.rdp.reservation.repository.ReservationRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final ProductRepository productRepository;
    private final ReservationProperties reservationProperties;

    public ReservationServiceImpl(
            ReservationRepository reservationRepository,
            ProductRepository productRepository,
            ReservationProperties reservationProperties) {
        this.reservationRepository = reservationRepository;
        this.productRepository = productRepository;
        this.reservationProperties = reservationProperties;
    }

    @Transactional
    @Override
    public ReservationResponse createReservation(CreateReservationRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException(
                        String.format("Failed to create reservation: Product with ID '%s' not found",
                                request.productId())
                ));

        if (product.getStock() < request.quantity()) {
            throw new InsufficientStockException(
                    String.format("Cannot reserve product '%s'. Requested: %d, Available: %d",
                        product.getId(), request.quantity(), product.getStock()));
        }

        Reservation reservation = new Reservation();
        reservation.setProduct(product);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setQuantity(request.quantity());

        Instant now = Instant.now();
        Instant expiresAt = now.plus(reservationProperties.expirationMinutes(), ChronoUnit.MINUTES);

        reservation.setCreatedAt(now);
        reservation.setExpiresAt(expiresAt);

        Reservation savedReservation = reservationRepository.saveAndFlush(reservation);

        return mapToDto(savedReservation);
    }

    @Transactional(noRollbackFor = ReservationTimeExpiredException.class)
    @Override
    public ReservationResponse confirmReservation(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(
                        String.format("Failed to confirm: Reservation with ID '%s' not found", reservationId)));

        if(!reservation.getStatus().equals(ReservationStatus.ACTIVE)) {
            throw new InvalidReservationStatusException(
                    String.format("Cannot confirm reservation '%s': expected status ACTIVE, but was %s",
                            reservationId, reservation.getStatus())
            );
        }

        if(reservation.getExpiresAt().isBefore(Instant.now())) {
            reservation.setStatus(ReservationStatus.EXPIRED);
            reservationRepository.saveAndFlush(reservation);

            throw new ReservationTimeExpiredException(
                    String.format("Failed to confirm: Reservation '%s' already expired at %s",
                    reservationId, reservation.getExpiresAt()));
        }

        Product product = reservation.getProduct();
        cleanupExpiredReservations(product);

        if(product.getStock() - reservation.getQuantity() < 0){
            throw new InsufficientStockException(
                    String.format("Cannot confirm reservation '%s': " +
                                    "insufficient stock for product '%s'. Required: %d, Available: %d",
                            reservationId, product.getId(), reservation.getQuantity(), product.getStock())
            );
        }

        product.setStock(product.getStock() - reservation.getQuantity());
        reservation.setStatus(ReservationStatus.CONFIRMED);

        Reservation savedReservation = reservationRepository.saveAndFlush(reservation);

        return mapToDto(savedReservation);
    }

    private void cleanupExpiredReservations(Product product){
        List<Reservation> expiredReservation =
                reservationRepository.findByProductIdAndStatusAndExpiresAtBefore(
                        product.getId(),
                        ReservationStatus.ACTIVE,
                        Instant.now()
                );

        if(expiredReservation.isEmpty()){ return;}

        for(Reservation reservation : expiredReservation){
            reservation.setStatus(ReservationStatus.EXPIRED);
        }
    }

    private ReservationResponse mapToDto(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getProduct().getId(),
                reservation.getQuantity(),
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getExpiresAt()
        );
    }
}
