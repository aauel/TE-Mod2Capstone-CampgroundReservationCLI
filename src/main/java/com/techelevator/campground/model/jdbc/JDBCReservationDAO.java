package com.techelevator.campground.model.jdbc;

import java.time.LocalDate;
import java.time.Period;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.campground.model.ReservationDAO;

public class JDBCReservationDAO implements ReservationDAO {

	private JdbcTemplate jdbcTemplate;
	
	public JDBCReservationDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	//Inserts the new reservation into the DB
	public int createReservationAndReturnId(String name, int siteId, LocalDate startDate, int days) {
		int result = 0;
		String sql = "INSERT INTO reservation (name, site_id, start_date, num_days) " +
					 "VALUES (?, ?, ?, ?) RETURNING reservation_id;";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, name, siteId, startDate, days);
		while(results.next()) {
			result = results.getInt("reservation_id");
		}
		return result;
	}

}
