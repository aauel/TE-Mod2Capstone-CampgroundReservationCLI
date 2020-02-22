package com.techelevator.campground.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SiteDAO {

	public List<Site> getTop5AvailableSites(String campground_name, LocalDate arrivalDate, LocalDate departureDate);
	
}
