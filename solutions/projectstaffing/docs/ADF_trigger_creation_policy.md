## ADF Triggers

### Trigger types
There are several types of ADF triggers. The ones relevant for us are:
- Schedule Trigger
    - https://docs.microsoft.com/en-us/azure/data-factory/how-to-create-schedule-trigger
    - has the advantage that we can fine tune the times when it should fire (e.g. allows choosing a time on a specific day of week)
    - disadvantage: does not perform backfill
- Time Window Trigger
    - https://docs.microsoft.com/en-us/azure/data-factory/how-to-create-tumbling-window-trigger
    - more complex, allows backfill and retries
    - they recover data from time windows in the past via a property called "backfill", i.e. all trigger runs that would 
      have occurred if the trigger was created at the moment of its start time are run immediately after the actual trigger 
      creation, using a configurable level of parallelism, to "fill back" the windows from the past
        - these backfill runs always occur in chronological order!
    - only starts at specific time intervals in relation to its start date
    - allows defining dependencies to other tumbling window triggers, with customizable offset and window size 
        - https://docs.microsoft.com/en-us/azure/data-factory/tumbling-window-trigger-dependency
    
### Design considerations
Since the employee profiles and inferred roles pipelines completely overwrite their output every time and 
use all available data as input, we should use scheduled triggers for these.  
Since the email data gets processed in an accumulative manner (we accumulate each new batch of emails to the output index), 
a tumbling window trigger fits naturally for this use case.

A challenge is populating the initial data to be used by the ProjectStaffing App Service immediately (or at least as soon 
as possible) after deployment, or after an ingestion mode switch has been performed.  
Since the scheduled triggers do not perform backfill, the first data that they will populate will arrive during the first weekend,
which makes the app unusable until then.  
To compensate for this, we are going to use the backfill ability of the tumbling window triggers, and adjust the start
date and end date in such a way as to process the desired data and to run immediately after they are started.

Another aspect is that the inferred roles pipeline can only run after the emails pipeline successfully populated some data. 
We can use a trigger dependency to achieve this.  
For emails, we want to process all data in a time interval which could start as far back as 10 years ago. 
Since the backfill only works in chronological order, and since the most recent emails are the most relevant, 
we cannot afford for the inferred roles trigger to wait until 10 years of data are processed.  
Also, it would be more relevant for the JGraph app to get more recent emails first and to also get ongoing emails from 
day to day processed first.  
Therefore, the solution is the following: define 3 separate triggers for the emails pipeline, and an additional backfill 
trigger for the inferred roles, to provide the initial output data, for the App Service to work with.

### Trigger definition policies
For emails, we'll have the following tumbling window triggers:
- emails_pipeline_trigger
    - should run on a daily ongoing basis, processing emails in 24h windows
    - has its start date set to 6AM UTC on the most recent day that is in the past
    - no end date
    - 10 minutes delay, to allow processing emails
    - suggestion for concurrency, retry count and interval in seconds (these can be fine-tuned later): 1, 3, 7200
- emails_pipeline_backfill_past_week_trigger
    - should process emails in 24h windows for the last week before deployment time
    - has its end date set to 6AM UTC on the most recent day that is in the past
    - has its start date set to 7 days before end date, 6AM UTC
    - suggestion for concurrency, retry count and interval in seconds (these can be fine-tuned later): 4, 3, 86400
- emails_pipeline_backfill_further_past_trigger
    - should process emails in 24h windows for the last 10 years before deployment time (except last week)
    - has its end date set to start date of emails_pipeline_backfill_past_week_trigger
    - has its start date set to 10 years before start date of emails_pipeline_trigger
    - suggestion for concurrency, retry count and interval in seconds (these can be fine tuned later): 10, 3, 86400
    
For inferred roles:
- inferred_roles_pipeline_backfill_trigger
    - tumbling window
    - it should have its recurrence set to 24 hours 
    - end date set to 6AM UTC on the most recent day that is in the past
    - start date one day before end date
    - should run only once, after the emails_pipeline_backfill_past_week_trigger completed
        - it should have a dependency on that pipeline
        - offset MINUS! six days "-6.00:00:00"
        - window size 7 days "7.00:00:00"
    - suggestion for concurrency, retry count and interval in seconds (these can be fine-tuned later): 1, 3, 86400
- inferred_roles_pipeline_trigger
    - scheduled trigger
    - running daily, 10 AM UTC
    - no end date
  
For employee profiles, it's enough to have a scheduled trigger running every day on an ongoing basis, to get the profiles
of new employees and to refresh the existing employee profiles, and a backfill trigger meant to process the initial 
batch of data, defined similarly to inferred_roles_pipeline_backfill_trigger, but which does not require any dependency.