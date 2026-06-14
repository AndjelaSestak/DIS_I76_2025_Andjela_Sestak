package com.hotelreservation.reservation.exception;

public class RoomNotAvailableException extends RuntimeException {

    public RoomNotAvailableException(Long roomId) {
        super("Room with id " + roomId + " is not available");
    }
}
