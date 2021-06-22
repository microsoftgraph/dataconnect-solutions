import axios from 'axios';
import { SharedStore } from '@/store/modules/shared.store';

export default function setup() {
  let req: Array<boolean> = [];
  axios.interceptors.request.use(
    function(config) {
      if (!config.headers['noLoader']) {
        req.push(true);
        SharedStore.setShowLoader(true);
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
        if (req.length === 0) SharedStore.setShowLoader(false);
      }
      return response;
    },
    error => {
      let errMessage =
        error && error.response && error.response.data
          ? error.response.data.message
          : null;
      let type = 1;
      if (
        error &&
        error.toJSON() &&
        error.toJSON().message === 'Network Error'
      ) {
        console.log('[LOG] - NETWORK ERROR', error.toJSON());
        SharedStore.setWarningModalData({
          visible: true,
          title: 'Session Expired',
          text:
            'Your session has expired. You will have to refresh your browser to start a new session.',
          closable: false
        });
      } else if (!error.config.headers['noFeedbackMessage'])
        SharedStore.setMessage({
          type: type,
          message: errMessage,
          error: error
        });
      if (!error.config.headers['noLoader']) {
        req.pop();
        if (req.length === 0) SharedStore.setShowLoader(false);
      }
      return Promise.reject(error);
    }
  );
}
