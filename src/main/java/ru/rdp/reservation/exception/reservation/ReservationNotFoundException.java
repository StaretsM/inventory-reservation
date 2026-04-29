package ru.rdp.reservation.exception.reservation;

public class ReservationNotFoundException extends AbstractReservationException {
    public ReservationNotFoundException(String message) {
        super(message);
    }
}
