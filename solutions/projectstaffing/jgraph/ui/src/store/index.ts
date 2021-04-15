import Vue from 'vue';
import Vuex from 'vuex';
import auth from '@/store/modules/authentication';
import shared from '@/store/modules/shared';
import settings from '@/store/modules/settings';
import team from '@/store/modules/team';
import search from '@/store/modules/search';
import webSockets from '@/store/modules/webSockets';
import ingestion from '@/store/modules/ingestion';
import fileUpload from '@/store/modules/fileUpload';

Vue.use(Vuex);

export default new Vuex.Store({
  state: {},
  modules: {
    auth,
    shared,
    team,
    settings,
    webSockets,
    ingestion,
    search,
    fileUpload
  },
  mutations: {},
  actions: {}
});
