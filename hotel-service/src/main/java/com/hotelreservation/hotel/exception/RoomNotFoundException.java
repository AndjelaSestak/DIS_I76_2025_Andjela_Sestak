package com.hotelreservation.hotel.exception;

public class RoomNotFoundException extends RuntimeException {

    public RoomNotFoundException(Long id) {
        super("Room not found with id: " + id);
    }

    public RoomNotFoundException(String message) {
        super(message);
    }
}
