package com.techelevator.campground.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Campground {

	private int campgroundId;
	private int parkId;
	private String name;
	private String openFromMm;
	private String openToMm;
	private BigDecimal dailyFee;
	
	public int getCampgroundId() {
		return campgroundId;
	}
	public void setCampgroundId(int campgroundId) {
		this.campgroundId = campgroundId;
	}
	public int getParkId() {
		return parkId;
	}
	public void setParkId(int parkId) {
		this.parkId = parkId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOpenFromMm() {
		return openFromMm;
	}
	public void setOpenFromMm(String getOpenFromMm) {
		this.openFromMm = getOpenFromMm;
	}
	public String getOpenToMm() {
		return openToMm;
	}
	public void setOpenToMm(String openToMm) {
		this.openToMm = openToMm;
	}
	public BigDecimal getDailyFee() {
		return dailyFee;
	}
	public void setDailyFee(BigDecimal dailyFee) {
		this.dailyFee = dailyFee;
	}
	
	public boolean isOpen(LocalDate parsedArrivalDate, LocalDate parsedDepartureDate) {
		boolean result = false;
		int fromMonth = Integer.parseInt(getOpenFromMm());
		int toMonth = Integer.parseInt(getOpenToMm());

		int parsedArrivalMonth = parsedArrivalDate.getMonthValue();
		int parsedDepartureMonth = parsedDepartureDate.getMonthValue();

		if (parsedArrivalMonth >= fromMonth && parsedArrivalMonth <= toMonth) {
			if (parsedDepartureMonth >= fromMonth && parsedDepartureMonth <= toMonth) {
				result = true;
			}
		}
		return result;
	}

}
