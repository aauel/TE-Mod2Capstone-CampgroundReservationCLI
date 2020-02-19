package com.techelevator.projects.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;


import com.techelevator.projects.model.Project;

import com.techelevator.projects.model.jdbc.JDBCProjectDAO;

public class JdbcProjectDAOTest {

		
	private static SingleConnectionDataSource dataSource;
	private JDBCProjectDAO dao;
	
	private static final long TEST_PROJECT_ID = 1;
	private static final String TEST_PROJECT_NAME = "Project";
	private static final String TEST_UNFINISHED_PROJECT = "Unfinished Project";
	private static final String TEST_FINISHED_PROJECT = "Finished Project";
	private static final LocalDate TEST_START_DATE = LocalDate.now().minusDays(100);
	private static final LocalDate TEST_END_DATE = LocalDate.now().minusDays(1);
	private static final LocalDate TEST_FUTURE_START_DATE = LocalDate.now().plusDays(100);
	private static final String TEST_NULL_NULL_PROJECT_NAME = "Null Null Project";
	private static final String TEST_NULL_TODATE_PROJECT_NAME = "Null ToDate Project";
	private static final String TEST_FUTURE_START_DATE_PROJECT_NAME = "Future Start Date Project";
	private static final int ACTIVE_PROJECTS = 1;
	private static final int TEST_EMPLOYEE_ID = 5;
	
	@BeforeClass
	public static void setupDataSource() {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/projects");
		dataSource.setUsername("postgres");
		dataSource.setPassword(System.getenv("DB_PASSWORD"));
		dataSource.setAutoCommit(false);
	}
	
	@Before
	public void setup() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update("TRUNCATE project CASCADE");
		dao = new JDBCProjectDAO(dataSource);
		
		for(int i = 0; i < 5; i++) {
			Project p = createNewProject();
			p.setId(p.getId() + i);
			p.setName(p.getName() + i);
			String sql = "INSERT INTO project (project_id, name, to_date, from_date) "
					     + "VALUES (?, ?, ?, ?);";
			jdbcTemplate.update(sql, p.getId(), p.getName(), TEST_END_DATE, TEST_START_DATE);
		}
	}
	
	private Project createNewProject() {
		Project p = new Project();
		p.setId(TEST_PROJECT_ID);
		p.setName(TEST_PROJECT_NAME);
		p.setStartDate(TEST_START_DATE);
		p.setEndDate(TEST_END_DATE);
		return p;
	}
	
	@Test
	public void getAllActiveProjects_works() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		String sqlAddNullNullProject = "INSERT INTO project (name) VALUES (?);";
		jdbcTemplate.update(sqlAddNullNullProject, TEST_NULL_NULL_PROJECT_NAME);
		
		String sqlAddActiveProject = "INSERT INTO project (name, from_date) VALUES (?, ?);";
		jdbcTemplate.update(sqlAddActiveProject, TEST_UNFINISHED_PROJECT, TEST_START_DATE);
		
		String sqlAddFutureStartDateProject = "INSERT INTO project (name, from_date) VALUES (?, ?);";
		jdbcTemplate.update(sqlAddFutureStartDateProject, TEST_FUTURE_START_DATE_PROJECT_NAME, TEST_FUTURE_START_DATE);
		
		List<Project> projects = dao.getAllActiveProjects();
		assertEquals(ACTIVE_PROJECTS, projects.size());
		assertEquals(TEST_UNFINISHED_PROJECT, projects.get(0).getName());
	}
	
	@Test
	public void addEmployeeToProject_works() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		dao.addEmployeeToProject((long)TEST_PROJECT_ID, (long)TEST_EMPLOYEE_ID);
		String sqlSelectProject = "SELECT project_id, employee_id FROM project_employee WHERE project_id = ?;";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSelectProject, TEST_PROJECT_ID);
		while(results.next()) {
			assertEquals(TEST_EMPLOYEE_ID, results.getInt("employee_id"));
		}
	}
	
	@Test
	public void removeEmployeeFromProject_works() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		dao.addEmployeeToProject((long)TEST_PROJECT_ID, (long)TEST_EMPLOYEE_ID);
		dao.removeEmployeeFromProject((long)TEST_PROJECT_ID, (long)TEST_EMPLOYEE_ID);
		String sql = "SELECT project_id, employee_id FROM project_employee WHERE project_id = ?;";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, TEST_PROJECT_ID);
		assertFalse(results.next());
	}
	
	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}
	
	@AfterClass
	public static void closeDataSource() throws SQLException {
		dataSource.destroy();
	}

}
