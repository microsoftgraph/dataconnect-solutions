import { Module } from 'vuex';
import moment from 'moment';

const shared: Module<any, any> = {
  state: {
    message: {},
    showLoader: false,
    passwordRegex: /^(?=.{8,})((?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[a-zA-Z])(?=.*[\W_])(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_])).*/,
    warningModal: false,
    alertModal: {
      title: '',
      text: '',
      closable: false
    },
    mobileSelectedIndex: 0,
    headers: {
      TeamModeler: {
        title: 'Talent Pool',
        actions: []
      },
      Employees: {
        title: 'Talent Pool',
        actions: []
      },
      Settings: {
        title: 'Settings',
        actions: [
          {
            text: 'Save',
            variant: 'primary',
            event: 'saveSearchSettings',
            class: 'default-size'
          },
          {
            text: 'Close',
            variant: 'outline-secondary',
            event: 'goHome',
            class: 'default-size'
          }
        ]
      }
    },
    downloadModalType: 'team'
  },
  getters: {
    getLocaleTimeByUTC: () => (timeZone: string) => {
      if (timeZone) {
        const utcOffset = +timeZone.toLowerCase().replace('gmt', '');
        return moment()
          .utc()
          .utcOffset(utcOffset * 60)
          .format('HH:mm A');
      }
      return '';
    }
  },
  mutations: {
    SHOW_DOWNLOAD_MODAL(state, data) {
      state.downloadModalType = data.type;
      data.modal.show('export-modal');
    },
    SET_MOBILE_SELECTED_INDEX(state, value) {
      state.mobileSelectedIndex = value;
    },
    SHOW_MESSAGE(state, val) {
      let errMessage = val.message;
      if (!errMessage && val.error) {
        if (val.error.response && val.error.response.data) {
          errMessage = val.error.response.data;
        }
        if (!errMessage && val.error.message) {
          errMessage = val.error.message;
        }
      }

      if (
        errMessage !== 'Full authentication is required to access this resource'
      ) {
        state.message = {
          type: val.type,
          message: errMessage,
          delay: val.delay
        };
      } else {
        console.log('Session Expired');
      }
    },
    SHOW_SESSION_EXPIRED_MODAL(state, values) {
      state.warningModal = !state.warningModal;
      state.alertModal = values;
    }
  },
  actions: {
    showWarningModal(context, values) {
      context.commit('SHOW_SESSION_EXPIRED_MODAL', values);
    },
    showConfirmDialog(context, params) {
      return params.modal
        .msgBoxConfirm(params.text, {
          title: params.title,
          size: 'md',
          dialogClass: 'confirm-dialog',
          buttonSize: params.buttonSize ? params.buttonSize : 'md',
          okVariant: 'danger',
          okTitle: params.ok ? params.ok : 'Yes',
          cancelVariant: 'primary',
          cancelTitle: params.cancel ? params.cancel : 'Cancel',
          footerClass: 'p-2',
          bodyClass: 'confirm-modal-body',
          modalClass: 'confirm-modal',
          hideHeaderClose: true,
          centered: true
        })
        .then((a: any) => {
          if (a === true) {
            params.callback();
          }
        });
    }
  }
};

export default shared;
