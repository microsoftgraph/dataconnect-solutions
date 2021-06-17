import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue';
import '@/assets/css/main.scss';

Vue.use(BootstrapVue);
Vue.use(IconsPlugin);
Vue.config.productionTip = false;
Vue.use(require('vue-moment'));
Vue.filter('gmtOffset', (value: any) => {
  const sign = +value > 0 ? '+' : '-';
  return `GMT ${sign} ${Math.floor(Math.abs(+value))}${
    +value % 1 === 0 ? '' : ':30'
  }`;
});

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app');
