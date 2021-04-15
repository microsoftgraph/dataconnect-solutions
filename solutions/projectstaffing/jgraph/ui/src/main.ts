import Vue from 'vue';
import App from './App.vue';
import store from '@/store';
import router from '@/router';
import axios from 'axios';
import VueAxios from 'vue-axios';
import Vuelidate from 'vuelidate';
import '@/assets/css/main.scss';
import '@/assets/css/theme.scss';
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue';

Vue.use(BootstrapVue);
Vue.use(IconsPlugin);
Vue.use(Vuelidate);
Vue.use(VueAxios, axios);
Vue.config.productionTip = false;
Vue.use(require('vue-moment'));

new Vue({
  store,
  router,
  render: h => h(App)
}).$mount('#app');
