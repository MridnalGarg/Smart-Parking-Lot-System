package org.mridnal.smartparkinglotsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.mridnal.smartparkinglotsystem.enums.VehicleType;

@Entity
public class ParkingLot {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "slotNumber is required")
    @Column(unique = true)
    private String slotNumber;

    @NotNull(message = "isAvailable flag should not be null")
    private boolean isAvailable;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "supportedVehicleType is required")
    private VehicleType supportedVehicleType;

    public ParkingLot() {}

    public ParkingLot(String slotNumber, boolean isAvailable, VehicleType supportedVehicleType) {
        this.slotNumber = slotNumber;
        this.isAvailable = isAvailable;
        this.supportedVehicleType = supportedVehicleType;
    }

    public Long getId() {
        return id;
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(String slotNumber) {
        this.slotNumber = slotNumber;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public VehicleType getSupportedVehicleType() {
        return supportedVehicleType;
    }

    public void setSupportedVehicleType(VehicleType supportedVehicleType) {
        this.supportedVehicleType = supportedVehicleType;
    }
}
