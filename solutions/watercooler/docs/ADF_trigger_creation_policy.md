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
Since the watercooler events pipeline completely overwrite its output every time and 
use all available data as input, we should use scheduled triggers for these.  

### Trigger definition policies
For watercooler events pipeline, we'll have the following tumbling window triggers:
- watercooler events pipeline
    - should run on a weekly ongoing basis, processing new calendar events for the forthcoming week
    - has its start date set to 6AM UTC on the most recent day that is in the past
    - no end date
    - suggestion for concurrency, retry count and interval in seconds (these can be fine-tuned later): 1, 3, 7200
