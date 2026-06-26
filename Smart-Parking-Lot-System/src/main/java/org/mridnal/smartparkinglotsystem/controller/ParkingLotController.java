package org.mridnal.smartparkinglotsystem.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.mridnal.smartparkinglotsystem.entity.Ticket;
import org.mridnal.smartparkinglotsystem.entity.Vehicle;
import org.mridnal.smartparkinglotsystem.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parking")
@Validated
public class ParkingLotController {

    @Autowired
    private ParkingService parkingService;

    @PostMapping("/checkin")
    public ResponseEntity<Ticket> checkIn(@Valid @RequestBody Vehicle vehicle) {
        Ticket ticket = parkingService.checkIn(vehicle.getRegNumber().trim(), vehicle.getVehicleType());
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }

    @PutMapping("/checkout")
    public ResponseEntity<Ticket> checkOutByRegNumber(@NotBlank @RequestParam("reg_number") String regNumber) {
        Ticket ticket = parkingService.checkOut(regNumber.trim());
        return ResponseEntity.ok(ticket);
    }

    @PutMapping("/checkout/{ticketNumber}")
    public ResponseEntity<Ticket> checkOutByTicket(@Positive @PathVariable Long ticketNumber) {
        Ticket ticket = parkingService.checkOutByTicketId(String.valueOf(ticketNumber));
        return ResponseEntity.ok(ticket);
    }

    @PatchMapping("/cancel")
    public ResponseEntity<Ticket> cancelTicketByRegNumber(@NotBlank @RequestParam("reg_number") String regNumber) {
        Ticket ticket = parkingService.cancelTicketByRegNumber(regNumber.trim());
        return ResponseEntity.ok(ticket);
    }

    @PatchMapping("/cancel/{ticketNumber}")
    public ResponseEntity<Ticket> cancelTicketByTicketNumber(@Positive @PathVariable Long ticketNumber) {
        Ticket ticket = parkingService.cancelTicketByTicketNumber(String.valueOf(ticketNumber));
        return ResponseEntity.ok(ticket);
    }

}