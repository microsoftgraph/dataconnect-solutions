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

@Module({
  dynamic: true,
  store: store,
  name: 'Auth',
  namespaced: true
})
class AuthStoreModule extends VuexModule {
  currentUser: { name: string; email: string } = { name: '', email: '' };

  @Mutation
  setUserData(data: { name: string; email: string }) {
    this.currentUser = data;
  }

  @Action({ rawError: true })
  getCurrentUser() {
    return axios
      .get('/.auth/me')
      .then(response => {
        if (response.status === 200 && response.data.length !== 0) {
          this.setUserData({
            name:
              response.data[0].user_claims.find((x: any) => x.typ === 'name')
                ?.val || '',
            email: response.data[0].user_id
          });
        }
        return response.data.length === 0;
      })
      .catch(err => {
        this.setUserData({
          name: 'Administrator',
          email: ''
        });
        return false;
      });
  }

  @Action({ rawError: true })
  verifyPermissions() {
    return axios
      .post(
        '/jwc/group/update-participation',
        {},
        { headers: { noFeedbackMessage: true } }
      )
      .then(response => {
        SharedStore.setMessage({
          type: 3,
          message: `In a few minutes the engagement information for all groups will be updated.`,
          delay: 10000
        });
        return response;
      })
      .catch(err => {
        throw err;
      });
  }
}
export const AuthStore = getModule(AuthStoreModule);
