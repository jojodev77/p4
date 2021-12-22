package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;

@ExtendWith(MockitoExtension.class)
public class ParkingSpotDAOTest {
	
	@InjectMocks
	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	
	ParkingType parkingType;

	DataBasePrepareService dataBasePrepareService;
	
	PreparedStatement ps;
	
	Connection con = null;

	@BeforeEach
	private void setUpPerTest() throws ClassNotFoundException, SQLException {
		con = dataBaseTestConfig.getConnection();
	}
	
	/**
	 * 
	 * @throws SQLException
	 * @description test for see if spot is free
	 */
	@Test
	public  void getNextAvailableSlotFreeSpotTest() throws SQLException {
		// GIVEN
		ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
		// WHEN
		parkingType = ParkingType.CAR;
		ps.setString(1, parkingType.toString());
		ResultSet rs = ps.executeQuery();
		// THEN
		assertEquals(true, rs.next());
	}
	
	/**
	 * 
	 * @throws SQLException
	 * @description test update ticket
	 */
	@Test
	public void updateTicketTest() throws SQLException {
		//GIVEN
		ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
		//WHEN
		ps.setBoolean(1, parkingSpot.isAvailable());
		ps.setInt(2, parkingSpot.getId());
		int updateRowCount = ps.executeUpdate();
		dataBaseTestConfig.closePreparedStatement(ps);
		//THEN
		assertEquals(true, updateRowCount == 1);
	}
	
	
	/**
	 * 
	 * @throws SQLException
	 * @description test error update ticket
	 */
	@Test
	public void updateTicketErrorWhenParkingSportIsFalseTest() throws SQLException {
		//GIVEN
		ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
		ParkingSpot parkingSpot = new ParkingSpot(0, ParkingType.CAR, false);
		//WHEN
		ps.setBoolean(1, parkingSpot.isAvailable());
		ps.setInt(2, parkingSpot.getId());
		int updateRowCount = ps.executeUpdate();
		dataBaseTestConfig.closePreparedStatement(ps);
		//THEN
		assertEquals(false, updateRowCount == 1);
	}
}
