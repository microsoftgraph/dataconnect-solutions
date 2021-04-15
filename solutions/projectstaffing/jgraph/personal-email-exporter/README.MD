# Personal Email exporter tool
The following documentation approaches the ```personal-email-exporter.jar``` script usage.  

# What is it ?
The ```personal email exporter``` tool is a simple java application meant to be ran from command line.  
The tool will export one user's emails from every directory (Inbox, Sent Items, Archived, CustomDirectory etc) into a jsonl file store on disk.  
The emails that get exported can be filtered by the time at which they were created/modified/sent/received and optionally by subject.  
No data will be shared with any 3rd party application while running the tool.

# Prerequisites  
- minimum required java version: 8  

# How to use
#### Run the script from command line
```
java -jar personal-email-exporter.jar -start 2020-08-01 -end 2020-08-07
```
```
java -jar personal-email-exporter.jar -startDate 2020-06-15 -endDate 2020-07-01
```
```
java -jar personal-email-exporter.jar -start 2020-08-01 -end 2020-08-07 -subject "Azure Devops"
```  
```
java -jar personal-email-exporter.jar -s 2020-08-01 -e 2020-08-07 -subject "Teams"
```  

### Authenticate with Active Directory credentials
The script is generating a code (Eg: FWFTNE22T) and asking you to enter the code at https://microsoft.com/devicelogin  
Script message example:  ```To sign in, use a web browser to open the page https://microsoft.com/devicelogin and enter the code FWFTNE22T to authenticate.```

### Check the output file
The output will be written into a JSON Line file (.jsonl) in your current directory.  
Check the data exported before sharing the file with anyone else.

## Help  
```
java -jar personal-email-exporter.jar --help
```

## Arguments
- **-field** `[Optional]` also can be used as **-timeFilterField**  
Defines the email field used to perform time based filtering on, by comparing it with the **-startDate** and **-endDate** arguments  
Available options are: SENT_DATE_TIME, RECEIVED_DATE_TIME, LAST_MODIFIED_DATE_TIME or CREATED_DATE_TIME  
Default value: **RECEIVED_DATE_TIME**  
Values example -field SENT_DATE_TIME  or -timeFilterField LAST_MODIFIED_DATE_TIME or -field "CREATED_DATE_TIME"

- **-startDate** `[Mandatory]` also can be used as **-start** or **-s** or **-S**  
Defines the oldest point in time up to which emails will be selected. Emails OLDER than **startDate** won't be read.  
The argument value should be in  'yyyy-MM-dd' format. The actual time of day gets set to 23:59:59 AM UTC on the provided date
The emails from the entire provided day `(in UTC timezone)` will be included in the output  
Value examples: -startDate 2020-07-01 or -start 2020-02-30  

- **-endDate** `[Mandatory]` also can be used as **-end** or **-e** or **-E**  
Defines the most recent point in time up to which emails will be selected. Emails NEWER than **endDate** won't be read.  
The argument value should be in 'yyyy-MM-dd' format. The actual time of day gets set to 23:59:59 UTC on the provided date
The emails from the entire provided day `(in UTC timezone)` will be included in the output  
Value examples: -endDate 2020-03-15 or -end 2020-08-06

- **-subject** `[Optional]`  
Defines an optional case insensitive subject filter.  
If provided, the argument value should be contained within the subject of selected emails.  
The argument value should be double-quoted, especially if it contains multiple words  
Default value: **null** `[Subject filter does not apply]`  
Value examples: -subject "Azure Devops"  or -subject "Microsoft azure"  


# Output  
The output will be written into a file in your current directory.   
Output file name example: ```michael_emails_from_2020-08-01_to_2020-08-02.jsonl``` or ```john_emails_from_2020-04-02_to_2020-06-21.jsonl```
