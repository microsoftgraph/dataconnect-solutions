import Vue from 'vue';
import VueRouter, { RouteConfig } from 'vue-router';
import Dashboard from '@/views/Dashboard.vue';
import Day from '@/views/Day.vue';
import Week from '@/views/Week.vue';
import Settings from '@/views/Settings.vue';
import Metrics from '@/views/Metrics.vue';

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard,
    redirect: 'day',
    children: [
      {
        path: 'day',
        component: Day
      },
      {
        path: 'week',
        component: Week
      },
      {
        path: 'metrics',
        component: Metrics
      },
      {
        path: 'settings',
        component: Settings
      }
    ]
  },
  { path: '*', redirect: '/' }
];

const router = new VueRouter({
  base: process.env.BASE_URL,
  routes,
  mode: 'history'
});

export default router;
