<template>
  <b-dropdown class="user-avatar-wrapper default-dropdown light" right>
    <template v-slot:button-content>
      <div>
        {{ user.name }}
      </div>
    </template>
    <b-dropdown-item class="dropdown-item-row" @click="viewAbout">
      <span> <b-icon-info-circle></b-icon-info-circle>About</span>
    </b-dropdown-item>
    <b-dropdown-item class="dropdown-item-row" @click="goToSettings">
      <span> <b-icon-gear></b-icon-gear>Settings</span>
    </b-dropdown-item>
    <a @click="logout" href="/.auth/logout">
      <b-icon-box-arrow-right></b-icon-box-arrow-right>
      Logout
    </a>
  </b-dropdown>
</template>

<script lang="ts">
import { AuthStore } from '@/store/modules/auth.store';
import { Component, Vue } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class UserAvatar extends Vue {
  get user() {
    return AuthStore.currentUser;
  }

  viewAbout() {
    this.$bvModal.show('about-modal');
  }

  goToSettings() {
    this.$router.push('/settings').catch(err => {
      if (err.name !== 'NavigationDuplicated') throw err;
    });
  }

  logout() {
    localStorage.setItem('logout', 'true');
  }
}
</script>

<style lang="scss">
.user-avatar-wrapper {
  padding: 0px 16px;
  ul {
    left: inherit !important;
  }
}
</style>
