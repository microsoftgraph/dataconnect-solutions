<template>
  <div class="custom-navbar">
    <div class="navbar-view">
      <router-link to="/">
        <img src="@/assets/logo-negative.png" class="navbar-logo" />
      </router-link>
      <span class="controls">
        <router-link
          class="control"
          v-for="control in controls"
          :key="`control_${control.name}`"
          :to="control.to"
          >{{ control.name }}
        </router-link>
      </span>
      <span class="h-100 right-side">
        <span class="timezone-label">Current Timezone </span>
        <Timezones />
        <UserAvatar />
      </span>
      <div class="navbar-toggle" @click="showSidenav = !showSidenav">
        Account Settings
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
      <div class="navbar-item" @click="goToSettings">
        <b-icon-gear></b-icon-gear>
        Settings
      </div>
      <div class="navbar-item">
        <a>
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
    <div
      v-if="showSidenav"
      class="backdrop"
      @click="showSidenav = !showSidenav"
    ></div>
    <AboutModal />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import UserAvatar from '@/components/UserAvatar.vue';
import Timezones from '@/components/Timezones.vue';
import AboutModal from '@/components/modals/AboutModal.vue';

@Component({
  components: { UserAvatar, AboutModal, Timezones }
})
export default class Navbar extends Vue {
  showSidenav = false;
  controls = [
    {
      name: 'Week',
      to: '/week'
    },
    {
      name: 'Day',
      to: '/day'
    },
    {
      name: 'Metrics',
      to: '/metrics'
    }
  ];

  viewAbout() {
    this.$bvModal.show('about-modal');
    this.showSidenav = false;
  }

  goToSettings() {
    this.showSidenav = false;
    this.$router.push('/settings').catch(err => {
      if (err.name !== 'NavigationDuplicated') throw err;
    });
  }
}
</script>

<style lang="scss">
.custom-navbar {
  .navbar-view {
    color: white;
    background-color: $main-color;
    display: flex;
    flex-direction: row;
    height: 56px;
    align-items: center;
    padding: 0px 12px 0px 28px;
    .navbar-logo {
      width: 110px;
    }
    .controls {
      display: flex;
      margin-left: calc(50% - 220px);
      height: 100%;
      .control {
        justify-content: center;
        display: flex;
        align-items: center;
        padding: 0px 16px;
        position: relative;
        color: rgb(211, 219, 227);
        &:hover {
          color: white;
          text-decoration: none;
        }
        &.router-link-exact-active {
          background-color: rgb(65, 75, 87);
          color: white;
          &::after {
            content: '';
            display: block;
            height: 4px;
            width: 100%;
            background-color: $primary;
            position: absolute;
            left: 0;
            bottom: 0;
          }
        }
      }
    }
    .right-side {
      margin-left: auto;
      .timezone-label {
        height: 100%;
        display: inline-block;
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
    padding: 12px var(--main-padding);
    cursor: pointer;
    border-top: 1px solid rgba($main-border-color, 0.1);
  }
  .side-navbar-wrapper {
    display: none;
  }
  .backdrop {
    position: fixed;
    height: 100vh;
    width: 100vw;
    background-color: #94949459;
    top: 0;
    z-index: 1;
  }
}

@media screen and (max-width: 1024px) {
  .custom-navbar {
    position: fixed;
    // margin-top: -56px;
    width: 100%;
    height: 56px;
    z-index: 3;
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
