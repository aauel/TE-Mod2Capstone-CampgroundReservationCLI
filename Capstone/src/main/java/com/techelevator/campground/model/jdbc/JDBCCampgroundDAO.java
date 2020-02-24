package com.techelevator.campground.model.jdbc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.campground.model.Campground;
import com.techelevator.campground.model.CampgroundDAO;
import com.techelevator.campground.model.Park;

public class JDBCCampgroundDAO implements CampgroundDAO {
	
	private JdbcTemplate jdbcTemplate;

	public JDBCCampgroundDAO (DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	//Gets all campgrounds in a chosen park
		@Override
		public List<Campground> getCampgroundsInPark(int chosen_park_id) {
			List<Campground> campgrounds = new ArrayList<Campground>();
			String sql = "SELECT campground.campground_id, campground.park_id, campground.name, " +
						 "campground.open_from_mm, campground.open_to_mm, campground.daily_fee "
					   + "FROM campground " 
					   + "JOIN park ON campground.park_id = park.park_id "
					   + "WHERE park.park_id = ? ORDER BY campground.name;";
			SqlRowSet results = jdbcTemplate.queryForRowSet(sql, chosen_park_id);
			while(results.next()) {
				campgrounds.add(mapRowToCampground(results));
			}
			return campgrounds;
		}

		@Override
		public Campground getCampgroundByCampgroundId(int campground_id) {
			Campground camp = new Campground();
			String sql = "SELECT campground_id, park_id, name, open_from_mm, open_to_mm, daily_fee " +
						 "FROM campground WHERE campground_id = ?;";
			SqlRowSet result = jdbcTemplate.queryForRowSet(sql, campground_id);
			while(result.next()) {
				camp = mapRowToCampground(result);
			}
			return camp;
		}
		
		@Override
		public BigDecimal getDailyFeeByCampgroundId(int campground_id) {
			String slq = "SELECT daily_fee FROM campground WHERE campground_id = ?;";
			SqlRowSet result = jdbcTemplate.queryForRowSet(slq, campground_id);
			result.next();
			return result.getBigDecimal("daily_fee");
		}
		
		//Creates a Campground object from a sqlRow
		private Campground mapRowToCampground(SqlRowSet row) {
			Campground campground = new Campground();
			campground.setCampground_id(row.getInt("campground_id"));
			campground.setPark_id(row.getInt("park_id"));
			campground.setName(row.getString("name"));
			campground.setOpen_from_mm(row.getString("open_from_mm"));
			campground.setOpen_to_mm(row.getString("open_to_mm"));
			campground.setDaily_fee(row.getBigDecimal("daily_fee"));
			return campground;
		}
		
	
}
