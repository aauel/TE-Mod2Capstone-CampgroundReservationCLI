using ProjectOrganizer.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ProjectOrganizer.DAL
{
    public interface IDepartmentDAO
    {
        /// <summary>
        /// Returns a list of all departments.
        /// </summary>
        /// <returns></returns>
        IList<Department> GetDepartments();

        /// <summary>
        /// Creates a new department.
        /// </summary>
        /// <param name="newDepartment">The department object to create.</param>
        /// <returns>The id of the new department.</returns>
        int CreateDepartment(Department newDepartment);

        /// <summary>
        /// Updates an existing department.
        /// </summary>
        /// <param name="updatedDepartment">The department object to update.</param>
        /// <returns>True, when successful.</returns>
        bool UpdateDepartment(Department updatedDepartment);
    }
}
