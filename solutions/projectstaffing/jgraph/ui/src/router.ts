/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

import Vue from 'vue';
import VueRouter from 'vue-router';

Vue.use(VueRouter);

const router = new VueRouter({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: '',
      component: () => import('./views/dashboard/Dashboard.vue'),
      children: [
        {
          path: '',
          name: 'TeamModeler',
          component: () => import('./views/dashboard/TeamModeler.vue'),
          children: [
            {
              path: '/employees/:id',
              name: 'Employees',
              component: () => import('./views/dashboard/Employees.vue')
            }
          ]
        },
        {
          path: '/settings',
          name: 'Settings',
          component: () => import('./views/dashboard/Settings.vue')
        }
      ]
    },

    {
      path: '/*',
      name: 'notFound',
      component: () => import('./views/not-found/NotFound.vue')
    }
  ],
  scrollBehavior() {
    return { x: 0, y: 0 };
  }
});

export default router;
