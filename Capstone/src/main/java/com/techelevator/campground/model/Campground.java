package com.techelevator.campground.model;

import java.math.BigDecimal;

public class Campground {

	private int campground_id;
	private int park_id;
	private String name;
	private String open_from_mm;
	private String open_to_mm;
	private BigDecimal daily_fee;
	
	public int getCampground_id() {
		return campground_id;
	}
	public void setCampground_id(int campground_id) {
		this.campground_id = campground_id;
	}
	public int getPark_id() {
		return park_id;
	}
	public void setPark_id(int park_id) {
		this.park_id = park_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOpen_from_mm() {
		return open_from_mm;
	}
	public void setOpen_from_mm(String open_from_mm) {
		this.open_from_mm = open_from_mm;
	}
	public String getOpen_to_mm() {
		return open_to_mm;
	}
	public void setOpen_to_mm(String open_to_mm) {
		this.open_to_mm = open_to_mm;
	}
	public BigDecimal getDaily_fee() {
		return daily_fee;
	}
	public void setDaily_fee(BigDecimal daily_fee) {
		this.daily_fee = daily_fee;
	}

}
