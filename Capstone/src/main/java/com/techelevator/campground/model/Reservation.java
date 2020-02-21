package com.techelevator.campground.model;

import java.time.LocalDate;

public class Reservation {

	private int reservation_id;
	private int site_id;
	private String name;
	private LocalDate start_date;
	private int num_days;
	private LocalDate create_date = LocalDate.now();
	
	public int getReservation_id() {
		return reservation_id;
	}
	public void setReservation_id(int reservation_id) {
		this.reservation_id = reservation_id;
	}
	public int getSite_id() {
		return site_id;
	}
	public void setSite_id(int site_id) {
		this.site_id = site_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LocalDate getStart_date() {
		return start_date;
	}
	public void setStart_date(LocalDate start_date) {
		this.start_date = start_date;
	}
	public int getNum_days() {
		return num_days;
	}
	public void setNum_days(int num_days) {
		this.num_days = num_days;
	}
	public LocalDate getCreate_date() {
		return create_date;
	}
	public void setCreate_date(LocalDate create_date) {
		this.create_date = create_date;
	}
	
	
	
}
