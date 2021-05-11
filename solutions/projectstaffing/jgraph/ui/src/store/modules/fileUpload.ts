/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

import axios from 'axios';
import { Module } from 'vuex';
// eslint-disable-next-line no-unused-vars
import { IFrame } from '@stomp/stompjs';

const fileUpload: Module<any, any> = {
  state: {
    fileUploadState: 'employee_pipeline_run_finished',
    fileUploadSubscription: null
  },
  getters: {},
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
    },
    SET_FILE_UPLOAD_STATUS(state, data) {
      state.fileUploadState = data.hrDataIngestionState;
    }
  },
  actions: {
    getFileUploadStatus(context, values) {
      return axios
        .get(`/gdc/admin/hr-data-upload/state`, {
          headers: { noLoader: true }
        })
        .then(response => {
          if (response.data) {
            context.commit('SET_FILE_UPLOAD_STATUS', response.data);
          }
        });
    },
    subscribeFileUploadStatus(context) {
      context.state.fileUploadSubscription = context.rootState.webSockets.wsInstance.subscribe(
        `/queue/import-hr-data-state`,
        (response: IFrame) => {
          const fileUploadState = JSON.parse(response.body);
          context.commit('SET_FILE_UPLOAD_STATUS', fileUploadState);
          // SHOW POPUP
          if (
            fileUploadState.hrDataIngestionState ===
            'employee_pipeline_run_finished'
          )
            context.commit('SHOW_MESSAGE', {
              type: 3,
              message: `HR Data file upload finished successfully!`,
              delay: 15000
            });
          if (fileUploadState.hrDataIngestionState === 'error')
            context.commit('SHOW_MESSAGE', {
              type: 1,
              message: `HR Data file was not uploaded!`,
              delay: 15000
            });
        }
      );
      context.dispatch('getFileUploadStatus');
    }
  }
};

export default fileUpload;
