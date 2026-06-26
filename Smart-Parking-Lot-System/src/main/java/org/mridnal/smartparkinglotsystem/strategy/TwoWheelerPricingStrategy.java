package org.mridnal.smartparkinglotsystem.strategy;

import org.mridnal.smartparkinglotsystem.enums.VehicleType;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class TwoWheelerPricingStrategy implements PricingStrategy{
    private static final double HOURLY_RATE = 5.0;

    @Override
    public double calculateFee(LocalDateTime checkIn, LocalDateTime checkOut) {
        long hours = Duration.between(checkIn, checkOut).toHours();
        if (hours < 1) hours = 1; // Charge minimum 1-hour fee
        return hours * HOURLY_RATE;
    }

    @Override
    public boolean supports(VehicleType vehicleType) {
        return vehicleType == VehicleType.TWO_WHEELER;
    }
}
