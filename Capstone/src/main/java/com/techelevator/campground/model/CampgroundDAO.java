package com.techelevator.campground.model;

import java.util.List;

public interface CampgroundDAO {

	public List<Campground> getCampgroundsInPark(int chosen_park_id);
	public Campground getCampgroundByCampgroundId(int campground_id);
}
