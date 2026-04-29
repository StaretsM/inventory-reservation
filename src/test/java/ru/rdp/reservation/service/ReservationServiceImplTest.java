package ru.rdp.reservation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.rdp.reservation.config.ReservationProperties;
import ru.rdp.reservation.dto.CreateReservationRequest;
import ru.rdp.reservation.dto.ReservationResponse;
import ru.rdp.reservation.exception.product.InsufficientStockException;
import ru.rdp.reservation.exception.reservation.InvalidReservationStatusException;
import ru.rdp.reservation.exception.reservation.ReservationTimeExpiredException;
import ru.rdp.reservation.model.Product;
import ru.rdp.reservation.model.Reservation;
import ru.rdp.reservation.model.ReservationStatus;
import ru.rdp.reservation.repository.ProductRepository;
import ru.rdp.reservation.repository.ReservationRepository;
import ru.rdp.reservation.service.reservation.ReservationServiceImpl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ReservationProperties reservationProperties;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Product product;
    private Reservation reservation;
    private final UUID productId = UUID.randomUUID();
    private final UUID reservationId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(productId);
        product.setStock(10);
        product.setName("Test name");

        reservation = new Reservation();
        reservation.setId(reservationId);
        reservation.setProduct(product);
        reservation.setQuantity(3);
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setCreatedAt(Instant.now());
        reservation.setExpiresAt(Instant.now().plus(10, ChronoUnit.MINUTES));
    }

    @Test
    void createReservation_ReturnsResponse_WhenValidRequest() {
        CreateReservationRequest request = new CreateReservationRequest(productId, 3);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(reservationProperties.expirationMinutes()).thenReturn(10);
        when(reservationRepository.saveAndFlush(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponse response = reservationService.createReservation(request);

        assertNotNull(response);
        assertEquals(ReservationStatus.ACTIVE, response.status());
        assertEquals(3, response.quantity());
        verify(reservationRepository, times(1)).saveAndFlush(any(Reservation.class));
    }

    @Test
    void createReservation_ThrowsInsufficientStockException_WhenNotEnoughStock() {
        CreateReservationRequest request = new CreateReservationRequest(productId, 15);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class,
                () -> reservationService.createReservation(request));
        verify(reservationRepository, never()).saveAndFlush(any());
    }

    @Test
    void confirmReservation_ThrowsTimeExpiredException_WhenTimeIsUp() {
        reservation.setExpiresAt(Instant.now().minus(5, ChronoUnit.MINUTES));

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        assertThrows(ReservationTimeExpiredException.class,
                () -> reservationService.confirmReservation(reservationId));

        assertEquals(ReservationStatus.EXPIRED, reservation.getStatus());
        verify(reservationRepository, times(1)).saveAndFlush(reservation);
    }

    @Test
    void confirmReservation_ThrowsInvalidStatusException_WhenAlreadyConfirmed() {
        reservation.setStatus(ReservationStatus.CONFIRMED);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        assertThrows(InvalidReservationStatusException.class,
                () -> reservationService.confirmReservation(reservationId));
    }


    @Test
    void confirmReservation_DecrementsStockAndSetsConfirmed_WhenValid() {
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        when(reservationRepository.findByProductIdAndStatusAndExpiresAtBefore(
                eq(productId), eq(ReservationStatus.ACTIVE), any(Instant.class)))
                .thenReturn(Collections.emptyList());

        when(reservationRepository.saveAndFlush(any(Reservation.class)))
                .thenAnswer(i -> i.getArgument(0));

        ReservationResponse response = reservationService.confirmReservation(reservationId);

        assertEquals(ReservationStatus.CONFIRMED, response.status());
        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());

        assertEquals(7, product.getStock());

        verify(reservationRepository, times(1)).saveAndFlush(reservation);
    }
}
