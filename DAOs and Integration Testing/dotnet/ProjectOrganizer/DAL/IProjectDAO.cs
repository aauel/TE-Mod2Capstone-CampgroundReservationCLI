using ProjectOrganizer.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ProjectOrganizer.DAL
{
    public interface IProjectDAO
    {
        /// <summary>
        /// Returns all projects.
        /// </summary>
        /// <returns></returns>
        IList<Project> GetAllProjects();

        /// <summary>
        /// Assigns an employee to a project.
        /// </summary>
        /// <param name="projectId">The project id.</param>
        /// <param name="employeeId">The employee id.</param>
        /// <returns></returns>
        bool AssignEmployeeToProject(int projectId, int employeeId);

        /// <summary>
        /// Removes an employee from a project.
        /// </summary>
        /// <param name="projectId">The project's id.</param>
        /// <param name="employeeId">The employee's id.</param>
        /// <returns>If it was successful.</returns>
        bool RemoveEmployeeFromProject(int projectId, int employeeId);

        /// <summary>
        /// Creates a new project.
        /// </summary>
        /// <param name="newProject">The new project.</param>
        /// <returns>The new id of the project.</returns>
        int CreateProject(Project newProject);
    }
}
