package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	@InjectMocks
	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

	@InjectMocks
	private static ParkingService parkingService;

	// @Mock
	private static ParkingSpotDAO parkingSpotDAO;

	// @Mock
	private static TicketDAO ticketDAO;

	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static FareCalculatorService fareCalculatorService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {

	}
	@BeforeAll
	public void entryParkingWithCarMethod() {
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
		Ticket ticket = new Ticket();
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		LocalDateTime ouTime = LocalDateTime.now();
		ticket.setInTime(inTime);
		ticket.setOutTime(ouTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("AAA");
		when(inputReaderUtil.readSelection()).thenReturn(1);
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		parkingSpotDAO.getNextAvailableSlot(parkingSpot.getParkingType());
		ticketDAO.saveTicket(ticket);
		parkingService.processIncomingVehicle();
	}
	
	@BeforeAll
	public void entryParkingWithBikeMethod() {
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, true);
		Ticket ticket = new Ticket();
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		LocalDateTime ouTime = LocalDateTime.now();
		ticket.setInTime(inTime);
		ticket.setOutTime(ouTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("AAA");
		when(inputReaderUtil.readSelection()).thenReturn(1);
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		parkingSpotDAO.getNextAvailableSlot(parkingSpot.getParkingType());
		ticketDAO.saveTicket(ticket);
		parkingService.processIncomingVehicle();
	}
	
	

	/**
	 * 
	 * @throws Exception
	 * @description simulation of entry car in the parking
	 */
	@Test
	public void entryParkingWithCarTest() throws Exception {
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
		Ticket ticket = new Ticket();
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		LocalDateTime ouTime = LocalDateTime.now();
		ticket.setInTime(inTime);
		ticket.setOutTime(ouTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("AAA");
		when(inputReaderUtil.readSelection()).thenReturn(1);
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		parkingSpotDAO.getNextAvailableSlot(parkingSpot.getParkingType());
		ticketDAO.saveTicket(ticket);
		parkingService.processIncomingVehicle();
		String t = ticketDAO.getTicket("AAA").getVehicleRegNumber();
		assertEquals(t, ticket.getVehicleRegNumber());

		// TODO: check that a ticket is actualy saved in DB and Parking table is updated
		// with availability
	}

	/**
	 * 
	 * @throws Exception
	 * @description simulation of entry bike in the parking
	 */
	@Test
	public void entryParkingWithBikeTest() throws Exception {
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, true);
		Ticket ticket = new Ticket();
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		LocalDateTime ouTime = LocalDateTime.now();
		ticket.setInTime(inTime);
		ticket.setOutTime(ouTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("AAA");
		when(inputReaderUtil.readSelection()).thenReturn(1);
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		parkingSpotDAO.getNextAvailableSlot(parkingSpot.getParkingType());
		ticketDAO.saveTicket(ticket);
		parkingService.processIncomingVehicle();
		String t = ticketDAO.getTicket("AAA").getVehicleRegNumber();
		assertEquals(t, ticket.getVehicleRegNumber());

		// TODO: check that a ticket is actualy saved in DB and Parking table is updated
		// with availability
	}

	/**
	 * 
	 * @throws Exception
	 * @description simulation of started car in the parking and verify timer of ticket
	 */
	@Test
	public void startedParkingWithCarTest() throws Exception {
		entryParkingWithCarMethod();
		Ticket ticket = new Ticket();
		LocalDateTime outTime = LocalDateTime.now();
		ticket = ticketDAO.getTicket("AAA");
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(ticket.getParkingSpot());
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("AAA");
		parkingSpotDAO.getNextAvailableSlot(ticket.getParkingSpot().getParkingType());
		parkingSpotDAO.updateParking(ticket.getParkingSpot());
		parkingService.processExitingVehicle();
		assertEquals(outTime, ticket.getOutTime());
		// TODO: check that the fare generated and out time are populated correctly in
		// the database
	}
	
	/**
	 * 
	 * @throws Exception
	 * @description simulation of started bike in the parking and verify timer of ticket
	 */
	@Test
	public void startedParkingWithBikeTest() throws Exception {
		entryParkingWithBikeMethod();
		Ticket ticket = new Ticket();
		LocalDateTime outTime = LocalDateTime.now();
		ticket = ticketDAO.getTicket("AAA");
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(ticket.getParkingSpot());
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("AAA");
		parkingSpotDAO.getNextAvailableSlot(ticket.getParkingSpot().getParkingType());
		parkingSpotDAO.updateParking(ticket.getParkingSpot());
		parkingService.processExitingVehicle();
		assertEquals(outTime, ticket.getOutTime());
		// TODO: check that the fare generated and out time are populated correctly in
		// the database
	}

	/**
	 * 
	 * @throws Exception
	 * @description simulation of entry car in the parking with error of timing
	 */
	@Test
	public void ErrorEntryParkingWithCarBeacauseOutimeIsEmptyTest() throws Exception {
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
		Ticket ticket = new Ticket();
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		LocalDateTime ouTime = LocalDateTime.now();
		ticket.setInTime(inTime);
		ticket.setOutTime(null);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("BBB");
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		parkingSpotDAO.getNextAvailableSlot(parkingSpot.getParkingType());
		assertEquals(ticketDAO.saveTicket(ticket), false);
	}
	// assertThrows(NullPointerException.class, () -> ticketDAO.saveTicket(ticket));

	/**
	 * 
	 * @throws Exception
	 * @description simulation of entry car in the parking with error of parkingSpot
	 */
	public void ErrorEntryParkingWithCarBeacauseParkingSportIsFullTest() throws Exception {
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		LocalDateTime ouTime = LocalDateTime.now();
		ticket.setInTime(inTime);
		ticket.setOutTime(ouTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("BBB");
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		parkingSpotDAO.getNextAvailableSlot(parkingSpot.getParkingType());
		assertEquals(ticketDAO.saveTicket(ticket), false);
	}
	
	/**
	 * 
	 * @throws Exception
	 * @description simulation of started with car when ticket is null
	 */
	@Test
	public void ErrorstartedParkingWithBikeWhenTicketIsNotFoundTest() throws Exception {
		entryParkingWithBikeMethod();
		Ticket ticket = new Ticket();
		LocalDateTime outTime = LocalDateTime.now();
		ticket = ticketDAO.getTicket("Axddd");
		assertNull(ticket);
	}
	
	/**
	 * 
	 * @throws Exception
	 * @description simulation of started with bike time format is bad
	 */
	@Test
	public void ErrorstartedParkingWithBikeWhenFormatTimeIsBadTest() throws Exception {
		entryParkingWithBikeMethod();
		Ticket ticket = new Ticket();
		LocalDateTime outTime = LocalDateTime.now().minusMonths(1);
		ticket = ticketDAO.getTicket("AAA");
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(ticket.getParkingSpot());
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("AAA");
		parkingSpotDAO.getNextAvailableSlot(ticket.getParkingSpot().getParkingType());
		parkingSpotDAO.updateParking(ticket.getParkingSpot());
		parkingService.processExitingVehicle();
		assertEquals(false, ticket.getPrice() > 0 );
		// TODO: check that the fare generated and out time are populated correctly in
		// the database
	}
	
	/**
	 * 
	 * @throws Exception
	 * @description simulation of started with car time format is bad
	 */
	@Test
	public void ErrorstartedParkingWithCarWhenFormatTimeIsBadTest() throws Exception {
		entryParkingWithCarMethod();
		Ticket ticket = new Ticket();
		LocalDateTime outTime = LocalDateTime.now().minusMonths(1);
		ticket = ticketDAO.getTicket("AAA");
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(ticket.getParkingSpot());
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("AAA");
		parkingSpotDAO.getNextAvailableSlot(ticket.getParkingSpot().getParkingType());
		parkingSpotDAO.updateParking(ticket.getParkingSpot());
		parkingService.processExitingVehicle();
		assertEquals(false, ticket.getPrice() > 0 );
		// TODO: check that the fare generated and out time are populated correctly in
		// the database
	}

}
