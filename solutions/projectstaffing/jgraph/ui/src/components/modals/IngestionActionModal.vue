<template>
  <b-modal
    id="ingestion-action-modal"
    dialog-class="default-modal"
    centered
    scrollable
    @hidden="closed"
  >
    <template v-slot:modal-header="{ close }">
      <h5 class="text-sb">Data Ingestion</h5>
      <b-icon-x @click="close"> </b-icon-x>
    </template>
    <b-row no-gutters class="mb-3 d-flex flex-column">
      <b-col no-gutters class="d-flex flex-row">
        <b-icon-x-circle-fill variant="danger"></b-icon-x-circle-fill>
        <span>
          {{ text }}
        </span>
      </b-col>
    </b-row>
    <template v-slot:modal-footer>
      <b-button
        v-if="
          $store.state.ingestion.ingestionModeDetails.modeSwitchPhase ===
            'error'
        "
        variant="outline-secondary"
        class="default-size"
        @click="showDetails"
      >
        Show Details
      </b-button>
      <b-button variant="outline-secondary" class="default-size" @click="close">
        Ignore
      </b-button>
      <b-button
        v-if="
          $store.state.ingestion.ingestionModeDetails.modeSwitchPhase ===
            'error'
        "
        variant="primary"
        class="default-size"
        @click="restart"
      >
        Restart
      </b-button>
      <b-button v-else variant="primary" class="default-size" @click="resume">
        Resume
      </b-button>
    </template>
  </b-modal>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import { constants } from '@/helpers/constants';

@Component({
  components: {},
  data() {
    return {
      constants
    };
  }
})
export default class IngestionActionModal extends Vue {
  id = 'ingestion-action-modal';

  mounted() {}

  @Watch('$store.state.ingestion.showIngestionModeModal', { deep: true })
  showIngestionModalActionChanged(value: boolean) {
    if (value) this.$bvModal.show('ingestion-action-modal');
  }

  get text() {
    return this.$store.state.ingestion.ingestionModeDetails.modeSwitchPhase ===
      'error'
      ? constants.ingestion.modal.failed
      : constants.ingestion.modal.paused;
  }

  closed() {
    this.$store.commit('SET_SHOW_INGESTION_ACTION_MODAL_DATA', {
      show: false,
      type: ''
    });
  }

  close() {
    this.$bvModal.hide(this.id);
  }

  open() {
    this.$bvModal.show(this.id);
  }

  resume() {
    this.$store.dispatch('resumeIngestionMode');
    this.close();
    this.closed();
  }

  restart() {
    this.$store.dispatch('restartIngestionMode');
    this.close();
    this.closed();
  }

  showDetails() {
    this.$bvModal.show('error-details-modal');
  }
}
</script>

<style lang="scss">
#ingestion-action-modal {
  .modal-dialog {
    max-width: 560px;
    .modal-body {
      padding: 20px 24px;
      display: flex;
      svg {
        margin-top: 3px;
        margin-right: 12px;
        width: 56px;
        height: 39px;
      }
    }
  }
}
</style>
