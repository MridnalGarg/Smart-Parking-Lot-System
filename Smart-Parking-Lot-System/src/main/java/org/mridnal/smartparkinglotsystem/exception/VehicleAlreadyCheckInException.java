package org.mridnal.smartparkinglotsystem.exception;

public class VehicleAlreadyCheckInException extends RuntimeException {
    public VehicleAlreadyCheckInException(String message) {
        super(message);
    }
}
