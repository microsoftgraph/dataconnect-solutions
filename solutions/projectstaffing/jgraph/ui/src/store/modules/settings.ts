/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

import { Module } from 'vuex';
import axios from 'axios';
import moment from 'moment';

interface XLSXSetting {
  Setting: String;
  Enabled: Boolean | null;
  Value: any;
}

const settings: Module<any, any> = {
  state: {
    tabIndex: 0,
    searchSettingsLegend: {
      PROFILE_SKILLS: 'M365 Profile Skills',
      PROFILE_ABOUT_ME: 'M365 Profile About Me',
      PROFILE_TOPICS: 'M365 Profile Topics',
      EMAIL_CONTENT: 'Emails Content',
      EMAIL_CONTENT_LEMMATIZED: 'Emails content - lemmatized'
    },
    searchSettings: {
      searchResultsFilters: [],
      dataSourceSettings: [
        {
          dataSourcesPriority: [],
          isHRDataMandatory: false
        }
      ],
      searchCriteria: [],
      freshnessEnabled: false,
      freshness: 10,
      volume: 20,
      volumeEnabled: true,
      relevanceScore: 70,
      relevanceScoreEnabled: true,
      freshnessBeginDate: null,
      freshnessBeginDateEnabled: true,
      includedEmailDomains: [],
      includedEmailDomainsEnabled: true,
      excludedEmailDomains: [],
      excludedEmailDomainsEnabled: true,
      useReceivedEmailsContent: true
    },
    defaultSearchSettings: {
      searchCriteria: [],
      dataSourceSettings: {}
    },
    about: {}
  },
  getters: {
    getSearchSettingsLegend: state => {
      return state.searchSettingsLegend;
    },
    mapSearchSettingsToArray: state => {
      const ds: any = state.searchSettings.searchCriteria.find(
        (x: any) => x.searchCriterionType === 'EMAIL_CONTENT'
      );
      let arr: XLSXSetting[] = [
        {
          Setting: 'Active DataSources Ranking',
          Enabled: true,
          Value: state.searchSettings.searchCriteria
            .filter((ds: any) => ds.isActive)
            .map((ds: any) => {
              return `${state.searchSettingsLegend[ds.searchCriterionType]}`;
            })
            .join(', ')
        },
        {
          Setting: 'Include Received Emails Content',
          Enabled:
            ds && ds.isActive
              ? state.searchSettings.useReceivedEmailsContent
              : false,
          Value: null
        },
        {
          Setting: 'Employee Ranking Attribute - Volume',
          Enabled: state.searchSettings.volumeEnabled,
          Value: state.searchSettings.volume
        },
        {
          Setting: 'Employee Ranking Attribute - Freshness',
          Enabled: state.searchSettings.freshnessEnabled,
          Value: state.searchSettings.freshness
        },
        {
          Setting: 'Employee Ranking Attribute - Relevance Score',
          Enabled: state.searchSettings.relevanceScoreEnabled,
          Value: state.searchSettings.relevanceScore
        },
        {
          Setting: 'Process emails newer than',
          Enabled: state.searchSettings.freshnessBeginDateEnabled,
          Value: state.searchSettings.freshnessBeginDate
            ? moment(state.searchSettings.freshnessBeginDate * 1000).format(
                'YYYY-MM-DD'
              )
            : ''
        },
        {
          Setting: 'Include emails from domains',
          Enabled: state.searchSettings.includedEmailDomainsEnabled,
          Value: state.searchSettings.includedEmailDomains.join(' | ')
        },
        {
          Setting: 'Exclude emails from domains',
          Enabled: state.searchSettings.excludedEmailDomainsEnabled,
          Value: state.searchSettings.excludedEmailDomains.join(' | ')
        }
      ];
      return arr;
    }
  },
  mutations: {
    SET_SETTINGS_TAB_INDEX(state, value) {
      state.tabIndex = value;
    },
    SET_SEARCH_SETTINGS(state, settings) {
      state.searchSettings = settings;
    },
    SET_SEARCH_SETTINGS_RANKING(state, ranks) {
      Object.keys(ranks).map(key => {
        state.searchSettings[key] = ranks[key];
      });
      state.searchSettings.freshnessBeginDate = null;
    },
    SET_ABOUT(state, data) {
      state.about = data;
    },
    SET_SEARCH_SETTINGS_SC_RESET(state, data) {
      state.defaultSearchSettings.searchCriteria = data.searchCriteria;
      state.defaultSearchSettings.useReceivedEmailsContent =
        data.useReceivedEmailsContent;
    },
    SET_SEARCH_SETTINGS_RANKING_RESET(state, data) {
      Object.keys(data).map(key => {
        state.defaultSearchSettings[key] = data[key];
      });
    },
    SET_SEARCH_SETTINGS_DS_FILTERS_RESET(state, data) {
      state.defaultSearchSettings.searchResultsFilters =
        data.searchResultsFilters;
    },
    SET_SEARCH_SETTINGS_DS_RESET(state, data) {
      state.defaultSearchSettings.dataSourceSettings.dataSourcesPriority =
        data.dataSourcesPriority;
      state.defaultSearchSettings.dataSourceSettings.isHRDataMandatory =
        data.isHRDataMandatory;
    }
  },
  actions: {
    verifyVersion(context) {
      return axios.get(`/gdc/about`).then(response => {
        if (response.data) {
          return response.data.version !== context.state.about.version;
        }
      });
    },
    getAbout(context) {
      return axios.get(`/gdc/about`).then(response => {
        if (response.data) {
          context.commit('SET_ABOUT', response.data);
        }
      });
    },
    getResetSearchCriteria(context) {
      return axios
        .get(`/gdc/configurations/search-settings/default-search-criteria`)
        .then(response => {
          if (response.data) {
            context.commit('SET_SEARCH_SETTINGS_SC_RESET', response.data);
          }
        });
    },
    getResetDataSourcesFilters(context, values) {
      return axios
        .get(
          `/gdc/configurations/search-settings/default-search-results-filtering-settings${
            values ? `?dataSourcesPriority=${values}` : ''
          }`
        )
        .then(response => {
          if (response.data) {
            context.commit(
              'SET_SEARCH_SETTINGS_DS_FILTERS_RESET',
              response.data
            );
          }
        });
    },
    getResetDataSources(context) {
      return axios
        .get(`/gdc/configurations/search-settings/default-data-source-settings`)
        .then(response => {
          if (response.data) {
            context.commit('SET_SEARCH_SETTINGS_DS_RESET', response.data);
          }
        });
    },
    getResetRankingValues(context) {
      return axios
        .get(`/gdc/configurations/search-settings/default-employee-ranking`)
        .then(response => {
          if (response.data) {
            context.commit('SET_SEARCH_SETTINGS_RANKING_RESET', response.data);
          }
        });
    },
    getSearchSettings(context) {
      return axios.get(`/gdc/configurations/search-settings`).then(response => {
        if (response.data) context.commit('SET_SEARCH_SETTINGS', response.data);
      });
    },
    saveSearchSettings(context, configurations) {
      return axios
        .post(`/gdc/configurations/search-settings`, configurations)
        .then(response => {
          if (response.data) {
            context.commit('SHOW_MESSAGE', {
              type: 3,
              message: `Settings updated successfully!`
            });
          }
        });
    },
    uploadHRDataCSV(context, file) {
      let formData = new FormData();
      formData.append('file', file);
      return axios
        .post(`/gdc/admin/hr-data-upload`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        .then(resp => {
          context.commit('SHOW_MESSAGE', {
            type: 3,
            message: `Uploading and processing new HR Data file in the background.`,
            delay: 7000
          });
          return true;
        })
        .catch(err => {
          return false;
        });
    }
  }
};

export default settings;
