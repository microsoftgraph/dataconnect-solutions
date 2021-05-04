# Incremental data load - Watermark method

It consists of 3 activities

#### LookUp Activity

https://docs.microsoft.com/en-us/azure/data-factory/control-flow-lookup-activity

This activity is used to get the watermark from the DB.

#### Copy Activity

https://docs.microsoft.com/en-us/azure/data-factory/copy-activity-overview

This activity is used to get the data greater than the current watermark till the current time.

#### SqlServerStoredProcedure Activity

https://docs.microsoft.com/en-us/azure/data-factory/transform-data-using-stored-procedure


This is used to update the watermark and set it to the current timestamp once the copy activity succeeds.

It makes use of TumblingWindowTrigger to schedule the pipeline to run consistently

https://docs.microsoft.com/en-us/azure/data-factory/how-to-create-tumbling-window-trigger

> **NOTE:** The connectionString property under waterMarkLinkedService in ARM template needs to be substituted with the db connection string. (Refer [samplewatermarkdbsetup.sql](samplewatermarkdbsetup.sql) for table and sproc definitions)