package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;

@ExtendWith(MockitoExtension.class)
public class TicketDAOTest {

	@InjectMocks
	private static ParkingService parkingService;

	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	PreparedStatement ps;

	@Mock
	TicketDAO ticketDAO;

	@InjectMocks
	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

	DataBasePrepareService dataBasePrepareService;

	Ticket ticket = new Ticket();

	Connection con = null;

	@BeforeEach
	private void setUpPerTest() throws ClassNotFoundException, SQLException {
		con = dataBaseTestConfig.getConnection();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		LocalDateTime inTime = LocalDateTime.now().minusHours(1);
		ticket.setParkingSpot(parkingSpot);
		ticket.setPrice(0);
		ticket.setInTime(inTime);
		ticket.setInTime(inTime);
		ticket.setOutTime(inTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
	}

	/**
	 * 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @description test ticket in database
	 */
	@Test
	public void saveTicketTest() throws ClassNotFoundException, SQLException {
		// GIVEN
		ps = dataBaseTestConfig.getConnection().prepareStatement(DBConstants.SAVE_TICKET);
		// WHEN
		ps.setInt(1, ticket.getParkingSpot().getId());
		ps.setString(2, ticket.getVehicleRegNumber());
		ps.setDouble(3, ticket.getPrice());
		ps.setTimestamp(4, Timestamp.valueOf((ticket.getInTime())));
		if (ticket.getInTime() != null) {
			System.out.println("--------->" + Timestamp.valueOf((ticket.getOutTime())));
			ps.setTimestamp(5, (Timestamp.valueOf((ticket.getOutTime()))));
		}

		// THEN
		assertEquals(false, ps.execute());
	}

	/**
	 * 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @description test get ticket of database
	 */
	@Test
	public void getTicketTest() throws ClassNotFoundException, SQLException {
		// GIVEN
		ps = dataBaseTestConfig.getConnection().prepareStatement(DBConstants.GET_TICKET);
		String vehicleRegNumber = "ABCDEF";
		ps.setString(1, vehicleRegNumber);
		ResultSet rs = ps.executeQuery();
		Ticket ticketSaveInDB = new Ticket();
		// WHEN
		if (rs.next()) {
			ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
			ticketSaveInDB.setParkingSpot(parkingSpot);
			ticketSaveInDB.setId(rs.getInt(2));
			ticketSaveInDB.setVehicleRegNumber(vehicleRegNumber);
			ticketSaveInDB.setPrice(rs.getDouble(3));
			ticketSaveInDB.setInTime(rs.getTimestamp(4).toLocalDateTime());
			ticketSaveInDB.setOutTime(rs.getTimestamp(5).toLocalDateTime());
		}
		// THEN
		assertEquals(ticket.getVehicleRegNumber(), ticketSaveInDB.getVehicleRegNumber());
	}

	/**
	 * 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @description test update ticket in database
	 */
	@Test
	public void updateTicketTest() throws ClassNotFoundException, SQLException {
		// GIVEN
		ps = dataBaseTestConfig.getConnection().prepareStatement(DBConstants.UPDATE_TICKET);
		LocalDateTime outTime = LocalDateTime.now().minusHours(1);
		ticket.setOutTime(outTime);
		// WHEN
		PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
		ps.setDouble(1, ticket.getPrice());
		ps.setTimestamp(2, Timestamp.valueOf((ticket.getOutTime())));
		ps.setInt(3, ticket.getId());
		
		// THEN
		assertEquals(false, ps.execute());
	}
}
