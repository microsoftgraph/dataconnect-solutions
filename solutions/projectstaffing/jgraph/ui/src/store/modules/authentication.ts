/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

import { Module } from 'vuex';
import axios from 'axios';
import { User } from '@/types/User';

const authentication: Module<any, any> = {
  state: {
    currentUser: new User(),
    permissionConsentLink: ''
  },

  mutations: {
    SET_USER_DATA(state, user) {
      state.currentUser = new User(user);
    },
    SET_USER_ROLE(state, role) {
      state.currentUser = new User({ ...state.currentUser, role });
    },
    SET_CONSENT_LINK(state, value) {
      state.permissionConsentLink = value;
    }
  },
  actions: {
    getCurrentUserRole(context) {
      return axios
        .get('/gdc/admin/user')
        .then(response => {
          if (response.status === 200) {
            context.commit(
              'SET_USER_ROLE',
              response.data.userIsAdmin ? 'admin' : 'regular'
            );
            return true;
          }
        })
        .catch(err => {
          context.commit('SET_USER_ROLE', 'regular');
          return false;
        });
    },
    async verifyIdentityAndGetCurrentUser(context) {
      return axios
        .get('/.auth/me')
        .then(response => {
          if (response.status === 200 && response.data.length !== 0) {
            context.commit('SET_USER_DATA', {
              name:
                response.data[0].user_claims.find((x: any) => x.typ === 'name')
                  ?.val || '',
              email: response.data[0].user_id
            });
          }
          return response.data.length === 0;
        })
        .catch(err => {
          return false;
        });
    }
  }
};

export default authentication;
