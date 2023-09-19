Release Notes for Organization Network Analysis (ONA) Microsoft Graph Data Connect Template

v1.0.0
August 1, 2023
- The id column used for deduplication is updated:
  - Teams Chats (BasicDataSet_v0.TeamChat_v1) changed from "id" to "InternetMessageId"
  - Outlook Emails (BasicDataSet_v0.Message_v1) changed from "id" to "InternetMessageId"
  - Outlook Calendar (BasicDataSet_v0.CalendarView_v0) changed from "id" to "iCalUId"
- Added mapping data flow (MDF) support for Calendar dataset to extract in parquet format.

v0.2.0
May 12, 2023
- PowerBI dashboard has new pages on community and org views.
- New ONA Visualization notebook that generates an interactive HTML visualization.
- Template installation experience and documentation improvements.

v0.1.0
March 15, 2023
- Community detection algorithm upgraded from Label Propagation (LPA) to Leiden from graspologic library.
- All ONA algorithms now use weighted edges. The weight is based on the inverse number of message recipients or meeting attendees.
- Added mapping data flow (MDF) support for Users, Teams Chats, and Outlook Emails dataset to extract in parquet format. Outlook Calendar dataset parquet support is pending.
- ONA notebook supports parquet output.

v0.0.0
December 1, 2022
- Initial implementation with ARM template, Synapse pipeline template and PowerBI.