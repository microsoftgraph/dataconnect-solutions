<template>
  <div class="kpi-chart">
    <div
      class="stat-row"
      v-for="(data, index) in chartData"
      :key="`kpi_stat_${index}`"
    >
      <span class="value">
        <span
          v-if="
            rawData[data.key] &&
              rawData[data.key][0] &&
              rawData[data.key][0][data.maxValueKey] !== undefined &&
              rawData[data.key][0][data.minValueKey] !== undefined
          "
          class="small-label "
        >
          <span
            v-if="rawData[data.key] && rawData[data.key][0][data.maxValueKey]"
            class="max"
            >Max: {{ rawData[data.key][0][data.maxValueKey] }}</span
          >
          <span
            v-if="rawData[data.key] && rawData[data.key][0][data.minValueKey]"
            class="min"
            >Min: {{ rawData[data.key][0][data.minValueKey] }}</span
          >
        </span>
        <span v-if="rawData[data.key] && rawData[data.key][0]">
          {{ rawData[data.key][0][data.avgValueKey] }}
        </span>
      </span>

      <span class="label">
        {{ data.label }}
        <br />
      </span>
    </div>
  </div>
</template>

<script lang="ts">
import { MetricsStore } from '@/store/modules/metrics.store';
import { Component, Vue, Prop } from 'vue-property-decorator';

@Component({
  components: {},
  methods: {}
})
export default class BarChart extends Vue {
  @Prop({
    default: () => {
      return {};
    }
  })
  chartData!: any;

  get rawData() {
    return MetricsStore.rawChartData;
  }

  mounted() {}
}
</script>

<style lang="scss">
.kpi-chart {
  display: flex;
  width: 100%;
  flex-direction: column;
  justify-content: center;
  height: 100%;
  .stat-row {
    padding: 4px var(--main-padding);
    display: flex;
    align-items: stretch;
    justify-content: flex-start;
    .value {
      display: flex;
      flex-direction: row;
      justify-content: flex-end;
      flex: 2;
      font-size: 24px;
      text-align: right;
      color: #a686f7;
      align-items: center;
      .small-label {
        padding: 2px 5px;
        margin-left: auto;
        margin-right: 16px;
        font-size: 12px;
        color: grey;
        border: 1px solid grey;
        border-radius: 4px;
        display: flex;
        align-items: flex-end;
        flex-direction: column;
      }
    }
    .label {
      flex: 3;
      margin-left: 10px;
      display: flex;
      align-items: center;
    }
  }
}
</style>
