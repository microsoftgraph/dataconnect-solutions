<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <b-modal
    :no-close-on-backdrop="!closable"
    :id="id"
    dialog-class="default-modal"
    centered
    scrollable
  >
    <template v-slot:modal-header="{}">
      <h5 class="text-sb">
        Session Expired
      </h5>
    </template>
    <b-row no-gutters class="mb-3 d-flex">
      <b-col no-gutters class="d-flex flex-row">
        <b-icon-exclamation-triangle-fill
          variant="warning"
        ></b-icon-exclamation-triangle-fill>
        <span>
          Your session has expired. You will have to logout and then login
          again.
        </span>
      </b-col>
    </b-row>
    <template v-slot:modal-footer>
      <a href="/.auth/logout">
        <b-button @click="logout" variant="primary" class="default-size">
          Logout
        </b-button>
      </a>
    </template>
  </b-modal>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class WarningModalSignout extends Vue {
  id = 'session-expired-signout-modal';

  get closable() {
    return false;
  }

  get title() {
    return this.$store.state.shared.alertModal.title;
  }

  get text() {
    return this.$store.state.shared.alertModal.text;
  }

  logout() {
    localStorage.setItem('logout', 'true');
  }

  mounted() {}

  open() {
    this.$bvModal.show(this.id);
  }

  close() {
    this.$bvModal.hide(this.id);
  }
}
</script>

<style lang="scss">
#session-expired-signout-modal___BV_modal_outer_ {
  z-index: 1800 !important;
  .modal-content {
    max-width: 400px;
    svg {
      margin-top: 3px;
      margin-right: 12px;
      width: 56px;
      height: 39px;
    }
  }
}
</style>
