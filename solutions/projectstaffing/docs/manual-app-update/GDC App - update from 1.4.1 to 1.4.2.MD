### Staffing App - upgrade from 1.4.1 to 1.4.2

For more context about deployment, please read the "Deployment" section of the [build & deployment documentation](../../deployment/README.MD)  
The following steps must be performed in order:

1. Update the Databricks cluster
    - First uninstall the old jars versions then restart the cluster. After this is done update the cluster with the latest versions of:
        - jars for: airtable-to-hr-data.jar, cleanup-jgraph.jar, latest-reply-extractor.jar, m365-user-to-azure-sql.jar, replace-current-search-index.jar, 
        update-azure-sql-data-version.jar, hr-data-to-azure-sql.jar
        - python script `mail_role_detection_taxo.py`

2. Azure Data Factory updates
    - Delete  `inferred_roles_pipeline_backfill_trigger`, `emails_pipeline_backfill_past_week_trigger` and `further_past_emails_trigger_time_span` before 
    executing the next steps. This can be done from Azure Data Factory UI (Manage -> Triggers).
    - Update ADF pipelines based on the ARM template file (`ADF_update_1_4_1_to_1_4_2.json`)
        - import the provided ARM template json file into ADF (ARM Template -> Import ARM Template -> Build your own template in the editor -> paste provided content)
        - adapt the parameters in the template:
            - `adf.name`- must match the name of the existing ADF resource
            - `pipeline_start_time`- must be set to most recent 6AM UTC date that is in the past
            - `emails.backfill.time.span`- a value between 1 and 3650 which represents the time span for which the emails will be processed
    
    - The value of the `gdc_employees_group_id` ADF global parameter has to be changed to the value of the object id of the `GDC Employees` group. The change can be
    done from the Azure Portal. Follow the steps below in order to make the necessary change:
        - Find the object id of the `GDC Employees` group:
            - Search for `Groups` in the Azure Portal
            - Search by name for the `GDC Employees` group
            - Select the group and copy the `Object Id`
        - Change the `gdc_employees_group_id` global parameter:
            - Search for Azure Data Factory in the Azure Portal
            - Select the data factory from the SkillsFinder App resource group
            - Select `Author & Monitor`
            - Select `Manage` from the for icons on the left
            - Select `Global parameters`
            - Select the `gdc_employees_group_id` global parameter and set the value to be the object id of the `GDC Employees` group

3. Virtual network configurations
    - In the Virtual network resource, from the SkillsFinder App resource group, a subnet has to be modified manually (from the Azure Portal). Follow the steps below in order to
    make the necessary change:
        - Got to section `Subnets`
        - Select the subnet with the name `jgraph-vnet`
        - In the `Service Endpoints` section, add a new Service with the name `Microsoft.Storage`

4. Add `User.Read.All` and `Mail.Read` API permissions to `gdc-m365-reader` service principal. In order to perform this change, the user has to be an owner of the 
`gdc-m365-reader` service principal. If you are not an owner of the `gdc-m365-reader` service principal, contact your administrator for help. 

    The change can be done from the Azure Portal, by following the steps described below:
    - Go to App Registrations
    - Select `All applications` and search for `gdc-m365-reader`
    - After selecting `gdc-m365-reader` from all the listed app registration go to the `API Permissions` section
    - Click on the `Add permissions button`
    - Select `APIs my organization uses` from the `Request API permissions` page
    - Search for `Microsoft Graph` and select it
    - Select `Application permissions`
    - In the list shown, search for `Mail`, and then check the checkbox next to `Mail.Read` permission
    - In the list shown, search for `User`, and then check the checkbox next to `User.Read.All` permission
 
5. Stop SkillsFinder App Service

6. Perform necessary changes to SkillsFinder App Service
    - Add a new env variable EMAILS_BACKFILL_TIME_SPAN with the same value added for `emails.backfill.time.span` parameter in ADF update file
    - Update Jgraph app image by running the following command:
        ```
        az webapp config container set --name <app-service-name> --resource-group <existing_gdc_resource_group_name> --docker-custom-image-name <docker_image_name> --docker-registry-server-url <docker_resitry_url>  -u <username> -p <password>
      ```
  
7. Start SkillsFinder App Service
