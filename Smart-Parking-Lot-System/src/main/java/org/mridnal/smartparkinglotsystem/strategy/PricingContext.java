package org.mridnal.smartparkinglotsystem.strategy;

import org.mridnal.smartparkinglotsystem.enums.VehicleType;
import org.mridnal.smartparkinglotsystem.exception.PricingStrategyNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PricingContext {

    private final List<PricingStrategy> pricingStrategies;

    @Autowired
    public PricingContext(List<PricingStrategy> strategies) {
        this.pricingStrategies = strategies;
    }

    public double calculateFee(VehicleType vehicleType, LocalDateTime checkIn, LocalDateTime checkOut) {
        return pricingStrategies.stream()
                .filter(strategy -> strategy.supports(vehicleType))
                .findFirst()
                .orElseThrow(() -> new PricingStrategyNotFoundException("No pricing strategy found for type: " + vehicleType))
                .calculateFee(checkIn, checkOut);
    }

}
