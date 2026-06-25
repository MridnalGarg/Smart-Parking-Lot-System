package org.mridnal.smartparkinglotsystem.repository;

import org.mridnal.smartparkinglotsystem.entity.ParkingLot;
import org.mridnal.smartparkinglotsystem.entity.Vehicle;
import org.mridnal.smartparkinglotsystem.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
    Optional<ParkingLot> findFirstByIsAvailableTrueAndSupportedVehicleType(VehicleType type);
}
