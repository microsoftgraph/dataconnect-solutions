<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <b-modal
    :id="id"
    dialog-class="default-modal"
    centered
    scrollable
    no-close-on-backdrop
  >
    <template v-slot:modal-header>
      <h5 class="text-sb">Insufficient Permissions</h5>
      <b-icon-x @click="close"> </b-icon-x>
    </template>

    <b-row no-gutters>
      <div>
        This action requires aditional permissions from you. Clicking the
        'Allow' button below will redirect you to your identity provider.
      </div>
    </b-row>
    <template v-slot:modal-footer>
      <b-button variant="outline-primary" class="default-size" @click="close">
        Close
      </b-button>
      <b-button variant="danger" class="default-size" @click="redirect">
        Allow
      </b-button>
    </template>
  </b-modal>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class RequestPermissionModal extends Vue {
  id = 'permission-modal';

  @Prop({ default: '' })
  link!: string;

  mounted() {}

  redirect() {
    window.location.href = this.link;
  }

  close() {
    this.$bvModal.hide(this.id);
  }

  open() {
    this.$bvModal.show(this.id);
  }
}
</script>

<style lang="scss">
#permission-modal {
  .modal-dialog {
    max-width: 360px;
    .modal-body {
      padding: 20px 24px;
      display: flex;
    }
  }
}
</style>
