# **PBI Report Template**

- [Setup](#Setup)
- [Data Source Updates](#Data-Source-Updates)
- [Usage](#Usage)

## Setup

Below steps will help to link datasets that are generated using Synapse pipeline above to link to PowerBI 
Template. 
1. Download and install Microsoft Power BI Desktop if you don’t have it installed already on your machine. 
    - Link to download Download Microsoft Power BI Desktop from Official Microsoft Download Center [here](https://www.microsoft.com/en-us/download/details.aspx?id=58494).
2. Download the pre-created PowerBI report from this folder.

![](Images/EsaPbiTemplateDownload.png)

3. Open the PowerBI file and click on Transform data → Data source settings.

![](Images/DataSourceSettings.png)

4. You will see 1 data source in the Data source settings page.

![](Images/ChangeSource.png)

5. Select it and click on Change Source. Change the Storage account path in URL with right storage account that data is generated from synapse pipeline in the steps above. You can get the storage account that is used in Synapse template pipeline. **Make sure the URL says dfs.core.windows.net, not blob.core.windows.net. If it says blob, change it to dfs.**

![](Images/ADLStorageURLChange.png)

6. Set the right storage account key / credentials for these data sources.
    - Click on Edit Permissions.

        ![](Images/EditPermissions1.png)        

    - Click on Edit under credentials.

        ![](Images/EditPermissions.png)        

    - Enter the storage account key value.

        ![](Images/ProvideAccountKey.png)        

    - The storage key can be retrieved by navigating to the storage account in azure portal (storage account → access keys).

        ![](Images/4.4.d.png)        

7. Confirm the Source and Navigation steps in each data source match to the folder location and file ID.

![](Images/5.1.png)

![](Images/5.2.png)

8. You may need to click on Apply Changes or Refresh the data.

![](Images/ClickApplyChanges.png)

![](Images/refresh.png)

9. Congratulations, you are all set and will see that the report will be refreshed with the latest data.

    ![](Images/WelcomePage.png) 

## Data Source Updates

To update the data source to point at a different file, these steps are recommended:

1. Within the Power Query portal from Transform data → Data source settings, duplicate the query that you are going to update.

  ![](Images/5.3.png) 

2. From the duplicated query, replace the URL in the Source step and click on the Binary content:

  ![](Images/5.4.png) 

3. Continue with replacing the next steps in the duplicated query.

  ![](Images/5.5.png) 

4. Copy the Source and Navigation formulas into the main query in the same steps.

  ![](Images/5.6.png) 

5. In the Navigation step, double click the file and proceed inserting a new step.

  ![](Images/5.7.png) 

6. Delete the old Imported step (and any other that applied to the previous data only) and save & apply changes. 

## Usage

**Overview Page**

This page gives you a high-level view of all the data represented on a simple table. Keep in mind that we’ve filtered each widget to only provide the Top 100 keywords by Mention. Otherwise, there would be thousands of not-applicable keywords and keywords that have only been mentioned once. Accompanying the table are the three keywords with the Most Mentions, Most Positive Mentions, and Most Negative Mentions.

![](Images/OverviewPage.png) 

**Sentiment Scatter Plot Page**

This scatter plot is based on each keyword’s Sentiment Score and its number of Mentions. The dots are colored according to General Sentiment to ease reviewing the data. This is especially helpful for differentiating between the Neutral, Mixed, and Polarized general sentiments, but also emphasizes the differences between Positive and Very Positive, and Negative and Very Negative. Additionally, instead of having to remember each keyword and search for it on the View keyword page, you can get a quick snapshot of it by mousing over the datapoint and bringing up its keyword Tooltip. In this way, you can quickly check the data while having it all on the board. 

![](Images/SentimentScatterPlotPage.png) 

**Conflate Keywords Page**

You’ll quickly realize that the entities aren’t always aggregated perfectly. Senders may be discussing the same keyword, but not using the same word. In a dataset about airline reviews, for instance, you might find a lot of posts about baggage-handling. The Senders could use words “bag,” “bags,” or “baggage” in reference to it. For situations like these, we’ve provided the Conflate keywords page.  Select the keywords that are alike in the slicer at the top left, and you’ll find your grouping on the table to its immediate right. All of the data displayed is an aggregate of the provided keywords. Using this tool, you can merge keywords together that are similar, but not identical. 

![](Images/MergeAlikeTopicsPage.png) 

**Compare Keywords**

Just like with the Merge Alike keywords page, select which keywords you’d like to compare in the slicer in the top left and they’ll be added to the table to its immediate right. All of the visuals on this page will show the group of keywords you’ve selected.

![](Images/CompareTopicsPage.png) 

**View Keyword**

For an in-depth view of a single keyword, visit the View keyword page. It provides a more readable interface than a table. By showing the various metric values, plus a couple graphs for how mentions and sentiment have changed over time, you’ll gain a better understanding of the chosen keyword. Perhaps you'll even pick up correlations between time and popularity. 

![](Images/ViewTopicPage.png) 

