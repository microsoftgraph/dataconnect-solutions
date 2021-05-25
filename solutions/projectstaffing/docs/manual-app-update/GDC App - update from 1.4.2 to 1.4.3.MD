### Staffing App - upgrade from 1.4.2 to 1.4.3

For more context about deployment, please read the "Deployment" section of the [build & deployment documentation](../../deployment/README.MD)  
The following steps must be performed in order:

1. Update the Databricks cluster
    - First uninstall the old jars versions then restart the cluster. After this is done update the cluster with the latest versions of:
        - jars for: airtable-to-hr-data.jar, cleanup-jgraph.jar, latest-reply-extractor.jar, m365-user-to-azure-sql.jar, replace-current-search-index.jar, 
        update-azure-sql-data-version.jar, hr-data-to-azure-sql.jar
        - python scripts
    
2. Perform necessary changes to ProjectStaffing App Service
    - Stop ProjectStaffing App Service
    - Update ProjectStaffing app image by running the following command, adapted to your environment, with 1.4.3 image:
        ```
        az webapp config container set --name <app-service-name> --resource-group <existing_gdc_resource_group_name> --docker-custom-image-name <docker_image_name> --docker-registry-server-url <docker_resitry_url>  -u <username> -p <password>
      ```
    - Start ProjectStaffing App Service
