using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ProjectOrganizer.Models
{
    public class Employee
    {
        /// <summary>
        /// The employee id.
        /// </summary>
        public int EmployeeId { get; set; }

        /// <summary>
        /// The department id the employee works for.
        /// </summary>
        public int DepartmentId { get; set; }

        /// <summary>
        /// The job title the employee has.
        /// </summary>
        public string JobTitle { get; set; }

        /// <summary>
        /// The first name of the employee.
        /// </summary>
        public string FirstName { get; set; }

        /// <summary>
        /// The last name of the employee.
        /// </summary>
        public string LastName { get; set; }

        /// <summary>
        /// The employee's birth date.
        /// </summary>
        public DateTime BirthDate { get; set; }

        /// <summary>
        /// The employee's gender.
        /// </summary>
        public string Gender { get; set; }

        /// <summary>
        /// The employee's hire date.
        /// </summary>
        public DateTime HireDate { get; set; }

        
    }
}
