package org.mridnal.smartparkinglotsystem.repository;

import org.mridnal.smartparkinglotsystem.entity.Ticket;
import org.mridnal.smartparkinglotsystem.entity.Vehicle;
import org.mridnal.smartparkinglotsystem.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTicketNumberAndStatus(Long ticketNumber, TicketStatus ticketStatus);

    Optional<Ticket> findByVehicleRegNumberAndStatus(String regNumber, TicketStatus ticketStatus);

    boolean existsByVehicleRegNumberAndStatus(String regNumber, TicketStatus status);
}
