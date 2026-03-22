package ru.rdp.reservation.exception.product;

public class ProductNotFoundException extends AbstractProductException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
