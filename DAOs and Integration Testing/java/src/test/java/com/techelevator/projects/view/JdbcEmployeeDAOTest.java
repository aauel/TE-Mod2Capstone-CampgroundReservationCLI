package com.techelevator.projects.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.jdbc.JDBCEmployeeDAO;

public class JdbcEmployeeDAOTest {

	private static SingleConnectionDataSource dataSource;
	private JDBCEmployeeDAO dao;
	
	private static final long TEST_EMPLOYEE_ID = 1;
	private static final long TEST_DEPARTMENT_ID = 1;
	private static final long TEST_CHANGE_DEPARTMENT_ID = 2;
	private static final String TEST_FIRST_NAME = "Angie";
	private static final String TEST_LAST_NAME = "Lewis";
	private static final LocalDate TEST_BIRTHDAY = LocalDate.now();
	private static final LocalDate TEST_HIRE_DAY = LocalDate.now().minusDays(50);
	private static final char TEST_GENDER = 'F';
	private static final long TEST_PROJECT_ID = 5;
	private static final int TEST_EMPLOYEES_WITHOUT_PROJECTS = 9;
	
	
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
		jdbcTemplate.update("TRUNCATE employee CASCADE");
		dao = new JDBCEmployeeDAO(dataSource);
	
		for(int i = 0; i < 10; i++) {
			Employee e = createTestEmployee();
			e.setFirstName(e.getFirstName() + i);
			e.setLastName(e.getLastName() + i);
			e.setId((long)i);
			String sqlInsertEmployee = "INSERT INTO employee (employee_id, department_id, first_name, last_name,"
					+ " birth_date, hire_date, gender) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?);";
			
			jdbcTemplate.update(sqlInsertEmployee, e.getId(), TEST_DEPARTMENT_ID, e.getFirstName(), e.getLastName(), 
								TEST_BIRTHDAY, TEST_HIRE_DAY, TEST_GENDER);
		}
		
	}
	
	private Employee createTestEmployee() {
		Employee e = new Employee();
		e.setFirstName(TEST_FIRST_NAME);
		e.setLastName(TEST_LAST_NAME);
		e.setDepartmentId(TEST_DEPARTMENT_ID);
		e.setBirthDay(TEST_BIRTHDAY);
		e.setHireDate(TEST_HIRE_DAY);
		e.setGender(TEST_GENDER);
		return e;
	}
	
	@Test
	public void getAllEmployees_returns_all_employees() {
		List<Employee> employees = dao.getAllEmployees();
		assertNotNull(employees);
		assertEquals(10, employees.size());	
	}
	
	@Test
	public void getEmployeesByDepartmentId_returns_correct_employees() {
		List<Employee> employees = dao.getEmployeesByDepartmentId(TEST_DEPARTMENT_ID);
		assertNotNull(employees);
		assertEquals(10, employees.size());
		for (Employee e : employees) {
			assertEquals(TEST_DEPARTMENT_ID, e.getDepartmentId());
		}
	}
	
	@Test
	public void searchEmployeesByName_returns_correct_employees() {
		List<Employee> employees = dao.searchEmployeesByName(TEST_FIRST_NAME, TEST_LAST_NAME);
		assertNotNull(employees);
		for (Employee e : employees) {
			assertEquals(TEST_FIRST_NAME, e.getFirstName());
			assertEquals(TEST_LAST_NAME, e.getLastName());
		}
	}
	
	@Test
	public void changeEmployeeDepartment_changes_department() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		dao.changeEmployeeDepartment(TEST_EMPLOYEE_ID, TEST_CHANGE_DEPARTMENT_ID);
		String sql = "SELECT department_id FROM employee WHERE employee_id = ?;";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, TEST_EMPLOYEE_ID);
		while(results.next()) {
			assertEquals(TEST_CHANGE_DEPARTMENT_ID, results.getInt("department_id"));
		}
	}
	
	@Test
	public void getEmployeeIdByIdByProjectId_returns_correct_employees() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		String sqlInsert = "INSERT INTO project_employee (project_id, employee_id) "
				 	+ "VALUES (?, ?);";
		jdbcTemplate.update(sqlInsert, TEST_PROJECT_ID, TEST_EMPLOYEE_ID);
		List<Employee> employees = dao.getEmployeesByProjectId(TEST_PROJECT_ID);
		for (Employee e : employees) {
			assertEquals(TEST_EMPLOYEE_ID, e.getId().longValue());
		}
	}
	
	@Test
	public void findEmployeesWithoutProjects_returns_correct_employees() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		String sqlPutOnProject = "INSERT INTO project_employee (project_id, employee_id) "
			 	+ "VALUES (?, ?);";
		jdbcTemplate.update(sqlPutOnProject, TEST_PROJECT_ID, TEST_EMPLOYEE_ID);
		List<Employee> lazyEmployees = dao.getEmployeesWithoutProjects();
		assertEquals(TEST_EMPLOYEES_WITHOUT_PROJECTS, lazyEmployees.size());
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
