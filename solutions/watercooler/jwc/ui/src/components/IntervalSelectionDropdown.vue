<template>
  <b-dropdown
    right
    variant="outline-secondary"
    class="min-140 default-dropdown dark"
    :text="intervalText"
  >
    <b-dropdown-item
      class="dropdown-item-row"
      @click="selectNewInterval(index)"
      v-for="(option, index) in options"
      :key="`option_metrics_${index}`"
    >
      {{ option.text }}
    </b-dropdown-item>
  </b-dropdown>
</template>

<script lang="ts">
import {
  // eslint-disable-next-line no-unused-vars
  CalendarTimeInterval,
  MetricsStore
} from '@/store/modules/metrics.store';
import moment from 'moment';
import { Component, Vue } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class IntervalSelectionDropdown extends Vue {
  selectedIntervalValueIndex = 2;
  options = [
    {
      text: 'Today'
    },
    {
      text: 'Yesterday'
    },
    {
      text: 'This Week'
    },
    {
      text: 'Last Week'
    },
    {
      text: 'This Month'
    },
    {
      text: 'Last Month'
    },
    {
      text: 'Custom Interval'
    }
  ];

  get intervalText() {
    if (this.selectedIntervalValueIndex === 6) {
      return `${this.selectedInterval.start.format(
        'MM/DD'
      )}-${this.selectedInterval.end.format('MM/DD')}`;
    } else return this.options[this.selectedIntervalValueIndex].text;
  }

  get selectedInterval() {
    return MetricsStore.selectedInterval;
  }

  set selectedInterval(value: CalendarTimeInterval) {
    MetricsStore.setSelectedInterval(value);
  }

  mounted() {}

  selectNewInterval(index: number) {
    this.selectedIntervalValueIndex = index;
    switch (index) {
      // today
      case 0: {
        this.selectedInterval = {
          start: moment(),
          end: moment()
        };
        break;
      }
      // today
      case 1: {
        this.selectedInterval = {
          start: moment().subtract(1, 'day'),
          end: moment().subtract(1, 'day')
        };
        break;
      }
      // this week
      case 2: {
        this.selectedInterval = {
          start: moment().startOf('week'),
          end: moment().endOf('week')
        };
        break;
      }
      // last week
      case 3: {
        this.selectedInterval = {
          start: moment()
            .subtract(1, 'week')
            .startOf('week'),
          end: moment()
            .subtract(1, 'week')
            .endOf('week')
        };
        break;
      }
      // this month
      case 4: {
        this.selectedInterval = {
          start: moment().startOf('month'),
          end: moment().endOf('month')
        };
        break;
      }
      // last month
      case 5: {
        this.selectedInterval = {
          start: moment()
            .subtract(1, 'month')
            .startOf('month'),
          end: moment()
            .subtract(1, 'month')
            .endOf('month')
        };
        break;
      }
      // custom interval
      case 6: {
        this.openWeekSelectionModal();
        break;
      }
    }
  }

  openWeekSelectionModal() {
    this.$bvModal.show('week-selection-modal');
  }
}
</script>

<style lang="scss"></style>
