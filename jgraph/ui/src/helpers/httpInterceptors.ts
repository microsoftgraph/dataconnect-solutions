import axios from 'axios';
import sharedStore from '../store/modules/shared';
import settingsStore from '../store/modules/settings';
import store from '../store/index';
import { Commit } from 'vuex';

export default function setup(commit: Commit) {
  let req: Array<boolean> = [];
  axios.interceptors.request.use(
    function(config) {
      if (!config.headers['noLoader']) {
        req.push(true);
        sharedStore.state.showLoader = true;
      }
      config.withCredentials = true;
      return config;
    },
    function(err) {
      return Promise.reject(err);
    }
  );

  axios.interceptors.response.use(
    response => {
      if (!response.config.headers['noLoader']) {
        req.pop();
        if (req.length === 0) sharedStore.state.showLoader = false;
      }
      return response;
    },
    error => {
      // Request user to authorize more permissions
      if (
        error.response.status === 400 &&
        error.response.data &&
        error.response.data.permissionConsentLink
      ) {
        commit(
          'SET_CONSENT_LINK',
          `${error.response.data.permissionConsentLink}&state=redir%3D%2Fsettings%3Ftab%3D${settingsStore.state.tabIndex}`
        );
      } else {
        let errMessage =
          error && error.response && error.response.data
            ? error.response.data.message
            : '';
        let type = 1;
        if (
          error &&
          error.toJSON() &&
          error.toJSON().message === 'Network Error'
        ) {
          console.log('[LOG] - NETWORK ERROR', error.toJSON());
          store.dispatch('showWarningModal', {
            title: 'Session Expired',
            text:
              'Your session has expired. You will have to refresh your browser to start a new session.',
            closable: false
          });
        } else
          commit('SHOW_MESSAGE', {
            type: type,
            message: errMessage,
            error: error
          });
      }
      if (!error.config.headers['noLoader']) {
        req.pop();
        if (req.length === 0) sharedStore.state.showLoader = false;
      }
      return Promise.reject(error);
    }
  );
}
