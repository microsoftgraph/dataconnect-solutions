# Incremental data load - Tumbling Window

This form of incremental data load uses slices of time to extract data. 

### Tumbling Window Trigger

https://docs.microsoft.com/en-us/azure/data-factory/how-to-create-tumbling-window-trigger

The Tumbling Window trigger is used to periodically schedule pipelines on defined time windows and it guarantees execution.

### Sync Modes

The samples currently perform:

1. **Backfill**: This is configured to pull the last `backfillNumberOfDays` (30 days be default)
2. **Incremental Sync**: This is configured to a daily pull based on the sync window.

### Scenarios

This is useful when you are looking to independent data pulls based on time slices regardless of the previous pull. Essentially, if the pipeline from Jan 1st fails (after retries etc...), the pipeline from Jan 2nd, will still run as needed. IF, your scenario requires data to be pulled consistently from the last sync please use the watermark [sample](https://github.com/OfficeDev/MS-Graph-Data-Connect/ARMTemplates/incrementaldataload-sqlwatermark-sample)