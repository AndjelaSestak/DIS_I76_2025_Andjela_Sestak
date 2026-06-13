package com.hotelreservation.hotel.exception;

public class HotelNotFoundException extends RuntimeException {

    public HotelNotFoundException(Long id) {
        super("Hotel not found with id: " + id);
    }

    public HotelNotFoundException(String message) {
        super(message);
    }
}
