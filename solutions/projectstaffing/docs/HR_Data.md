## HR Data
HR Data is a data source which is meant to be complementary to the data obtained from M365 (Graph Data Connect)
It is a custom data format which can be derived from the systems used by the HR department to store data about the company's employees:
- it can contain information which
    - is not contained in the M365 user schema (e.g. `availableStartingFrom`, `currentEngagement`, `employeeType`, `linkedInProfile`)
    - for some reason is not usually filled in M365, but is supported by M365 schema (e.g. `role`, `department`, `companyName` etc )
- the data should have the following schema (i.e. each record should to contain the following string fields)
    - `mail` the email address of the employee
    - `name` the full name of the employee (first name + last name)
    - `availableStartingFrom` the date starting from which the employee is available to work on a new project. The format must be `YYYY-MM-DD`
    - `role` the role/job title of the employee in the company
    - `employeeType` this field can describe if the employee is a contractor, consultant, part/full-time employee etc.
    - `currentEngagement` the name of the team in which the employee is a member
    - `department`  the name of the department the employees is a part of
    - `companyName` the name of the company for which the employee works
    - `managerName` the name of the person that acts as the employee's manager
    - `managerEmail` the manager's email address
    - `country` the country where the employee works
    - `state` the state where the employee works
    - `city` the city where the employee works
    - `location` a string representing all the location information regarding the employee (e.g. "city, state, country"). Useful for companies which need more flexibility or which don't store the country, state and city as separate fields in their HR systems
    - `officeLocation` a string representing the location of the office from which the employee works
    - `linkedInProfile` a link to the LinkedIn profile of the employee
- the header, like the one below, should be included as the first row in every file
    - `mail,name,availableStartingFrom,role,employeeType,currentEngagement,department,companyName,managerName,managerEmail,country,state,city,location,officeLocation,linkedInProfile`
- the data must be in csv format
- the data can be delivered in one or more files, as long as they have the correct schema
- the files can either be manually uploaded to the deployment specific AZBS path (the `hr_data` folder, of the 
  `production-data`/`simulated-data`/`sample-data` container, in the `demodata<deploymenthash>` storage account) or, 
  if a single file is used, it can be uploaded by an admin from the application UI, from `Settings -> Upload HR Data`
- any fields containing commas or special characters must be quoted between double quotes
- quote characters within a quoted field's contents can be escaped by doubling them (e.g. "John ""Anonymous"" Doe")
- the mail and name columns must be populated, or the entire row will be ignored
- the dates in the `availableStartingFrom` field must be in `yyyy-MM-dd` format (e.g. 2021-01-31)
- if the `availableStartingFrom` field is not filled for an employee, that person is considered to be available effective immediately