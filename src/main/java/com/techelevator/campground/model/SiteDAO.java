package com.techelevator.campground.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SiteDAO {

	public List<Site> getTop5AvailableSites(int campgroundId, LocalDate arrivalDate, LocalDate departureDate);
	
}
