package com.techelevator.campground.model;

import java.time.LocalDate;

public class Park {

	private int park_id;
	private String name;
	private String location;
	private LocalDate establish_date;
	private int area;
	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}
	private int visitors;
	private String description;
	
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
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public LocalDate getEstablish_date() {
		return establish_date;
	}
	public void setEstablish_date(LocalDate establish_date) {
		this.establish_date = establish_date;
	}
	public int getVisitors() {
		return visitors;
	}
	public void setVisitors(int visitors) {
		this.visitors = visitors;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
