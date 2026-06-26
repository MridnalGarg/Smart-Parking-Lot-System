package org.mridnal.smartparkinglotsystem.service;

import jakarta.transaction.Transactional;
import org.mridnal.smartparkinglotsystem.entity.ParkingLot;
import org.mridnal.smartparkinglotsystem.entity.Ticket;
import org.mridnal.smartparkinglotsystem.entity.Vehicle;
import org.mridnal.smartparkinglotsystem.enums.TicketStatus;
import org.mridnal.smartparkinglotsystem.enums.VehicleType;
import org.mridnal.smartparkinglotsystem.exception.NoSlotAvailableException;
import org.mridnal.smartparkinglotsystem.exception.TicketNotFoundException;
import org.mridnal.smartparkinglotsystem.exception.VehicleAlreadyCheckInException;
import org.mridnal.smartparkinglotsystem.exception.InputViolationException;
import org.mridnal.smartparkinglotsystem.repository.ParkingLotRepository;
import org.mridnal.smartparkinglotsystem.repository.TicketRepository;
import org.mridnal.smartparkinglotsystem.repository.VehicleRepository;
import org.mridnal.smartparkinglotsystem.strategy.PricingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ParkingService {

    @Autowired
    private LockService lockService;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PricingContext pricingContext;

    @Transactional
    public Ticket checkIn(String regNumber, VehicleType type) {
        String lockKey = "vehicle:" + regNumber;
        try {
            lockService.lock(lockKey);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for lock");
        }

        try {

        // Fetch the vehicle if it exists
        Optional<Vehicle> existingVehicle = vehicleRepository.findByRegNumber(regNumber);

        // Validate the vehicle type if it already exists
        if (existingVehicle.isPresent()
                && !existingVehicle.get().getVehicleType().equals(type)) {
            throw new InputViolationException(
                    "Vehicle " + regNumber + " is registered as a " + existingVehicle.get().getVehicleType() +
                            ", but you attempted to check it in as a " + type
            );
        }

        // Check if the Vehicle is already checked-in
        boolean isAlreadyCheckedIn = ticketRepository.existsByVehicleRegNumberAndStatus(regNumber, TicketStatus.ACTIVE);

        if(isAlreadyCheckedIn){
            throw new VehicleAlreadyCheckInException("Vehicle already check in");
        }

        // Find an available slot matching the vehicle type and reserve it.
        ParkingLot slot = parkingLotRepository.findFirstByIsAvailableTrueAndSupportedVehicleType(type)
                .orElseThrow(() -> new NoSlotAvailableException("Parking Full! We don't have any slots available."));

        slot.setAvailable(false);
        parkingLotRepository.save(slot);

        // Get the existing vehicle or create a new one safely
        Vehicle vehicle = existingVehicle
                .orElseGet(() -> vehicleRepository.save(new Vehicle(regNumber, type)));

        // Issue Ticket
        Ticket ticket = new Ticket();
        ticket.setVehicle(vehicle);
        ticket.setSlot(slot);
        ticket.setCheckInTime(LocalDateTime.now());
        ticket.setStatus(TicketStatus.ACTIVE);

        return ticketRepository.save(ticket);
        } finally {
            lockService.unlock(lockKey);
        }
    }

    @Transactional
    public Ticket checkOut(String regNumber){
        String lockKey = "vehicle:" + regNumber;
        try {
            lockService.lock(lockKey);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for lock");
        }

        try {
            Ticket ticket = ticketRepository.findByVehicleRegNumberAndStatus(regNumber, TicketStatus.ACTIVE)
                .orElseThrow(() -> new TicketNotFoundException("Active ticket not found for vehicle: " + regNumber));

        LocalDateTime checkOutTime = LocalDateTime.now();
        ticket.setCheckOutTime(checkOutTime);

        // Calculate fee using pricing strategy
        VehicleType vehicleType = ticket.getVehicle().getVehicleType();
        double finalFee = pricingContext.calculateFee(vehicleType, ticket.getCheckInTime(), checkOutTime);
        ticket.setFee(finalFee);

        // Free up the parking slot
        ParkingLot slot = ticket.getSlot();
        slot.setAvailable(true);
        parkingLotRepository.save(slot);

        ticket.setStatus(TicketStatus.COMPLETED);
        return ticketRepository.save(ticket);
        } finally {
            lockService.unlock(lockKey);
        }
    }


    @Transactional
    public Ticket checkOutByTicketId(String ticketNumber) {

        Ticket ticket = ticketRepository.findByTicketNumberAndStatus(Long.parseLong(ticketNumber), TicketStatus.ACTIVE)
                .orElseThrow(() -> new TicketNotFoundException("Active ticket not found"));

        LocalDateTime checkOutTime = LocalDateTime.now();
        ticket.setCheckOutTime(checkOutTime);

        // Calculate fee using strategy based on vehicle type
        VehicleType vehicleType = ticket.getVehicle().getVehicleType();
        double finalFee = pricingContext.calculateFee(vehicleType, ticket.getCheckInTime(), checkOutTime);
        ticket.setFee(finalFee);

        // Free up the parking slot
        ParkingLot slot = ticket.getSlot();
        slot.setAvailable(true);
        parkingLotRepository.save(slot);

        ticket.setStatus(TicketStatus.COMPLETED);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket cancelTicketByTicketNumber(String ticketNumber) {

        Ticket ticket = ticketRepository.findByTicketNumberAndStatus(Long.parseLong(ticketNumber), TicketStatus.ACTIVE)
                .orElseThrow(() -> new TicketNotFoundException("Active ticket not found"));

        ticket.setCheckOutTime(LocalDateTime.now());

        // No fee for cancelled tickets
        ticket.setFee(0.0);

        // Free up the parking slot
        ParkingLot slot = ticket.getSlot();
        slot.setAvailable(true);
        parkingLotRepository.save(slot);

        // Mark ticket as cancelled
        ticket.setStatus(TicketStatus.CANCELLED);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket cancelTicketByRegNumber(String regNumber) {

        Ticket ticket = ticketRepository.findByVehicleRegNumberAndStatus(regNumber, TicketStatus.ACTIVE)
                .orElseThrow(() -> new TicketNotFoundException("Active ticket not found for vehicle: " + regNumber));

        ticket.setCheckOutTime(LocalDateTime.now());

        // No fee for cancelled tickets
        ticket.setFee(0.0);

        // Free up the parking slot
        ParkingLot slot = ticket.getSlot();
        slot.setAvailable(true);
        parkingLotRepository.save(slot);

        // Mark ticket as cancelled
        ticket.setStatus(TicketStatus.CANCELLED);
        return ticketRepository.save(ticket);
    }

}
