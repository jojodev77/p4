package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    /**
     * 
     * @author j.de-la-osa
     * @description calcul priice for one hours with a car
     */
    @Test
    public void calculateFareCar(){
    	// GIVEN
    	LocalDateTime inTime = LocalDateTime.now().minusHours(1);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        // WHEN
        fareCalculatorService.calculateFare(ticket);
        // THEN
        assertEquals(1.5, Fare.CAR_RATE_PER_HOUR);
    }

    /**
     * 
     * @author j.de-la-osa
     * @description calcul priice for one hours with a bike
     */
    @Test
    public void calculateFareBike(){
    	// GIVEN
    	LocalDateTime inTime = LocalDateTime.now().minusHours(1);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        // WHEN
        fareCalculatorService.calculateFare(ticket);
        // THEN
        assertEquals(1, Fare.BIKE_RATE_PER_HOUR);
    }

    /**
     * 
     * @author j.de-la-osa
     * @description get exception when type of vehicule is null
     */
    @Test
    public void calculateFareUnkownType(){
    	// GIVEN
    	// WHEN
    	LocalDateTime inTime = LocalDateTime.now().minusHours(1);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        //THEN
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    /**
     * 
     * @author j.de-la-osa
     * @description get exception when  a bike is error with future in time
     */
    @Test
    public void calculateFareBikeWithFutureInTime(){
    	// GIVEN
    	// WHEN
    	LocalDateTime inTime = LocalDateTime.now().minusHours(1);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        // THEN
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    /**
     * 
     * @author j.de-la-osa
     * @description calcul price ticket when time in parking is less then one hour with a bike
     */
    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
    	//GIVEN
    	LocalDateTime inTime = LocalDateTime.now().minusMinutes(45);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        // WHEN
        fareCalculatorService.calculateFare(ticket);
        // THEN
        assertEquals(((45 - 30) * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    /**
     * 
     * @author j.de-la-osa
     * @description calcul price ticket when time in parking is less then one hour with a car
     */
    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
    	// GIVEN
    	LocalDateTime inTime = LocalDateTime.now().minusMinutes(45);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        // WHEN
        fareCalculatorService.calculateFare(ticket);
        // THEN
        assertEquals( ((45 - 30) * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }
    
    
    /**
     * 
     * @author j.de-la-osa
     * @description calcul price ticket when time in parking during 24 hours with a car
     */
    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
    	// GIVEN
    	LocalDateTime inTime = LocalDateTime.now().minusDays(1);
        LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(3, ParkingType.CAR,false);
      
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime) ;
        ticket.setParkingSpot(parkingSpot);
        // WHEN
        fareCalculatorService.calculateFare(ticket);
        // THEN
        assertEquals( ((1440 - 30) * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }
    
    /**
     * 
     * @author j.de-la-osa
     * @description calcul price ticket when time in parking is less than 30 minutes with a car
     */
    @Test
    public void calculateFareCarWithLessThan30minutsParkingTime(){
    	// GIVEN
    	LocalDateTime inTime = LocalDateTime.now().minusMinutes(15);
    	 LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime) ;
        ticket.setParkingSpot(parkingSpot);
        // WHEN
        fareCalculatorService.calculateFare(ticket);
        // THEN
        assertEquals( 0 , ticket.getPrice());
    }
    
    /**
     * 
     * @author j.de-la-osa
     * @description calcul price ticket when time in parking is less than 30 minutes with a bike
     */
    @Test
    public void calculateFareBikeWithLessThan30minutsParkingTime(){
    	// GIVEN
    	LocalDateTime inTime = LocalDateTime.now().minusMinutes(15);
    	 LocalDateTime outTime = LocalDateTime.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime) ;
        ticket.setParkingSpot(parkingSpot);
        // WHEN
        fareCalculatorService.calculateFare(ticket);
        // THEN
        assertEquals( 0 , ticket.getPrice());
    }

}
