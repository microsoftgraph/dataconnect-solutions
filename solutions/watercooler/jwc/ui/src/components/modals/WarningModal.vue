<template>
  <b-modal
    :no-close-on-backdrop="!modalData.closable"
    :id="id"
    dialog-class="default-modal"
    centered
    scrollable
  >
    <template v-slot:modal-header="{}">
      <span class="title">
        {{ modalData.title }}
      </span>
    </template>
    <b-row no-gutters class="mb-3 d-flex flex-column">
      <b-col no-gutters class="d-flex flex-row">
        <b-icon-exclamation-triangle-fill
          variant="warning"
        ></b-icon-exclamation-triangle-fill>
        <span>
          {{ modalData.text }}
        </span>
      </b-col>
    </b-row>
    <template v-slot:modal-footer>
      <b-button
        v-if="modalData.closable"
        variant="outline-secondary"
        class="default-size"
        @click="close"
      >
        Close
      </b-button>
      <b-button variant="primary" class="default-size" @click="refresh">
        Refresh
      </b-button>
    </template>
  </b-modal>
</template>

<script lang="ts">
import { SharedStore } from '@/store/modules/shared.store';
import { Component, Vue } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class WarningModal extends Vue {
  id = 'warning-modal';

  get modalData() {
    return SharedStore.warningModalData;
  }

  refresh() {
    location.reload();
  }

  open() {
    this.$bvModal.show(this.id);
  }

  close() {
    this.$bvModal.hide(this.id);
  }
}
</script>

<style lang="scss">
#warning-modal___BV_modal_outer_ {
  z-index: 1200 !important;
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
