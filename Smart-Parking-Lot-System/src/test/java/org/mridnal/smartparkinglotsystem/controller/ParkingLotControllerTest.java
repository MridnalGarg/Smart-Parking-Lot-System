package org.mridnal.smartparkinglotsystem.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mridnal.smartparkinglotsystem.entity.Ticket;
import org.mridnal.smartparkinglotsystem.entity.Vehicle;
import org.mridnal.smartparkinglotsystem.enums.VehicleType;
import org.mridnal.smartparkinglotsystem.service.ParkingService;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingLotControllerTest {

    @Mock
    private ParkingService parkingService;

    @InjectMocks
    private ParkingLotController parkingLotController;

    private Vehicle sampleVehicle;
    private Ticket sampleTicket;

    @BeforeEach
    void setUp() {
        sampleVehicle = new Vehicle();
        sampleVehicle.setRegNumber("DL-3C-1234");
        sampleVehicle.setVehicleType(VehicleType.LMV);

        sampleTicket = new Ticket();
        // Set properties for your sample ticket as required by entity class
    }

    @Test
    void testConcurrentCheckInRequests() throws InterruptedException {
        int numberOfThreads = 10;

        // Mock the service to return a dummy ticket and simulate a slight network/processing lag (50ms)
        when(parkingService.checkIn(anyString(), any())).thenAnswer(invocation -> {
            Thread.sleep(50);
            return sampleTicket;
        });

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        // Controls simultaneous execution start
        CountDownLatch startLatch = new CountDownLatch(1);
        // Tracks when all threads finish their work
        CountDownLatch finishLatch = new CountDownLatch(numberOfThreads);

        // Thread-safe collection to capture responses
        List<ResponseEntity> responses = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await(); // Wait here until the green light is given
                    ResponseEntity<Ticket> response = parkingLotController.checkIn(sampleVehicle);
                    responses.add(response);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        // Release all threads at the exact same time
        startLatch.countDown();

        // Wait for all threads to complete their execution before evaluating assertions
        boolean completedInTime = finishLatch.await(5, TimeUnit.SECONDS);
        assertTrue(completedInTime, "The concurrent executions took too long or deadlocked.");

        // Clean up the thread pool
        executorService.shutdown();

        // Assertions
        assertEquals(numberOfThreads, responses.size(), "Not all concurrent requests completed.");
        responses.forEach(response -> assertEquals(HttpStatus.CREATED, response.getStatusCode()));

        // Verify the underlying service was triggered exactly the expected number of times
        verify(parkingService, times(numberOfThreads)).checkIn("DL-3C-1234", VehicleType.LMV);
    }

    @Test
    void testConcurrentCheckOutByRegNumberRequests() throws InterruptedException {
        int numberOfThreads = 5;
        String regNumber = "DL-3C-1234";

        when(parkingService.checkOut(anyString())).thenAnswer(invocation -> {
            Thread.sleep(30);
            return sampleTicket;
        });

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(numberOfThreads);
        List<ResponseEntity<Ticket>> responses = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    ResponseEntity<Ticket> response = parkingLotController.checkOutByRegNumber(regNumber);
                    responses.add(response);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        finishLatch.await(5, TimeUnit.SECONDS);
        executorService.shutdown();

        assertEquals(numberOfThreads, responses.size());
        for (ResponseEntity<Ticket> response : responses) {
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
        verify(parkingService, times(numberOfThreads)).checkOut(regNumber);
    }
}