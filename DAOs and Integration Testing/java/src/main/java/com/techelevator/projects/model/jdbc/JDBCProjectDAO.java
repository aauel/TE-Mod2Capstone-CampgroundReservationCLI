package com.techelevator.projects.model.jdbc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.cglib.core.Local;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.ProjectDAO;

public class JDBCProjectDAO implements ProjectDAO {

	private JdbcTemplate jdbcTemplate;
	
	private Project rowToProject(SqlRowSet row) {
		Project p = new Project();
		p.setId(row.getLong("project_id"));
		p.setName(row.getString("name"));
		p.setStartDate(row.getDate("from_date").toLocalDate());
		if (row.getDate("to_date") != null) {
			p.setEndDate(row.getDate("to_date").toLocalDate());
		}
		return p;
	}

	public JDBCProjectDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Project> getAllActiveProjects() {
		List<Project> projectList = new ArrayList<Project>();
		LocalDate currentDate = LocalDate.now();
		String sql = "SELECT * FROM project WHERE from_date IS NOT NULL AND (from_date < ?)"
					+ "AND (to_date IS NULL OR to_date > ?);";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, currentDate, currentDate);
		while(results.next()) {
			projectList.add(rowToProject(results));
		}
		return projectList;
	}

	@Override
	public void removeEmployeeFromProject(Long projectId, Long employeeId) {
		String sql = "DELETE FROM project_employee WHERE project_id = ? AND employee_id = ?;";
		jdbcTemplate.update(sql, projectId, employeeId);
	}

	@Override
	public void addEmployeeToProject(Long projectId, Long employeeId) {
		String sql = "INSERT INTO project_employee (project_id, employee_id) "
					+ "VALUES (?, ?)";
		jdbcTemplate.update(sql, projectId, employeeId);
	}

}
