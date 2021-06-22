<template>
  <div class="metrics-wrapper">
    <Header :title="'Metrics'">
      <template v-slot:actions>
        <CurrentTimezoneDropdown class="ml-auto mr-3" />
        <span class="mr-2 text-secondary">Time Interval</span>
        <IntervalSelectionDropdown />
      </template>
    </Header>
    <div class="content">
      <div class="col-card">
        <span class="header border-bottom">Engagement Stats</span>
        <div class="chart">
          <BarChart :chart-data="chartsData.Bar.data" />
        </div>
      </div>
      <div class="col-card">
        <span class="header border-bottom">Engagement Rate</span>
        <div class="chart">
          <LineChart
            :chart-data="chartsData.Line.data"
            :options="chartsData.Line.options"
            :styles="{
              height: '100%',
              width: '100%',
              position: 'relative'
            }"
          />
        </div>
      </div>
      <div class="col-card">
        <span class="header border-bottom">Aggregate Indicators</span>
        <div class="chart">
          <KPI :chart-data="chartsData.KPI.data" />
        </div>
      </div>
      <div class="col-card col8">
        <span class="header border-bottom">Hourly Attendance</span>
        <div class="chart">
          <Heatmap />
        </div>
      </div>
    </div>
    <WeekSelectionModal />
  </div>
</template>

<script lang="ts">
import Header from '@/components/Header.vue';
import { Component, Vue, Watch } from 'vue-property-decorator';
import LineChart from '@/components/charts/LineChart.vue';
import BarChart from '@/components/charts/BarChart.vue';
import KPI from '@/components/charts/KPI.vue';
import Heatmap from '@/components/charts/Heatmap.vue';
import { GroupsStore } from '@/store/modules/groups.store';
import WeekSelectionModal from '@/components/modals/WeekSelectionModal.vue';
import {
  // eslint-disable-next-line no-unused-vars
  CalendarTimeInterval,
  MetricsStore
} from '@/store/modules/metrics.store';
import IntervalSelectionDropdown from '@/components/IntervalSelectionDropdown.vue';
import CurrentTimezoneDropdown from '@/components/CurrentTimezoneDropdown.vue';

@Component({
  components: {
    CurrentTimezoneDropdown,
    IntervalSelectionDropdown,
    Header,
    LineChart,
    KPI,
    BarChart,
    Heatmap,
    WeekSelectionModal
  }
})
export default class Metrics extends Vue {
  get selectedDate() {
    return GroupsStore.selectedDate;
  }

  get selectedInterval() {
    return MetricsStore.selectedInterval;
  }

  get chartsData() {
    return MetricsStore.chartData;
  }

  @Watch('selectedInterval')
  selectedIntervalChanged() {
    MetricsStore.getMetrics({
      start: this.selectedInterval.start.format('YYYY-MM-DD'),
      end: this.selectedInterval.end.format('YYYY-MM-DD')
    });
  }

  mounted() {
    // initial get data
    if (GroupsStore.availableTimezonesLoaded) this.getMetrics();
    this.$root.$on('available-timezones-loaded', this.getMetrics);
    // listener for timezone change
    this.$root.$on('timezone-changed', this.getMetrics);
  }

  getMetrics() {
    MetricsStore.getMetrics({
      start: this.selectedInterval.start.format('YYYY-MM-DD'),
      end: this.selectedInterval.end.format('YYYY-MM-DD')
    });
  }

  beforeDestroy() {
    this.$root.$off('available-timezones-loaded', this.getMetrics);
    this.$root.$off('timezone-changed', this.getMetrics);
  }
}
</script>

<style lang="scss">
.metrics-wrapper {
  width: 100%;
  display: flex;
  flex-direction: column;
  .content {
    display: flex;
    padding: 16px;
    flex-wrap: wrap;
    overflow-y: auto;
    width: 100%;
    .col-card {
      margin: 12px;
      height: 280px;
      background-color: white;
      border: 1px solid $main-border-color;
      border-radius: 8px;
      width: calc(100% / 3 - 24px);
      &.col8 {
        width: calc((100% / 3) * 2 - 24px);
      }
      .header {
        color: $secondary;
        font-weight: 500;
        height: 50px;
        display: flex;
        align-items: center;
        padding: 0px var(--main-padding);
      }
      .chart {
        height: calc(100% - 50px);
      }
    }
  }
}
</style>
