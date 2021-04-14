# dataconnect-solutions

## SkillsFinder application

The purpose of the SkillsFinder application is to allow engagement managers to build the best suited teams for projects, by finding the employees that have the best set of skills for the project, as well as the best set of potential skills.

The application ingests data from several data sources offline using ADF pipelines and uses this to build a model based on which the most relevant employees are recommended.

The following data sources are used:
1. M365 User profiles
   - data retrieved using Graph Data Connect (GDC)
2. M365 Emails
   - sent and received emails retrieved using Graph Data Connect (GDC)
3. HR Data employee profiles
   - custom data format which can be derived from the systems used by the HR department to store data about the company's employees
   - it can contain information which
     - is not contained in the M365 user schema (e.g. availableStartingFrom, currentEngagement, employeeType, linkedInProfile)
     - for some reason is not usually filled in M365, but is supported by M365 schema (e.g. role, department, companyName)
   - the data should have the following schema:
     - mail, name, availableStartingFrom, role, employeeType, currentEngagement, department, companyName, managerName, managerEmail, country, state, city, location, officeLocation, linkedInProfile
   - the data must be in csv format
   - the data can be delivered in one or more files, as long as they have the correct schema
   - the files can either be manually uploaded to the deployment specific AZBS path or, if a single file is used, it can be uploaded by an admin from the application UI, from Settings->Upload HR Data
   - the header should be included in every file
   - any fields containing commas or special characters must be quoted between double quotes
   - quote characters within a quoted field's contents can be escaped by doubling them (e.g. "John ""Anonymous"" Doe")
   - the mail and name columns must be populated, or the entire row will be ignored
   - dates must be in yyyy-mm-dd format (e.g. 2021-01-31)
   - if the "availableStartingFrom" field is not filled for an employee, that person is considered to be available effective immediately
