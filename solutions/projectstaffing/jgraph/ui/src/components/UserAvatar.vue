<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <b-dropdown class="user-avatar-wrapper" right ref="editProfileDropdown">
    <template v-slot:button-content>
      <div>
        {{ user.name }}
      </div>
    </template>
    <b-dropdown-item @click="viewAbout" class="dropdown-item-row">
      <span>
        <b-icon-info-circle></b-icon-info-circle>
        About</span
      >
    </b-dropdown-item>
    <b-dropdown-item @click="viewSettings" class="dropdown-item-row">
      <span>
        <b-icon-gear></b-icon-gear>
        Settings</span
      >
    </b-dropdown-item>
    <span class="dropdown-item-row">
      <a @click="logout" href="/.auth/logout">
        <b-icon-box-arrow-right></b-icon-box-arrow-right>
        Logout</a
      >
    </span>
  </b-dropdown>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class UserAvatar extends Vue {
  get user() {
    return this.$store.state.auth.currentUser;
  }

  mounted() {}

  logout() {
    localStorage.setItem('logout', 'true');
  }

  viewAbout() {
    this.$bvModal.show('about-modal');
  }

  viewSettings() {
    this.$store.dispatch('getCurrentUserRole');
    this.$router.push('/settings').catch(err => {
      if (err.name !== 'NavigationDuplicated') throw err;
    });
  }
}
</script>

<style lang="scss">
.user-avatar-wrapper {
  button {
    background-color: transparent !important;
    display: flex;
    align-items: center;
    font-size: 14px;
    padding: 0px;
    border: none;
    &:hover {
      background-color: transparent;
    }
    &:active,
    &:focus {
      background-color: transparent !important;
      outline: none;
      box-shadow: none !important;
    }
    div {
      margin-right: 5px;
    }
    img {
      margin-right: 10px;
      border-radius: 50px;
      width: 30px;
      height: 30px;
    }
  }

  .dropdown-item {
    &:active,
    &:hover {
      background-color: transparent;
      outline: none;
    }
  }
  .dropdown-item-row {
    font-size: 14px;
    cursor: pointer;
    display: flex;
    align-items: center;
    color: black;
    padding: 8px 12px;
    a {
      color: black !important;
      width: 100%;
      padding: 0px;
      font-weight: 300;
    }
    svg {
      fill: black;
      margin-right: 5px;
      width: 16px;
      height: 16px;
    }
    &:hover {
      background-color: #f8f9fa;
    }
  }
}
</style>
