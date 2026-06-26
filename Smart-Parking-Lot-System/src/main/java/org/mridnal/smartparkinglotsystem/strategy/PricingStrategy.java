package org.mridnal.smartparkinglotsystem.strategy;

import org.mridnal.smartparkinglotsystem.enums.VehicleType;

import java.time.LocalDateTime;

public interface PricingStrategy {
    double calculateFee(LocalDateTime checkIn, LocalDateTime checkOut);
    boolean supports(VehicleType vehicleType);
}
