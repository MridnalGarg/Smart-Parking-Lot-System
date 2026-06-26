package org.mridnal.smartparkinglotsystem.strategy;

import org.mridnal.smartparkinglotsystem.enums.VehicleType;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class HMVPricingStrategy implements PricingStrategy{
    private static final double HOURLY_RATE = 30.0;
    private static final double DAILY_RATE = 250.0;

    @Override
    public double calculateFee(LocalDateTime checkIn, LocalDateTime checkOut) {
        long hours = Duration.between(checkIn, checkOut).toHours();
        if (hours < 1) hours = 1;
        if (hours < 24) return hours * HOURLY_RATE;
        int days = (int) (hours / 24); // Assuming HMV parked for N days
        return days*DAILY_RATE;
    }

    @Override
    public boolean supports(VehicleType vehicleType) {
        return vehicleType == VehicleType.HMV;
    }
}
