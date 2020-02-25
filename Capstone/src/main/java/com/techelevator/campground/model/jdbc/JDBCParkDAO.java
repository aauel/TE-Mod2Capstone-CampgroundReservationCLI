package com.techelevator.campground.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.campground.model.Campground;
import com.techelevator.campground.model.Park;
import com.techelevator.campground.model.ParkDAO;

public class JDBCParkDAO implements ParkDAO {

	private JdbcTemplate jdbcTemplate;
	
	public JDBCParkDAO (DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	//Gets all parks in system, sorted alphabetically by name
	@Override
	public List<Park> getAllParks() {
		List<Park> parks = new ArrayList<Park>();
		String sql = "SELECT park_id, name, location, establish_date, area, visitors, description "
					+ "FROM park ORDER BY name;";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while(results.next()) {
			parks.add(mapRowToPark(results));
		}
		return parks;
	}
	
	
	//Creates a Park object from a sqlRow
	private Park mapRowToPark(SqlRowSet row) {
		Park park = new Park();
		park.setParkId(row.getInt("park_id"));
		park.setName(row.getString("name"));
		park.setLocation(row.getString("location"));
		park.setEstablishDate(row.getDate("establish_date").toLocalDate());
		park.setArea(row.getInt("area"));
		park.setVisitors(row.getInt("visitors"));
		park.setDescription(row.getString("description"));
		return park;
	}
	
}
