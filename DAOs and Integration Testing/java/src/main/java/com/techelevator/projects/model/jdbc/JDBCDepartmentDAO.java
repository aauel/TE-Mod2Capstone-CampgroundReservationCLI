package com.techelevator.projects.model.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.DepartmentDAO;

public class JDBCDepartmentDAO implements DepartmentDAO {
	
	private JdbcTemplate jdbcTemplate;


	public JDBCDepartmentDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	

	@Override
	public List<Department> getAllDepartments() {
		List<Department> departmentList = new ArrayList<Department>();
		String sql = "SELECT * FROM department;";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while(results.next()) {
			departmentList.add(mapRowToDepartment(results));
		}
		return departmentList;
	}

	@Override
	public List<Department> searchDepartmentsByName(String nameSearch) {
		List<Department> departmentList = new ArrayList<Department>();
		String sql = "SELECT * FROM department WHERE name ILIKE ?;";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, "%" + nameSearch + "%");
		while(results.next()) {
			departmentList.add(mapRowToDepartment(results));
		}
		return departmentList;
	}

	@Override
	public void saveDepartment(Department updatedDepartment) {
		Long id = updatedDepartment.getId();
		String updatedName = updatedDepartment.getName();
		String sql = "UPDATE department SET name = ? WHERE department_id = ?;";
		jdbcTemplate.update(sql, updatedName, id);
	}

	@Override
	public Department createDepartment(Department newDepartment) {
		String sql = "INSERT INTO department (name) VALUES (?) RETURNING department_id;";
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, newDepartment.getName());
		if(results.next()) {
			newDepartment.setId(results.getLong("department_id"));
		}
		return newDepartment;
	}

	@Override
	public Department getDepartmentById(Long id) {
		Department d = null;
		String sql = "SELECT * FROM department WHERE department_id = ?;";
		SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
		while(result.next()) {
			d = mapRowToDepartment(result);
		}
		return d;
	}
	
	private Department mapRowToDepartment(SqlRowSet row) {
		Department newDepartment = new Department();
		newDepartment.setId(row.getLong("department_id"));
		newDepartment.setName(row.getString("name"));
		return newDepartment;
	}
	
	

}
