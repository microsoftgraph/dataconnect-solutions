<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <div>
    <Message />
    <WarningModal />
    <WarningModalSignout />
    <ErrorDetailsModal />
    <IngestionActionModal />
    <RequestPermissionModal />
    <div class="spinner" :class="{ 'd-block': $store.state.shared.showLoader }">
      <Loader />
    </div>
    <router-view></router-view>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
// eslint-disable-next-line no-unused-vars
import interceptorsSetup from './helpers/httpInterceptors';
import store from '@/store';
import Message from '@/components/Message.vue';
import Loader from '@/components/Loader.vue';
import WarningModal from '@/components/modals/WarningModal.vue';
import WarningModalSignout from '@/components/modals/WarningModalSignout.vue';
import IngestionActionModal from '@/components/modals/IngestionActionModal.vue';
import ErrorDetailsModal from '@/components/modals/ErrorDetailsModal.vue';
import RequestPermissionModal from '@/components/modals/RequestPermissionModal.vue';

interceptorsSetup(store.commit);

@Component({
  components: {
    Message,
    WarningModal,
    Loader,
    IngestionActionModal,
    WarningModalSignout,
    ErrorDetailsModal,
    RequestPermissionModal
  }
})
export default class App extends Vue {
  get isAdmin() {
    // see if current user is admin based on webnsocket response
    // return this.$store.getters.isCurrentUserAdmin(
    //   this.$store.state.auth.currentUser.email
    // );
    // see if current user is admin based on role
    return this.$store.state.auth.currentUser.isAdmin();
  }

  @Watch('$store.state.shared.showLoader')
  loaderChanged(newvalue: boolean) {
    if (window.innerWidth <= 1024) {
      if (newvalue)
        document.getElementById('root-element')!.classList.add('o-hidden');
      else
        document.getElementById('root-element')!.classList.remove('o-hidden');
    }
  }

  @Watch('$store.state.shared.warningModal')
  onPropertyChanged() {
    this.$bvModal.show('session-expired-modal');
  }

  initSelectedTaxonomies() {
    const tax = localStorage.getItem('selectedTaxonomies');
    if (tax) this.$store.commit('SET_SELECTED_TAXONOMIES', JSON.parse(tax));
  }

  async verifyIdentityAndGetCurrentUser() {
    const shouldRefresh = await this.$store.dispatch(
      'verifyIdentityAndGetCurrentUser'
    );
    if (shouldRefresh) {
      this.$bvModal.show('session-expired-signout-modal');
    }
  }

  @Watch('$store.state.webSockets.wsConnected')
  onWsConnectionStateChange(newValue: boolean) {
    if (newValue) {
      this.$store.dispatch('subscribeIngestionStatus');
      this.$store.dispatch('subscribeFileUploadStatus');
    }
    // Handle edge case with /.auth/me response is []
    if (this.$store.state.webSockets.wsDisconnected && newValue)
      this.verifyIdentityAndGetCurrentUser();
  }

  @Watch('$store.state.ingestion.ingestionModeDetails', { deep: true })
  onIngestionModeStateChanged(values: any) {
    // handle admin related action for ingestion mode
    if (this.isAdmin && this.$store.state.ingestion.ingestionModeShowModal) {
      if (values.modeSwitchPhase === 'error')
        this.$store.commit('SET_SHOW_INGESTION_ACTION_MODAL_DATA', {
          show: true,
          type: 'retry'
        });
      if (
        values.modeSwitchPhase !== 'error' &&
        values.modeSwitchPhase !== 'completed' &&
        values.modeSwitchPaused
      )
        this.$store.commit('SET_SHOW_INGESTION_ACTION_MODAL_DATA', {
          show: true,
          type: 'resume'
        });
    }
  }

  async mounted() {
    await this.verifyIdentityAndGetCurrentUser();
    this.verifyLogout();
    await this.$store.dispatch('getCurrentUserRole');
    this.$root.$emit('user-role-loaded');
    this.$root.$on('goHome', this.goHome);
    this.$store.dispatch('getIngestionModeDetails', { showModal: true });
    this.$store.dispatch('initWS');
    this.initSelectedTaxonomies();
    if (localStorage.getItem('logout') && !location.href.includes('/.auth')) {
      location.reload(true);
      localStorage.removeItem('logout');
    }
  }

  verifyLogout() {
    // Custom Logout logic for Azure App Services loading page from cache after logout
    if (localStorage.getItem('logout') && !location.href.includes('/.auth')) {
      location.reload();
      localStorage.removeItem('logout');
    }
  }

  goHome() {
    this.$router.push('/');
  }
}
</script>

<style>
.spinner {
  position: fixed;
  display: none;
  width: 100vw;
  height: 100vh;
  z-index: 9999999;
  background-color: #00000066;
  top: 0;
}

@media (max-width: 1024px) {
  .root-element {
    overflow-y: auto;
  }
}
</style>
