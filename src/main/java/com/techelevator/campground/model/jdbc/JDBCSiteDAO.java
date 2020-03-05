package com.techelevator.campground.model.jdbc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.campground.model.Site;
import com.techelevator.campground.model.SiteDAO;

public class JDBCSiteDAO implements SiteDAO {

	private JdbcTemplate jdbcTemplate;
	
	public JDBCSiteDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Site> getTop5AvailableSites(int campgroundId, LocalDate arrivalDate, LocalDate departureDate) {
		List<Site> sites = new ArrayList<Site>();
		String sql = "SELECT site.site_id, site.campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities " + 
					 	//Gets all information for the sites
					 "FROM site " + 
					 "WHERE campground_id = ? AND site.site_id NOT IN " + 
					 "(SELECT site_id FROM reservation " + 
					 "WHERE ((reservation.start_date, reservation.start_date + num_days) OVERLAPS " + 
					 "(DATE(?), DATE(?)))) " + 
					 	//Gets the sites from the chosen campground where the site is NOT already reserved for the
					 	//dates that the new reservation was requested (i.e. makes sure site isn't already booked)
					 "ORDER BY site_number " +
					 "LIMIT 5;";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, campgroundId, arrivalDate, departureDate);
		while(results.next()) {
			sites.add(mapRowToSite(results));
		}
		return sites;
	}
	
	
	private Site mapRowToSite(SqlRowSet row) {
		Site site = new Site();
		site.setSiteId(row.getInt("site_id"));
		site.setCampgroundId(row.getInt("campground_id"));
		site.setSiteNumber(row.getInt("site_number"));
		site.setMaxOccupancy(row.getInt("max_occupancy"));
		site.setAccessible(row.getBoolean("accessible"));
		site.setMaxRvLength(row.getInt("max_rv_length"));
		site.setUtilities(row.getBoolean("utilities"));
		return site;
	}
	
	
	
	
}
