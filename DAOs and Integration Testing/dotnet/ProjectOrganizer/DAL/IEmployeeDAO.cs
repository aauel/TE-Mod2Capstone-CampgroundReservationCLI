using ProjectOrganizer.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ProjectOrganizer.DAL
{
    public interface IEmployeeDAO
    {
        /// <summary>
        /// Returns a list of all of the employees.
        /// </summary>
        /// <returns>A list of all employees.</returns>
        IList<Employee> GetAllEmployees();

        /// <summary>
        /// Searches the system for an employee by first name or last name.
        /// </summary>
        /// <remarks>The search performed is a wildcard search.</remarks>
        /// <param name="firstname"></param>
        /// <param name="lastname"></param>
        /// <returns>A list of employees that match the search.</returns>
        IList<Employee> Search(string firstname, string lastname);

        /// <summary>
        /// Gets a list of employees who are not assigned to any active projects.
        /// </summary>
        /// <returns></returns>
        IList<Employee> GetEmployeesWithoutProjects();
    }
}
