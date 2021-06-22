import {
  Action,
  getModule,
  Module,
  Mutation,
  VuexModule
} from 'vuex-module-decorators';
import store from '@/store';
import axios from 'axios';
import { SharedStore } from './shared.store';

export interface Settings {
  clustering: {
    role_similarity: number;
    skill_match: number;
    location_match: number;
    randomness: number;
  };
  core_hours: string;
  groups_time_range: number;
}

@Module({
  dynamic: true,
  store: store,
  name: 'Settings',
  namespaced: true
})
class SettingsStoreModule extends VuexModule {
  settings: Settings = {
    clustering: {
      role_similarity: 0,
      skill_match: 0,
      location_match: 0,
      randomness: 0
    },
    core_hours: '8,16',
    groups_time_range: 7
  };
  @Mutation
  setSettings(data: Settings) {
    this.settings = data;
    console.log(this);
  }

  @Action({ rawError: true })
  getSettings() {
    return axios.get('/jwc/meta/settings').then(response => {
      if (Object.entries(response.data).length !== 0)
        this.setSettings(response.data);
    });
  }

  @Action({ rawError: true })
  saveSettings(data: Settings) {
    return axios.put('/jwc/meta/settings', data).then(response => {
      SharedStore.setMessage({
        type: 3,
        message: `Settings Saved`
      });
    });
  }
}
export const SettingsStore = getModule(SettingsStoreModule);
