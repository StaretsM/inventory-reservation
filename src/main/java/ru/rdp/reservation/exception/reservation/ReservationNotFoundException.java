package ru.rdp.reservation.exception.reservation;

import java.util.UUID;

public class ReservationNotFoundException extends AbstractReservationException {
    public ReservationNotFoundException(String message) {
        super(message);
    }
}
