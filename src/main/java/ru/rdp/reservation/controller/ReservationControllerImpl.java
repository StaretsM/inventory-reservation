package ru.rdp.reservation.controller;

import org.springframework.web.bind.annotation.RestController;
import ru.rdp.reservation.dto.CreateReservationRequest;
import ru.rdp.reservation.dto.ReservationResponse;
import ru.rdp.reservation.service.reservation.ReservationService;

import java.util.UUID;

@RestController
public class ReservationControllerImpl implements ReservationController{
    private final ReservationService reservationService;

    public ReservationControllerImpl(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Override
    public ReservationResponse createReservation(CreateReservationRequest request) {
        return reservationService.createReservation(request);
    }

    @Override
    public ReservationResponse confirmReservation(UUID id) {
        return reservationService.confirmReservation(id);
    }
}
