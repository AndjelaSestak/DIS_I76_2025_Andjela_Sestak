package com.hotelreservation.reservation.exception;

public class ReservationNotFoundException extends RuntimeException {

    public ReservationNotFoundException(Long id) {
        super("Reservation not found with id: " + id);
    }

    public ReservationNotFoundException(String reservationNumber) {
        super("Reservation not found with number: " + reservationNumber);
    }
}
