<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <b-modal
    id="error-details-modal"
    :dialog-class="['default-modal', isCollapsedClass()]"
    centered
    scrollable
  >
    <template v-slot:modal-header="{ close }">
      <h5 class="text-sb">Mode Switch Error</h5>
      <b-icon-x @click="close"> </b-icon-x>
    </template>
    <b-row no-gutters class="w-100 mb-3 d-flex flex-column">
      <b-col no-gutters class="d-flex flex-row">
        <b-icon-x-circle-fill variant="danger"></b-icon-x-circle-fill>

        <span class="content">
          <span class="card-input-wrapper mb-3">
            <span
              v-for="field in fields"
              :key="`${field.key}_ingestionModeSettings`"
            >
              <span class="label">{{ field.label }}:</span>
              <strong class="text-sb">{{
                field.key === 'ingestionMode'
                  ? $store.state.ingestion.ingestionModesOptions[
                      ingestionMode[field.key]
                    ]
                  : ingestionMode[field.key]
              }}</strong></span
            >
          </span>
          <span class="card-input-wrapper mb-3 custom-toggler">
            <span class="card-label">
              <span class="toggler text-sb" v-b-toggle.collapse-1>
                <b-icon class="collapsed" :icon="'caret-down-fill'"></b-icon>
                <b-icon
                  class="not-collapsed"
                  :icon="'caret-right-fill'"
                ></b-icon>
                Error Stack Trace
              </span>
              <span
                class="copy-control"
                v-b-tooltip.hover
                title="Copy to Clipboard"
                @click="copyToClipboard()"
              >
                <b-icon-files></b-icon-files>
              </span>
            </span>
            <textarea
              type="text"
              class="hidden-input"
              ref="copyToClipboard"
            ></textarea>
            <b-collapse
              class="stack-holder break-spaces"
              v-model="isVisible"
              id="collapse-1"
            >
              {{ ingestionMode.modeSwitchErrorStackTrace }}
            </b-collapse>
          </span>
        </span>
      </b-col>
    </b-row>
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
export default class ErrorDetailsModal extends Vue {
  id = 'error-details-modal';
  isVisible = false;
  fields = [
    {
      label: 'Ingestion Mode',
      key: 'ingestionMode'
    },
    {
      label: 'Mode Switch Requester',
      key: 'modeSwitchRequester'
    },
    {
      label: 'Mode Switch Timestamp',
      key: 'modeSwitchTimestamp'
    },
    {
      label: 'Mode Switch Error',
      key: 'modeSwitchErrorMessage'
    },
    {
      label: 'Logs Correlation ID',
      key: 'logsCorrelationId'
    }
  ];

  mounted() {}

  get ingestionMode() {
    return this.$store.state.ingestion.ingestionModeDetails;
  }

  isCollapsedClass() {
    return this.isVisible ? 'max-width' : '';
  }

  copyToClipboard() {
    const el = this.$refs['copyToClipboard'] as any;
    el.value = this.ingestionMode.modeSwitchErrorStackTrace;
    el.select();
    document.execCommand('copy');
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
#error-details-modal {
  .modal-dialog {
    &.max-width {
      max-width: calc(100% - 48px) !important;
    }
    .modal-body {
      padding: 20px 24px;
      overflow-x: hidden;
      display: flex;
      .content {
        width: calc(100% - 100px);
        .stack-holder {
          overflow-x: scroll;
        }
      }
      svg {
        margin-top: 3px;
        margin-right: 12px;
        width: 56px;
        height: 39px;
      }
    }
  }
  .break-spaces {
    white-space: break-spaces;
  }
}
</style>
