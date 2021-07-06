*Create the notebook*  

```
az synapse notebook create --file @arm/notebook.json --name 'ProcessMails-ConversationLineage' --workspace-name '<Azure Synapse Workspace Name>' --spark-pool-name '<Spark Pool Nam>e'
```

<br>
<br>  

*Create the pipeline*  

```
az synapse pipeline create --file @arm/pipeline.json --name 'ConversationLineage' --workspace-name <Azure Synapse Workspace Name>
```

<br>
<br> 

*Run the pipeline* 

```
az synapse pipeline create-run --workspace-name <Azure Synapse Workspace Name> --name 'ConversationLineage' \
    --parameters '{"sql_server_name": "<SQL Pool Server Name>", "sql_database_name": "<SQL Pool Database Name>", "sql_table_name": "<SQL Pool Table name>", "sql_username": "<SQL Pool Username>", "sql_password": "<SQL Pool Password>", "azure_ai_endpoint": "<Azure Text Analytics URL>", "azure_ai_key": "<Azure Text Analytics Secret Key>"}'
```

<br> 
