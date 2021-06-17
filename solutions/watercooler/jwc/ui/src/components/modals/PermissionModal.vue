<template>
  <b-modal
    :id="id"
    dialog-class="default-modal"
    centered
    scrollable
    no-close-on-backdrop
  >
    <template v-slot:modal-header>
      <span class="title">Welcome</span>
    </template>

    <b-row no-gutters>
      <span>
        Welcome to the Watercooler product, an enjoyable way to keep track of
        all your water cooler breaks. In order to function properly we need to
        access your calendar.</span
      >
      <b-form-checkbox v-model="selected" class="mt-4 custom-checkbox">
        Grant access to the calendar
      </b-form-checkbox>
    </b-row>
    <template v-slot:modal-footer>
      <b-button :disabled="!selected" variant="primary" class="default-size" @click="close">
        Start
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
export default class PermissionModal extends Vue {
  id = 'permission-modal';
  selected = false;

  mounted() {
    SharedStore.getAbout();
  }

  close() {
    if (this.selected) this.$bvModal.hide(this.id);
  }

  open() {
    this.$bvModal.show(this.id);
  }
}
</script>

<style lang="scss">
#permission-modal {
  .modal-header {
    padding: var(--main-padding);
    border-bottom: none !important;
    .title {
      font-size: 20px;
    }
  }
  .modal-dialog {
    max-width: 400px;
    .modal-body {
      padding: 0px var(--main-padding) 20px var(--main-padding);
      display: flex;
      img {
        max-width: 100%;
        object-fit: contain;
      }
    }
  }
}
</style>
