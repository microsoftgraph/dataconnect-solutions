<template>
  <b-modal
    id="week-selection-modal"
    dialog-class="default-modal"
    centered
    scrollable
  >
    <template v-slot:modal-header="{ close }">
      <span class="title">Select Period</span>
      <b-icon-x @click="close"> </b-icon-x>
    </template>
    <IntervalCalendar :interval="interval" ref="intervalCalendar" />
    <template v-slot:modal-footer>
      <b-button variant="outline-primary" class="default-size" @click="close">
        Close
      </b-button>
      <b-button variant="primary" class="default-size" @click="save">
        Save
      </b-button>
    </template>
  </b-modal>
</template>

<script lang="ts">
import {
  // eslint-disable-next-line no-unused-vars
  CalendarTimeInterval,
  MetricsStore
} from '@/store/modules/metrics.store';
import { Component, Vue } from 'vue-property-decorator';
import IntervalCalendar from '@/components/IntervalCalendar.vue';

@Component({
  components: { IntervalCalendar }
})
export default class WeekSelectionModal extends Vue {
  id = 'week-selection-modal';

  get interval() {
    return MetricsStore.selectedInterval;
  }

  set interval(value: CalendarTimeInterval) {
    MetricsStore.setSelectedInterval(value);
  }

  mounted() {}

  close() {
    this.$bvModal.hide(this.id);
  }

  open() {
    this.$bvModal.show(this.id);
  }

  save() {
    this.interval = (this.$refs['intervalCalendar'] as any).localInterval;
    this.$bvModal.hide(this.id);
  }
}
</script>

<style lang="scss">
#week-selection-modal {
  .modal-dialog {
    // max-width: 360px;
    min-width: 700px;
    .modal-body {
      padding: 20px var(--main-padding);
      display: flex;
      .calendar-wrapper {
        display: flex;
        width: 100%;
        justify-content: space-between;
      }
    }
  }
}
</style>
