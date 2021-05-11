/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

export const constants = {
  ingestion: {
    talentPool: {
      info:
        'The system is processing new data in the background, therefore the search functionality might return incomplete results or no results at all.',
      pausedAdmin:
        'Processing new data is paused due to a recent application restart. Resuming data processing requires admin intervention.',
      failedAdmin:
        'Processing new data failed. The procedure must be restarted by an admin.',
      pausedNonAdmin:
        'Processing new data is paused due to a recent application restart. Please contact an administrator to resume data processing.',
      failedNonAdmin:
        'Processing new data failed. Please contact an administrator to resume data processing.'
    },
    settings: {
      failed:
        'Switching the data ingestion mode failed. The procedure must be restarted by an admin. Would you like to restart the data ingestion mode switch now or further investigate the cause of the failure first?',
      paused:
        'Switching the data ingestion mode is paused due to a recentapplication restart. Resuming data processing requires admin intervention. Would you like to resume the process now from where it left off?',
      warning:
        'Switching the data ingestion mode will delete all data processed in the previous mode. Depending on the situation, retrieving the initial batch of data may take several minutes or even hours. During the data retrieval process, the search functionality migth return incomplete results or no results at all.',
      ongoing:
        'Currently switching to a new data ingestion mode. This process deletes all data processed in the previous mode. Depending on the situation, retrieving the initial batch of data may take several minutes or even hours. During the data retrieval process, the search functionality might return incomplete results.'
    },
    modal: {
      paused:
        'Switching the data ingestion mode is paused due to a recent application restart. Resuming data processing requires admin intervention. Would you like to resume the process now from where it left off?',
      failed:
        'Switching the data ingestion mode failed. The procedure must be restarted by an admin. Would you like to restart the data ingestion mode switch now or further investigate the cause of the failure first?'
    }
  },
  settings: {
    facets: {
      country: 'Country',
      state: 'State',
      city: 'City',
      location: 'Location',
      department: 'Department',
      HRData: 'HR Data',
      M365: 'M365',
      hr_data_locate: 'Location',
      office_location: 'Office Location',
      company_name: 'Company Name',
      m365_country: 'Country',
      m365_department: 'Department',
      m365_state: 'State',
      m365_role: {
        searchLabel: 'Role',
        settingsLabel: 'Role'
      },
      m365_city: 'City',
      m365_office_location: 'Office Location',
      m365_company_name: 'Company Name',
      hr_data_location: 'Location',
      hr_data_role: {
        searchLabel: 'HR Data Role',
        settingsLabel: 'Role'
      }
    }
  }
};
