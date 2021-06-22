import {
  getModule,
  Module,
  Mutation,
  VuexModule,
  Action
} from 'vuex-module-decorators';
import store from '@/store';
import axios from 'axios';

export interface AlertModalData {
  visible: boolean;
  title: string;
  text: string;
  closable: boolean;
}

export interface AboutData {
  appName: string;
  buildBranch: string;
  builtAt: Date;
  commit: string;
  dockerTag: string;
  javaVersion: string;
  scalaVersion: string;
  version: string;
}
@Module({
  dynamic: true,
  store: store,
  name: 'Shared',
  namespaced: true
})
class SharedStoreModule extends VuexModule {
  public about: AboutData = {} as AboutData;
  public message = {
    type: 0,
    message: '',
    delay: 3000
  };

  public warningModalData: AlertModalData = {
    visible: false,
    title: '',
    text: '',
    closable: false
  };

  public showLoader: boolean = false;

  @Mutation
  setWarningModalData(data: AlertModalData) {
    this.warningModalData = data;
  }

  @Mutation
  setShowLoader(value: boolean) {
    this.showLoader = value;
  }

  @Mutation
  setMessage(val: any) {
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
      this.message = {
        type: val.type,
        message: errMessage,
        delay: val.delay
      };
    } else {
      console.log('Session Expired');
    }
  }

  @Mutation
  setAbout(data: AboutData) {
    this.about = data;
  }

  @Action({ rawError: true })
  getAbout() {
    return axios
      .get(`/jwc/about`)
      .then(response => {
        if (response.data) {
          this.setAbout(response.data);
        }
      })
      .catch(err => {
        console.log(err);
      });
  }
}
export const SharedStore = getModule(SharedStoreModule);
