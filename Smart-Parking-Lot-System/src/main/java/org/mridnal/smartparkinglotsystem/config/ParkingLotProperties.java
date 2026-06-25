package org.mridnal.smartparkinglotsystem.config;

import org.mridnal.smartparkinglotsystem.enums.VehicleType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "parking")
public class ParkingLotProperties {

    private final Map<String, Integer> capacity = new HashMap<>();

    public Map<String, Integer> getCapacity() {
        return capacity;
    }

    public Map<VehicleType, Integer> getCapacityAsEnum() {
        Map<VehicleType, Integer> enumCapacity = new HashMap<>();
        capacity.forEach((key, value) -> {
            try {
                VehicleType vehicleType = VehicleType.valueOf(key.toUpperCase().trim());
                enumCapacity.put(vehicleType, value);
            } catch (IllegalArgumentException e) {
                System.err.println(">>> Warning: Invalid vehicle type in config: " + key);
            }
        });
        return enumCapacity;
    }
}
