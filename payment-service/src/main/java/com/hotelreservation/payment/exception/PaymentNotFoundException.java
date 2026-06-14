package com.hotelreservation.payment.exception;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(Long id) {
        super("Payment not found with id: " + id);
    }

    public PaymentNotFoundException(String paymentNumber) {
        super("Payment not found with number: " + paymentNumber);
    }
}
