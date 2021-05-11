/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

import axios from 'axios';
import { Module } from 'vuex';
// eslint-disable-next-line no-unused-vars
import { IFrame } from '@stomp/stompjs';

const ingestion: Module<any, any> = {
  state: {
    ingestionModeDetails: {
      ingestionMode: '',
      modeSwitchErrorMessage: '',
      modeSwitchErrorStackTrace: '',
      modeSwitchPaused: false,
      modeSwitchRequester: '',
      modeSwitchPhase: 'completed',
      modeSwitchTimestamp: '',
      logsCorrelationId: ''
    },
    currentIngestionMode: '',
    ingestionSubscription: null,
    ingestionModesOptions: {
      production_mode: 'Production',
      sample_mode: 'Sample',
      simulated_mode: 'Simulated'
    },
    ingestionModeShowModal: true,
    showIngestionModeModal: false,
    ingestionModeModalType: 'resume'
  },
  getters: {
    isCurrentUserAdmin: state => (email: string) => {
      return state.ingestionModeDetails.modeSwitchRequester === email;
    },
    getIngestionModeOptionsArray: state => {
      return Object.keys(state.ingestionModesOptions).map(mode => {
        return {
          value: mode,
          text: state.ingestionModesOptions[mode]
        };
      });
    }
  },
  mutations: {
    SET_INGESTION_MODE_SHOW_MODAL(state, value) {
      state.ingestionModeShowModal = value;
    },
    SET_SHOW_INGESTION_ACTION_MODAL_DATA(state, values) {
      state.showIngestionModeModal = values.show;
      state.ingestionModeModalType = values.type;
    },
    SET_INGESTION_MODE_DETAILS(state, values) {
      state.ingestionModeDetails = { ...state.ingestionModeDetails, ...values };
    },
    SET_INGESTION_MODE(state, mode) {
      state.currentIngestionMode = mode;
    }
  },
  actions: {
    resumeIngestionMode(context) {
      return axios
        .put(`/gdc/admin/ingestion-mode/switch/resume`)
        .then(response => {
          if (response.data) {
            context.dispatch('getIngestionModeDetails', { showModal: false });
          }
        });
    },
    restartIngestionMode(context) {
      return axios
        .put(`/gdc/admin/ingestion-mode/switch/retry`)
        .then(response => {
          if (response.data) {
            context.dispatch('getIngestionModeDetails', { showModal: true });
          }
        });
    },
    updateIngestionMode(context, mode) {
      return axios.put(`/gdc/admin/ingestion-mode`, { mode }).then(response => {
        if (response.data) {
          context.commit('SET_INGESTION_MODE', mode);
          context.commit('SHOW_MESSAGE', {
            type: 3,
            message: `Ingestion Mode updated successfully!`
          });
        }
      });
    },
    getIngestionModeDetails(context, values) {
      return axios
        .get(`/gdc/ingestion-mode-switch-state`, {
          headers: { noLoader: true }
        })
        .then(response => {
          if (response.data) {
            // show modal functionality is not desired everywhere
            context.commit('SET_INGESTION_MODE_SHOW_MODAL', values.showModal);
            context.commit('SET_INGESTION_MODE_DETAILS', response.data);
          }
        });
    },
    subscribeIngestionStatus(context) {
      context.state.ingestionSubscription = context.rootState.webSockets.wsInstance.subscribe(
        `/queue/ingestion-mode-switch-state`,
        (response: IFrame) => {
          context.commit(
            'SET_INGESTION_MODE_DETAILS',
            JSON.parse(response.body)
          );
        }
      );
      context.dispatch('getIngestionModeDetails', { showModal: true });
    }
  }
};

export default ingestion;
