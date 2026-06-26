package org.mridnal.smartparkinglotsystem.config;

import org.mridnal.smartparkinglotsystem.entity.ParkingLot;
import org.mridnal.smartparkinglotsystem.enums.VehicleType;
import org.mridnal.smartparkinglotsystem.repository.ParkingLotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Component
@EnableConfigurationProperties(ParkingLotProperties.class)
public class ParkingSlotAutoConfigurer implements CommandLineRunner {

    private static final Logger LOGGER = Logger.getLogger(ParkingSlotAutoConfigurer.class.getName());
    private final ParkingLotRepository slotRepository;
    private final ParkingLotProperties lotProperties;

    public ParkingSlotAutoConfigurer(ParkingLotRepository slotRepository, ParkingLotProperties lotProperties) {
        this.slotRepository = slotRepository;
        this.lotProperties = lotProperties;
    }

    @Override
    public void run(String... args) {
        try {
            // Prevent duplicate setups on application restarts
            long existingCount = slotRepository.count();
            LOGGER.info(">>> System Boot: Parking Lot initialization starting... Existing slots: " + existingCount);
            
            if (existingCount == 0) {
                LOGGER.info(">>> System Boot: Starting Parking Lot Auto-Configuration...");
                List<ParkingLot> slotsToInitialize = new ArrayList<>();

                Map<VehicleType, Integer> capacityMap = lotProperties.getCapacityAsEnum();
                LOGGER.info(">>> Raw config: " + lotProperties.getCapacity());

                if (capacityMap != null && !capacityMap.isEmpty()) {
                    LOGGER.info(">>> Capacity map: " + capacityMap);
                    
                    // Loop through every configured VehicleType in your map
                    capacityMap.forEach((vehicleType, count) -> {
                        LOGGER.info(">>> Creating " + count + " slots for vehicle type: " + vehicleType);
                        
                        // Extract prefix character (e.g., 'L' for LMV, 'H' for HMV)
                        String prefix = String.valueOf(vehicleType.name().charAt(0));

                        for (int i = 1; i <= count; i++) {
                            ParkingLot slot = new ParkingLot();
                            slot.setSlotNumber(prefix + "-" + i);
                            slot.setAvailable(true);
                            slot.setSupportedVehicleType(vehicleType);

                            slotsToInitialize.add(slot);
                        }
                    });

                    if (!slotsToInitialize.isEmpty()) {
                        slotRepository.saveAll(slotsToInitialize);
                        LOGGER.info(">>> System Boot: Auto-configured " + slotsToInitialize.size() + " slots successfully.");
                    } else {
                        LOGGER.warning(">>> System Boot: No slots to initialize!");
                    }
                } else {
                    LOGGER.warning(">>> System Boot: Capacity map is null or empty. Check application.properties!");
                }
            } else {
                LOGGER.info(">>> System Boot: Parking configuration already present. Skipping initialization step.");
            }
        } catch (Exception e) {
            LOGGER.severe(">>> System Boot: Error during parking lot auto-configuration: " + e.getMessage());
        }
    }
}