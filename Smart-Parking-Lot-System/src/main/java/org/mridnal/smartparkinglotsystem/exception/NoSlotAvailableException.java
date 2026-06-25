package org.mridnal.smartparkinglotsystem.exception;

public class NoSlotAvailableException extends RuntimeException {
    public NoSlotAvailableException(String message) {
        super(message);
    }
}
