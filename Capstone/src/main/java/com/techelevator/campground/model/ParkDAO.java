package com.techelevator.campground.model;

import java.util.List;

public interface ParkDAO {

	public List<Park> getAllParks();
	public int getParkIdByName(String park_name);
	public Park getParkByName(String park_name);
	
}
