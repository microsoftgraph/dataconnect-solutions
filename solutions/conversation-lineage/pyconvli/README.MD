*Project purpose*
- extract conversation analytics from the mail flow between members of a company
- more specifically: extract sentiment and nlp entities with possible purpose of identifying conversation sentiment
over certain topics and write it to an sql table with the goal of exposing the insights through a power-bi presentation notebook.

*Input data*
-  

*Explain requirements.txt
- azure-ai-textanalytics==5.0.0
- html2text (for cleaning mail html body)

*Explain azure text analytics
- provide links
    - [Azure Text Analytics] (https://azure.microsoft.com/en-us/services/cognitive-services/text-analytics/#features)

- explain the returned results

* Explain synapse
    - [Azure Synapse] (https://azure.microsoft.com/en-us/services/synapse-analytics/)

* Explain notebook parameters
  [parameters for debugging](./docs/notebook_parameters_for_debugging.png)
  [parameters for process conversation lineage](./docs/parameters_for_process_conversation_lineage_notebook.png)
*Explain how do we store the data and make it accessible




