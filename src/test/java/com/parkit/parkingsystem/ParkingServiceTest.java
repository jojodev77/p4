package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

	@InjectMocks
    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;
    
   
    private static final Logger logger = LogManager.getLogger("ParkingService");
    
    

    @BeforeEach
    private void setUpPerTest() {
        try {
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processExitingVehicleTest() throws Exception{
    	 ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
    	 Ticket ticket = new Ticket();
         ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
         ticket.setParkingSpot(parkingSpot);
         ticket.setVehicleRegNumber("ABCDEF");
    	  when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    	 when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
    	  when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
          when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }
    
    @Test
    public void processIncomingVehicleTestException() throws Exception {

    	   // calling method under test
    	   try {
    			 ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
    	   } catch(Exception e) {
    		   logger.error("Unable to process incoming vehicle",e);
    		   assertEquals("Unable to process incoming vehicle", e.getMessage());
    	   }
    }
    
    
    @Test
    public void getNextParkingNumberIfAvailableOfCarTest() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    	 when(inputReaderUtil.readSelection()).thenReturn(1);
    	 when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
    	 ParkingSpot parkingSpot =  parkingService.getNextParkingNumberIfAvailable();
    	  assertEquals(ParkingType.CAR, parkingSpot.getParkingType());
    }
    
    
    @Test
    public void getNextParkingNumberIfAvailableOfBikeTest() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    	 when(inputReaderUtil.readSelection()).thenReturn(2);
    	 when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(2);
    	 ParkingSpot parkingSpot =  parkingService.getNextParkingNumberIfAvailable();
    	  assertEquals(ParkingType.BIKE, parkingSpot.getParkingType());
    }
    
    @Test
    public void getNextParkingNumberIfAvailableOfIllegalArgumentException()  {
    	ParkingType parkingType = null;
    	 when(inputReaderUtil.readSelection()).thenReturn(1);
    	 when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))) .thenThrow(IllegalArgumentException.class); 
    	 parkingService.getNextParkingNumberIfAvailable();
    }
    
      
    @Test
    public void processIncomingVehicleTest() throws Exception {
    	 ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
    	 Ticket ticket = new Ticket();
         ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
         ticket.setParkingSpot(parkingSpot);
         ticket.setVehicleRegNumber("ABCDEF");
    	when(inputReaderUtil.readSelection()).thenReturn(1);
   	 	when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
   	 	//when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
   	 	when(ticketDAO.saveTicket(ticket)).thenReturn(true);
   	 	parkingService.processIncomingVehicle();
   	 	assertEquals(true, ticketDAO.saveTicket(ticket));
    }
    

    
    

}