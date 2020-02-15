using Microsoft.Extensions.Configuration;
using ProjectOrganizer.DAL;
using System;
using System.IO;

namespace ProjectOrganizer
{
    class Program
    {
        static void Main(string[] args)
        {
            // Get the connection string from the appsettings.json file
            IConfigurationBuilder builder = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("appsettings.json", optional: true, reloadOnChange: true);

            IConfigurationRoot configuration = builder.Build();

            string connectionString = configuration.GetConnectionString("Project");

            IProjectDAO projectDAO = null;
            IEmployeeDAO employeeDAO = null;
            IDepartmentDAO departmentDAO = null;

            ProjectCLI projectCLI = new ProjectCLI(employeeDAO, projectDAO, departmentDAO);
            projectCLI.RunCLI();
        }
    }
}
