package org.mridnal.smartparkinglotsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.mridnal.smartparkinglotsystem.enums.TicketStatus;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDateTime;

@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Positive(message = "Ticket Number should always be positive")
    private Long ticketNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @NotNull(message = "Vehicle information is required to create a parking ticket")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    @NotNull(message = "slot information is required to create a parking ticket")
    private ParkingLot slot;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    @PositiveOrZero(message = "Fee can be either 0.0 or a positive value")
    private Double fee;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    public Ticket(
            Vehicle vehicle,
            ParkingLot slot,
            LocalDateTime checkInTime,
            LocalDateTime checkOutTime,
            Double fee,
            TicketStatus status) {
        this.vehicle = vehicle;
        this.slot = slot;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.fee = fee;
        this.status = status;
    }

    public Ticket() {

    }

    public Long getTicketNumber() {
        return ticketNumber;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public ParkingLot getSlot() {
        return slot;
    }

    public void setSlot(ParkingLot slot) {
        this.slot = slot;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}
