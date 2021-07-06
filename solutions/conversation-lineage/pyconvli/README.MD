#Conversation lineage python scripts

##Goal:
  - extract conversation analytics from the mail flow between members of a company
  - more specifically: extract sentiment and nlp entities with possible purpose of identifying conversation sentiment
  over certain topics and write it to an sql table with the goal of exposing the insights through a power-bi presentation notebook.

##Input data:
- is `dbo.augmented_emails`. An example view is this one [dbo_augmented_mails.png](../docs/dbo_augmented_mails.png)

##Prerequisites
- azure-ai-textanalytics==5.0.0
- html2text (for cleaning mail html body)

###Azure Ai Text Analytics usage.
In order to retrieve the sentiment of the mails content we make use of the `azure-ai-textanalytics` python library.
How:
  - instantiate a class:
    ```python
    text_analytics_client = TextAnalyticsClient(endpoint=azure_ai_endpoint,credential=AzureKeyCredential(azure_ai_key))
    ```
    - `azure_ai_endpoint` must be in the following format: `https://{service_name}.cognitiveservices.azure.com/"`
    - `azure_ai_key` represents the secret key for using the service
  
  - extract the entities:
    ```python
    entities_result = text_analytics_client.recognize_entities(content)
    ```
    - `content` represents a list of strings
    - `entities_result` is a json containing the following fields (more details [here](https://docs.microsoft.com/en-us/azure/cognitive-services/text-analytics/how-tos/text-analytics-how-to-entity-linking?tabs=version-3))
    ```json
        {
          "documents": [
            {
              "id": "1",
              "entities": [
                {
                  "text": "tour guide",
                  "category": "PersonType",
                  "offset": 4,
                  "length": 10,
                  "confidenceScore": 0.45
                },
                {
                  "text": "Space Needle",
                  "category": "Location",
                  "offset": 30,
                  "length": 12,
                  "confidenceScore": 0.38
                },
                {
                  "text": "trip",
                  "category": "Event",
                  "offset": 54,
                  "length": 4,
                  "confidenceScore": 0.78
                },
                {
                  "text": "Seattle",
                  "category": "Location",
                  "subcategory": "GPE",
                  "offset": 62,
                  "length": 7,
                  "confidenceScore": 0.78
                },
                {
                  "text": "last week",
                  "category": "DateTime",
                  "subcategory": "DateRange",
                  "offset": 70,
                  "length": 9,
                  "confidenceScore": 0.8
                }
              ],
              "warnings": []
            }
          ],
          "errors": [],
          "modelVersion": "2020-04-01"
        }
    ```
  - extract the sentiment for the mail content:
    ```python
    sentiment_results = text_analytics_client.analyze_sentiment(content)
    ```
    - `content` represents a list of strings
    - `sentiment_results` is a json containing the following fields (more details [here](https://docs.microsoft.com/en-us/azure/cognitive-services/text-analytics/how-tos/text-analytics-how-to-sentiment-analysis?tabs=version-3-1))
    ```json
      {
        "documents": [
          {
            "id": "1",
            "sentiment": "positive",
            "confidenceScores": {
              "positive": 1,
              "neutral": 0,
              "negative": 0
            },
            "sentences": [
              {
                "sentiment": "positive",
                "confidenceScores": {
                  "positive": 1,
                  "neutral": 0,
                  "negative": 0
                },
                "offset": 0,
                "length": 58,
                "text": "The restaurant had great food and our waiter was friendly.",
                "targets": [
                  {
                    "sentiment": "positive",
                    "confidenceScores": {
                      "positive": 1,
                      "negative": 0
                    },
                    "offset": 25,
                    "length": 4,
                    "text": "food",
                    "relations": [
                      {
                        "relationType": "assessment",
                        "ref": "#/documents/0/sentences/0/assessments/0"
                      }
                    ]
                  },
                  {
                    "sentiment": "positive",
                    "confidenceScores": {
                      "positive": 1,
                      "negative": 0
                    },
                    "offset": 38,
                    "length": 6,
                    "text": "waiter",
                    "relations": [
                      {
                        "relationType": "assessment",
                        "ref": "#/documents/0/sentences/0/assessments/1"
                      }
                    ]
                  }
                ],
                "assessments": [
                  {
                    "sentiment": "positive",
                    "confidenceScores": {
                      "positive": 1,
                      "negative": 0
                    },
                    "offset": 19,
                    "length": 5,
                    "text": "great",
                    "isNegated": false
                  },
                  {
                    "sentiment": "positive",
                    "confidenceScores": {
                      "positive": 1,
                      "negative": 0
                    },
                    "offset": 49,
                    "length": 8,
                    "text": "friendly",
                    "isNegated": false
                  }
                ]
              }
            ],
            "warnings": []
          }
        ],
        "errors": [],
        "modelVersion": "2020-04-01"
      }
    ```  
  - more information about the usage of the cognitive services library can be found here:[Azure Text Analytics](https://azure.microsoft.com/en-us/services/cognitive-services/text-analytics/#features)


## Deployment on azure synapse

The mail sentiment and entity extraction code is deployed in  [Azure Synapse](https://azure.microsoft.com/en-us/services/synapse-analytics/).


* Explain notebook parameters
  [parameters for debugging](../docs/notebook_parameters_for_debugging.png)
  [parameters for process conversation lineage](../docs/parameters_for_process_conversation_lineage_notebook.png)
*Explain how do we store the data and make it accessible




