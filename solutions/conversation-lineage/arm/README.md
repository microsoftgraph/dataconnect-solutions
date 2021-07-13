### ARM deployment

In order to deploy all the synapse components (linked services, pipelines, datasets, notebooks & triggers) run the following command from the `arm` directory, after replacing the values of the parameters with the values specific to the environment where the deployment will take place:

```
./deploy.sh --workspace-name "<synapse-workspace-name>" --spark-pool-name "<spark-pool-name>" --start-date "YYYY-MM-DDTHH:mm:ssZ" --key-vault-endpoint "https://<keyvault-name>.vault.azure.net/" --storage-account-endpoint "<storage-account-endpoint>"   --storage-account-secret-name "<storage-account-key-secret-name>" --service-principal-tenant-id "<tenant-id>"--service-principal-id "<sp-id>" --service-principal-secret-name "<sp-secret-name>" --dedicated-sql-pool-endpoint "<dedicated-sql-pool-endpoint>" --sql-pool-database-name "<database-name>" --azure-ai-endpoint "<azure-ai-endpoint>" --m365-extraction-group-id "<group_id>"
```