<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <div class="custom-navbar">
    <div class="navbar-view">
      <router-link class="navbar-logo" to="/">
        <img src="@/assets/navbar-logo.png"
      /></router-link>
      <div v-for="item in items" :key="item.name" class="navbar-item">
        {{ item.name }}
      </div>
      <div class="ingestion-mode-loader" v-if="ingestionModeOngoing">
        <b-icon-arrow-repeat animation="spin"></b-icon-arrow-repeat>
        Processing Data
      </div>
      <UserAvatar />
      <div class="navbar-toggle" @click="showSidenav = !showSidenav">
        {{ user.name }}
      </div>
    </div>
    <div
      class="side-navbar-wrapper "
      :class="{ slideInDown: showSidenav, slideOutUp: !showSidenav }"
    >
      <div class="navbar-item" @click="viewAbout">
        <b-icon-info-circle></b-icon-info-circle>
        About
      </div>
      <div class="navbar-item" @click="viewSettings">
        <b-icon-gear></b-icon-gear>
        Settings
      </div>
      <div class="navbar-item">
        <a @click="logout" href="/.auth/logout">
          <b-icon-box-arrow-right></b-icon-box-arrow-right>
          Logout</a
        >
      </div>
    </div>
    <div
      v-if="showSidenav"
      class="backdrop"
      @click="showSidenav = !showSidenav"
    ></div>
    <AboutModal />
  </div>
</template>

<script lang="ts">
import UserAvatar from '@/components/UserAvatar.vue';
import { Component, Vue } from 'vue-property-decorator';
import AboutModal from '@/components/modals/AboutModal.vue';

@Component({
  components: { UserAvatar, AboutModal }
})
export default class Navbar extends Vue {
  showSidenav = false;
  items = [];

  get ingestionModeOngoing() {
    return !['completed', 'error'].includes(
      this.$store.state.ingestion.ingestionModeDetails.modeSwitchPhase
    );
  }

  mounted() {}

  viewAbout() {
    this.$bvModal.show('about-modal');
    this.showSidenav = false;
  }

  viewSettings() {
    this.$store.dispatch('getCurrentUserRole');
    this.$router.push('/settings').catch(err => {
      if (err.name !== 'NavigationDuplicated') throw err;
    });
    this.showSidenav = false;
  }

  get user() {
    return this.$store.state.auth.currentUser;
  }

  logout() {
    localStorage.setItem('logout', 'true');
  }
}
</script>

<style lang="scss">
.custom-navbar {
  .navbar-view {
    color: white;
    border-top-left-radius: $main-border-radius;
    border-top-right-radius: $main-border-radius;
    background-color: $main-color;
    display: flex;
    flex-direction: row;
    height: 56px;
    align-items: center;
    padding: 0px var(--main-padding);
    .ingestion-mode-loader {
      margin: 0 auto;
    }
    :not(.ingestion-mode-loader) + .user-avatar-wrapper {
      margin-left: auto;
    }
    .navbar-logo {
      img {
        height: 20px;
      }
    }
    .navbar-info {
      display: none;
    }
    .navbar-toggle {
      display: none;
    }
  }
  .navbar-item {
    padding: 12px 24px;
    cursor: pointer;
    border-top: 1px solid rgba($main-border-color, 0.1);
  }
  .side-navbar-wrapper {
    display: none;
  }
}

@media screen and (max-width: 1024px) {
  .custom-navbar {
    position: fixed;
    margin-top: -56px;
    width: 100%;
    height: 56px;
    z-index: 2;
    .navbar-view {
      z-index: 3;
      border-radius: 0px;
      width: 100%;
      position: absolute;
      justify-content: space-between;
      .user-avatar-wrapper {
        display: none;
      }
      .navbar-info {
        display: flex;
      }
      .navbar-toggle {
        display: flex;
        cursor: pointer;
        &::after {
          display: inline-block;
          margin-left: 7px;
          vertical-align: 0.255em;
          content: '';
          border-top: 0.3em solid;
          border-right: 0.3em solid transparent;
          border-bottom: 0;
          border-left: 0.3em solid transparent;
          margin-top: 9px;
        }
      }
      .navbar-item {
        display: none;
      }
    }
    .side-navbar-wrapper {
      display: flex;
      box-shadow: 0 0 20px 1px #d4d4d4;
      z-index: 2;
      width: 100%;
      flex-direction: column;
      height: auto;
      background-color: $main-color;
      position: absolute;
      height: auto;
      animation-fill-mode: both;
      animation-duration: 0.3s;
      background-color: white;
      a {
        color: inherit;
      }
      .navbar-item {
        cursor: pointer;
        z-index: 2;
      }
    }
  }
  .content-wrapper {
    margin-top: 56px;
    // height: calc(100vh - 56px);
  }
}
</style>
