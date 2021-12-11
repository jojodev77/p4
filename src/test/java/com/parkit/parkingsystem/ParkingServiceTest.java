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
import java.time.LocalDateTime;
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
		// GIVEN
		try {
			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
			// WHEN
		} catch (Exception e) {
			e.printStackTrace();
			// THEN
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}

	/**
	 * 
	 * @author j.de-la-osa
	 * @throws Exception
	 * @description process for exit vehicule
	 */
	@Test
	public void processExitingVehicleTest() throws Exception {
		// GIVEN
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		ticket.setInTime(inTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
		// WHEN
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
		when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
		// when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
		parkingService.processExitingVehicle();
		// THEN
		assertEquals(true, parkingSpotDAO.updateParking(ticket.getParkingSpot()));
	}

	/**
	 * 
	 * @author j.de-la-osa
	 * @throws Exception
	 * @description getException when process incoming vehicul is error
	 */
	@Test
	public void processIncomingVehicleTestException() throws Exception {
		// GIVEN
		try {
			// WHEN
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		} catch (Exception e) {
			// THEN
			logger.error("Unable to process incoming vehicle", e);
			assertEquals("Unable to process incoming vehicle", e.getMessage());
		}
	}

	/**
	 * 
	 * @author j.de-la-osa
	 * @throws Exception
	 * @description aviability parking spot of parking with a car
	 */
	@Test
	public void getNextParkingNumberIfAvailableOfCarTest() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		// WHEN
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
		// GIVER
		ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

		// THEN
		assertEquals(ParkingType.CAR, parkingSpot.getParkingType());
	}

	/**
	 * 
	 * @author j.de-la-osa
	 * @throws Exception
	 * @description aviability parking spot of parking with a bike
	 */
	@Test
	public void getNextParkingNumberIfAvailableOfBikeTest() throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		// WHEN
		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(2);
		// GIVEN
		ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
		// THEN
		assertEquals(ParkingType.BIKE, parkingSpot.getParkingType());
	}

	/**
	 * 
	 * @author j.de-la-osa
	 * @throws Exception
	 * @description get exception whien aviabilityparking sport is a error
	 */
	@Test
	public void getNextParkingNumberIfAvailableOfIllegalArgumentException() {
		// GIVEN
		ParkingType parkingType = null;
		// WHEN
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenThrow(IllegalArgumentException.class);

		// THEN
		parkingService.getNextParkingNumberIfAvailable();
	}

	/**
	 * 
	 * @author j.de-la-osa
	 * @throws Exception
	 * @description process incoming vehicule with a car
	 */
	@Test
	public void processIncomingVehicleTest() throws Exception {
		// GIVEN
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		ticket.setInTime(inTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
		// WHEN
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
		// when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
		when(ticketDAO.saveTicket(ticket)).thenReturn(true);

		// THEN
		parkingService.processIncomingVehicle();
		assertEquals(true, ticketDAO.saveTicket(ticket));
	}

}