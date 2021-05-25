<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <b-modal
    id="about-modal"
    dialog-class="default-modal"
    centered
    scrollable
    @change="verifyVersion"
  >
    <template v-slot:modal-header="{ close }">
      <h5 class="text-sb">About</h5>
      <b-icon-x @click="close"> </b-icon-x>
    </template>
    <b-col class="p-0">
      <img style="width:135px" src="@/assets/logo.png" />
    </b-col>
    <b-col class="p-0 ml-4" cols="6">
      <b-row
        v-for="field in fields"
        :key="`about_modal_${field.key}`"
        no-gutters
        class="mb-3 d-flex flex-column"
      >
        <div class="text-sb">{{ field.label }}</div>
        <div v-if="field.type !== 'date'">
          {{ about[field.key] || field.fallback }}
        </div>
        <div v-else>
          <span
            v-if="
              about[field.key] && new Date(about[field.key]).toJSON() !== null
            "
          >
            {{ about[field.key] | moment('MMMM DD, YYYY') }}
          </span>
          <span v-else> {{ field.fallback }}</span>
        </div>
      </b-row>
    </b-col>
    <template v-slot:modal-footer>
      <b-button variant="primary" class="default-size" @click="close">
        Close
      </b-button>
    </template>
  </b-modal>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class AboutModal extends Vue {
  id = 'about-modal';
  isNewVersionAvailable = false;
  fields = [
    {
      label: 'Version',
      key: 'version',
      fallback: '-'
    },
    {
      label: 'Build',
      key: 'dockerTag',
      fallback: '-'
    },
    {
      label: 'Release Date',
      key: 'builtAt',
      type: 'date',
      fallback: '-'
    }
  ];

  get about() {
    return this.$store.state.settings.about;
  }

  async verifyVersion() {
    if (await this.$store.dispatch('verifyVersion'))
      this.$store.dispatch('showWarningModal', {
        title: 'New UI Version Available',
        text:
          'You are using an old UI version. Refresh manually or by clicking the button below to make sure you are using latest UI version.',
        closable: true
      });
  }

  mounted() {
    this.$store.dispatch('getAbout');
  }

  close() {
    this.$bvModal.hide(this.id);
  }

  open() {
    this.$bvModal.show(this.id);
  }

  refresh() {
    location.reload();
  }
}
</script>

<style lang="scss">
#about-modal {
  .modal-dialog {
    max-width: 360px;
    .modal-body {
      padding: 20px 24px;
      display: flex;
    }
  }
}
</style>
