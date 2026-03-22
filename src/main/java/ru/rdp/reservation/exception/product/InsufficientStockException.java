package ru.rdp.reservation.exception.product;

public class InsufficientStockException extends AbstractProductException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
