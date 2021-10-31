package com.parkit.parkingsystem.service;

import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.NullArgumentException;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
	
	 TicketDAO ticketDAO = new TicketDAO();

    public void calculateFare(Ticket ticket){
    
        if( (ticket.getOutTime() == null ) || ticket.getInTime() == null || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        //TODO: Some tests are failing here. Need to check if this logic is correct
        /**
         * @author j.de-la-osa
         * @return le temps facturé en heure
         * @param ticket.getOutTime().getTime
         * @param ticket.getInTime().getTime()
         */
        long diff  = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
      
        double duration = TimeUnit.MILLISECONDS.toHours(diff) ;
        
        /* 2 decimal apres la virgule*/
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        
        
        /**
         * @author j.de-la-osa
         * @return  si le temps est moins de 1 heure, on le correspond en minute
         * @param duration
         */
        if (duration < 1.1) {
              duration = (double) ((diff / (1000*60)) % 60) /60;
             
        }
        
        if (ticket.getParkingSpot().getParkingType() == null) {
			throw new NullPointerException("forget type of vehicul");
		}
        
        if (duration < 0.30) {
        	freeFor30minuts(duration, ticket);
		} else {
			switch (ticket.getParkingSpot().getParkingType()){  
            case CAR: {
                ticket.setPrice( (duration - 0.30)   * Fare.CAR_RATE_PER_HOUR - (((duration - 0.30)   * Fare.CAR_RATE_PER_HOUR ) * clientIsExist (ticket)  / 100));
                break;
            }
            case BIKE: {
                ticket.setPrice((duration - 0.30)  * Fare.BIKE_RATE_PER_HOUR - (((duration - 0.30)   * Fare.BIKE_RATE_PER_HOUR ) * clientIsExist (ticket)  / 100));
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
		}
        

        
    }
    
    private void freeFor30minuts(double duration, Ticket ticket) {
  
    	switch (ticket.getParkingSpot().getParkingType()){  
        case CAR: {
            ticket.setPrice( 0 );
            break;
        }
        case BIKE: {
            ticket.setPrice(0);
            break;
        }
        default: throw new IllegalArgumentException("Unkown Parking Type");
    }
    }
    
    /**
     * 
     * @author j.de-la-osa
     * @param ticket
     * @return une reduction si l utilisateur existe dans la base de donnee
     */
    private int clientIsExist(Ticket ticket) {
    	int reduction = 0;
    	if (ticketDAO.getTicket(ticket.getVehicleRegNumber()) != null) {
    		 reduction = 5;
		} else {
			 reduction = 0;
		}
    	return reduction;	
    }
}