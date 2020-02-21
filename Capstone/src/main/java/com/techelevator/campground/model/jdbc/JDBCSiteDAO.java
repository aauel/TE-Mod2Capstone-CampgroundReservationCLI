package com.techelevator.campground.model.jdbc;

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
	public List<Site> availableSites(String campground_name, String arrivalDate, String departureDate) {
		List<Site> sites = new ArrayList<Site>();
		String sql = "SELECT DISTINCT site.site_id, site.campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities " + 
					 "FROM site " + 
					 "JOIN campground ON campground.campground_id = site.campground_id " + 
					 "LEFT JOIN reservation ON site.site_id = reservation.site_id " + 
					 "WHERE campground.name = ? AND site.site_id NOT IN " + 
					 "(SELECT site_id FROM reservation " + 
					 "WHERE ((reservation.start_date, reservation.start_date + num_days) OVERLAPS " + 
					 "(DATE(?), DATE(?)))) " + 
					 "ORDER BY site_number " +
					 "LIMIT 5;";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, campground_name, arrivalDate, departureDate);
		while(results.next()) {
			sites.add(mapRowToSite(results));
		}
		return sites;
	}
	
	
	private Site mapRowToSite(SqlRowSet row) {
		Site site = new Site();
		site.setSite_id(row.getInt("site_id"));
		site.setCampground_id(row.getInt("campground_id"));
		site.setSite_number(row.getInt("site_number"));
		site.setMax_occupancy(row.getInt("max_occupancy"));
		site.setAccessible(row.getBoolean("accessible"));
		site.setMax_rv_length(row.getInt("max_rv_length"));
		site.setUtilities(row.getBoolean("utilities"));
		return site;
	}
	
	
	
	
}
