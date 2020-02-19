package com.techelevator.projects.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.jdbc.JDBCDepartmentDAO;


public class JdbcDepartmentDAOTest {

	private static SingleConnectionDataSource dataSource;
	private JDBCDepartmentDAO dao;
	
	private static final long TEST_DEPARTMENT_ID = -1;
	private static final String TEST_DEPARTMENT_NAME = "Test Department";
	
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
		jdbcTemplate.update("TRUNCATE department CASCADE");
		String sqlInsertDepartment = "INSERT INTO department (department_id, name) VALUES (?, ?);";
		
		jdbcTemplate.update(sqlInsertDepartment, TEST_DEPARTMENT_ID, TEST_DEPARTMENT_NAME);
		
		dao = new JDBCDepartmentDAO(dataSource);
	}
	
	@Test
	public void getAllDepartments_returns_all_departments() {
		for (int i = 0; i < 10; i++) {
			Department d = createTestDepartment();
			d.setName(d.getName() + i);
			dao.createDepartment(d);
		}
		
		List<Department> departments = dao.getAllDepartments();
		
		assertNotNull(departments);
		assertEquals(11, departments.size());	
	}
	
	@Test
	public void getDepartmentById_returns_correct_department() {
		Department foundDepartment = dao.getDepartmentById(TEST_DEPARTMENT_ID);
		
		assertEquals(TEST_DEPARTMENT_NAME, foundDepartment.getName());
	}
	
	@Test
	public void getDepartmentByName_returns_correct_departments() {
		for (int i = 0; i < 10; i++) {
			Department d = createTestDepartment();
			d.setName(d.getName() + i);
			dao.createDepartment(d);
		}
		List<Department> departmentList = dao.searchDepartmentsByName("Test");
		assertNotNull(departmentList);
		assertEquals(11, departmentList.size());
	}
	
	@Test
	public void update_and_read_back_department() {
		Department d = createTestDepartment();
		d.setName("Test Name 2");
		dao.saveDepartment(d);
		Department d2 = dao.getDepartmentById(d.getId());
		
		assertEquals("Test Name 2", d2.getName());
	}
	
	private Department createTestDepartment() {
		Department d = new Department();
		d.setName(TEST_DEPARTMENT_NAME);
		d.setId(TEST_DEPARTMENT_ID);
		
		return d;
	}
	
	@AfterClass
	public static void closeDataSource() throws SQLException {
		dataSource.destroy();
	}
	
	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}
	
}
