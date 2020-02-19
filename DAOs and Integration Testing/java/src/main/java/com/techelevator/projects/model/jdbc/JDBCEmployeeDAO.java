package com.techelevator.projects.model.jdbc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.EmployeeDAO;

public class JDBCEmployeeDAO implements EmployeeDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCEmployeeDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private Employee rowToEmployee(SqlRowSet row) {
		Employee newbie = new Employee();
		newbie.setId(row.getLong("employee_id"));
		newbie.setDepartmentId(row.getLong("department_id"));
		newbie.setFirstName(row.getString("first_name"));
		newbie.setLastName(row.getString("last_name"));
		newbie.setBirthDay(row.getDate("birth_date").toLocalDate());
		newbie.setGender(row.getString("gender").charAt(0));
		newbie.setHireDate(row.getDate("hire_date").toLocalDate());
		return newbie;
	}
	
	
	@Override
	public List<Employee> getAllEmployees() {
		List<Employee> emps = new ArrayList<Employee>();
		String sql = "SELECT * FROM employee;";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while (results.next()) {
			emps.add(rowToEmployee(results));
		}
		return emps;
	}

	@Override
	public List<Employee> searchEmployeesByName(String firstNameSearch, String lastNameSearch) {
		List<Employee> emps = new ArrayList<>();
		String sql = "SELECT * FROM employee WHERE first_name = ? AND last_name = ?;";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, firstNameSearch, lastNameSearch);
		while (results.next()) {
			emps.add(rowToEmployee(results));
		}
		return emps;
	}

	@Override
	public List<Employee> getEmployeesByDepartmentId(long id) {
		List<Employee> emps = new ArrayList<>();
		String sql = "SELECT * FROM employee WHERE department_id = ?;";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
		while (results.next()) {
			emps.add(rowToEmployee(results));
		}
		return emps;
	}

	@Override
	public List<Employee> getEmployeesWithoutProjects() {
		List<Employee> emps = new ArrayList<>();
		String sql = "SELECT * FROM employee "
				   + "LEFT JOIN project_employee ON employee.employee_id = project_employee.employee_id "
				   + "WHERE project_employee.project_id IS NULL;";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while (results.next()) {
			emps.add(rowToEmployee(results));
		}
		return emps;
	}

	@Override
	public List<Employee> getEmployeesByProjectId(Long projectId) {
		List<Employee> emps = new ArrayList<>();
		String sql = "SELECT * FROM employee "
				   + "JOIN project_employee ON employee.employee_id = project_employee.employee_id "
				   + "WHERE project_employee.project_id = ?;";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, projectId);
		while (results.next()) {
			emps.add(rowToEmployee(results));
		}
		return emps;
	}

	@Override
	public void changeEmployeeDepartment(Long employeeId, Long departmentId) {
		String sql = "UPDATE employee SET department_id = ? WHERE employee_id = ?;";
		jdbcTemplate.update(sql, departmentId, employeeId);		
	}

}
