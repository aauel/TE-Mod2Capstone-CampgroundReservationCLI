package com.techelevator.campground.model;

import java.math.BigDecimal;
import java.util.List;

public interface CampgroundDAO {

	public List<Campground> getCampgroundsInPark(int chosenParkId);
	public BigDecimal getDailyFeeByCampgroundId(int campgroundId);

}
