package com.techelevator.campground.model;

import java.util.List;

public interface SiteDAO {

	public List<Site> availableSites(String campground_name, String arrivalDate, String departureDate);
	
}
