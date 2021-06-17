import {
  getModule,
  Module,
  Mutation,
  Action,
  VuexModule
} from 'vuex-module-decorators';
import store from '@/store';
import axios from 'axios';
import Vue from 'vue';
import { EmployeeInfo } from '@/types/Employee';

@Module({
  dynamic: true,
  store: store,
  name: 'Employee',
  namespaced: true
})
class EmployeeStoreModule extends VuexModule {
  public employees: { [email: string]: EmployeeInfo } = {};

  @Mutation
  setEmployeeEmptyProfile(email: string) {
    Vue.set(this.employees, email, {});
  }

  @Mutation
  setEmployee(data: EmployeeInfo) {
    Vue.set(this.employees, data.mail, data);
  }

  @Action({ rawError: true })
  getEmployeeProfile(data: { email: string; silentLoad: boolean }) {
    let headers = {};
    if (data.silentLoad)
      headers = {
        headers: { noLoader: true }
      };
    return axios
      .get(`/jwc/employee?email=${data.email}`, headers)
      .then(response => {
        if (response.data) {
          this.setEmployee(response.data);
        }
      });
  }
}
export const EmployeeStore = getModule(EmployeeStoreModule);
