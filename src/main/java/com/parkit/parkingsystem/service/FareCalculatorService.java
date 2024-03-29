package com.parkit.parkingsystem.service;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.NullArgumentException;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

/**
 * 
 * @author j.de-la-osa
 * @Description service calcul of count for the client
 *
 */
public class FareCalculatorService {

	TicketDAO ticketDAO = new TicketDAO();

	public void calculateFare(Ticket ticket) {

		if ((ticket.getOutTime() == null) || ticket.getInTime() == null) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		// return this time in MINUTS

		long duration = ChronoUnit.MINUTES.between(ticket.getInTime(), ticket.getOutTime());

		// limit decimal to 2 number after decimal
		     DecimalFormat df = new DecimalFormat("0.00");

		if (ticket.getParkingSpot().getParkingType() == null) {
			throw new NullPointerException("forget type of vehicul");
		}

		// for the free time for the client
		if (duration < 30) {
			freeFor30minuts(duration, ticket);
		} else {
			// calcul to reduction with free 30 minuts and 5% for the client is present in
			// database
			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				ticket.setPrice((duration  - 30) * 0.016667 * Fare.CAR_RATE_PER_HOUR
						- (((duration - 30) * 0.016667 * Fare.CAR_RATE_PER_HOUR) * clientIsExist(ticket) / 100));
				break;
			}
			case BIKE: {
				ticket.setPrice((duration - 30) * 0.016667 * Fare.BIKE_RATE_PER_HOUR
						- (((duration - 30) * 0.016667 * Fare.BIKE_RATE_PER_HOUR) * clientIsExist(ticket) / 100));
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		}

	}

	/**
	 * @author j.de-la-osa
	 * @param duration
	 * @param ticket
	 * @Description post free parking for the client
	 */
	private void freeFor30minuts(double duration, Ticket ticket) {

		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			ticket.setPrice(0);
			break;
		}
		case BIKE: {
			ticket.setPrice(0);
			break;
		}
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}
	}

	/**
	 * 
	 * @author j.de-la-osa
	 * @param ticket
	 * @return 5% of count oh ticket if client exist in database
	 */
	private int clientIsExist(Ticket ticket) {
		int reduction = 0;
		if (ticketDAO.getTicket(ticket.getVehicleRegNumber()) != null
				&& ticketDAO.getTicket(ticket.getVehicleRegNumber()).getOutTime() != null) {
			reduction = 5;
		} else {
			reduction = 0;
		}
		return reduction;
	}
}