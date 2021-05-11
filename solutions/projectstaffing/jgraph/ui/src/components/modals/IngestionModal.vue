<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <b-modal
    id="ingestion-modal"
    dialog-class="default-modal"
    centered
    scrollable
    @change="init"
  >
    <template v-slot:modal-header="{ close }">
      <h5 class="text-sb">Switch Ingestion Mode</h5>
      <b-icon-x @click="close"> </b-icon-x>
    </template>
    <b-row no-gutters>
      <b-alert show variant="warning">
        <b-icon-exclamation-triangle-fill
          variant="warning"
          class="mr-2"
        ></b-icon-exclamation-triangle-fill>
        <span
          v-if="
            $store.state.ingestion.ingestionModeDetails.ingestionMode !==
              'production_mode'
          "
          >Switching the ingestion mode will delete all data processed in the
          previous mode. Are you sure you want to proceed?
        </span>
        <span v-else>
          You are about to switch from Production Mode to a different ingestion
          mode. This will delete all production data processed in the previous
          mode. That could mean deleting years worth of data. Are you sure you
          want to proceed?
        </span>
      </b-alert>
      <b-form-group class="mt-4 w-100" label="Choose the new Ingestion Mode">
        <b-form-select
          v-model="mode"
          :options="$store.getters.getIngestionModeOptionsArray"
        ></b-form-select>
      </b-form-group>
      <b-form-group
        class=" w-100"
        label=""
        description="As a safety precaution, type the new ingestion mode"
      >
        <b-form-input
          :disabled="isSame"
          v-model="modeConfirmation"
          @keypress.enter="submit"
        ></b-form-input>
      </b-form-group>
    </b-row>
    <template v-slot:modal-footer>
      <b-button variant="outline-primary" class="default-size" @click="close">
        Cancel
      </b-button>
      <b-button
        variant="danger"
        class="default-size"
        :disabled="!isCorrect"
        @click="submit"
        >Switch</b-button
      >
    </template>
  </b-modal>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class IngestionModal extends Vue {
  id = 'ingestion-modal';
  mode = '';
  modeConfirmation = '';

  mounted() {}

  init() {
    this.modeConfirmation = '';
    this.mode = this.$store.state.ingestion.ingestionModeDetails.ingestionMode;
  }

  get isCorrect() {
    return (
      this.$store.state.ingestion.ingestionModesOptions[this.mode] ===
      this.modeConfirmation
    );
  }

  get isSame() {
    return (
      this.$store.state.ingestion.ingestionModeDetails.ingestionMode ===
      this.mode
    );
  }

  get options() {
    return this.$store.state.ingestion.ingestionModesOptions;
  }

  close() {
    this.$bvModal.hide(this.id);
  }

  open() {
    this.$bvModal.show(this.id);
  }

  async submit() {
    if (this.isCorrect) {
      await this.$store.dispatch('updateIngestionMode', this.mode);
      this.close();
    }
  }
}
</script>

<style lang="scss">
#ingestion-modal {
  .modal-dialog {
    .modal-body {
      padding: 20px 24px;
      display: flex;
    }
  }
}
</style>
