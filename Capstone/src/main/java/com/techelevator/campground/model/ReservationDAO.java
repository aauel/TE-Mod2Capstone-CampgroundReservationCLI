package com.techelevator.campground.model;

import java.time.LocalDate;

public interface ReservationDAO {

	
	public int createReservationAndReturnId(String name, int site_id, LocalDate startDate, int days);
	
	
}
