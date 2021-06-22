<template>
  <div class="heatmap-chart">
    <div
      class="stat-row stat"
      v-for="(d, index) in chartData.y"
      :key="`heatmap_stat_${index}`"
    >
      <span class="label"> {{ d }}</span>
      <span class="values">
        <span
          class="value"
          v-for="hour in validHours"
          :key="`val_stat_${hour}_${d}`"
          ><span
            v-if="
              rawData.weekGroupLoadResponse !== undefined &&
                rawData.weekGroupLoadResponse[d] !== undefined
            "
            v-b-tooltip.hover
            :title="rawData.weekGroupLoadResponse[d][hour]"
            :style="{
              backgroundColor: getBackgroundColor(
                rawData.weekGroupLoadResponse[d][hour]
              )
            }"
            >{{
          }}</span>
        </span>
      </span>
    </div>
    <div class="stat-row">
      <span class="label"></span>
      <span
        class="values text-secondary"
        v-for="(val, ind) in validHours"
        :key="`static_val_stat_${ind}`"
        >{{ mappedHours[val] }}</span
      >
    </div>
  </div>
</template>

<script lang="ts">
import { MetricsStore } from '@/store/modules/metrics.store';
import { Component, Vue } from 'vue-property-decorator';
import { mappedHours } from '@/store/modules/groups.store';

@Component({
  components: {},
  methods: {},
  data() {
    return {
      mappedHours
    };
  }
})
export default class Heatmap extends Vue {
  colors = ['#CFE1FF', '#A6C8FF', '#6A9BEB', '#4276C9', '#2C5AA3'];
  min = 23;
  max = 0;

  get chartData() {
    return MetricsStore.chartData.Heatmap.data;
  }

  get validHours() {
    this.min = 23;
    this.max = 0;
    let arr: string[] = [];
    if (this.rawData.weekGroupLoadResponse) {
      // check each day to see hour for first (min) and last event(max)
      Object.keys(this.rawData.weekGroupLoadResponse as any).forEach(day => {
        arr = Object.keys((this.rawData.weekGroupLoadResponse as any)[day])
          .sort((a, b) => {
            if (+a > +b) return 1;
            else return -1;
          })
          .filter(
            hour => (this.rawData.weekGroupLoadResponse as any)[day][hour] !== 0
          );
        if (arr.length !== 0) {
          this.min = Math.min(+arr[0], this.min);
          this.max = Math.max(+arr[arr.length - 1], this.max);
        }
      });
    }
    return this.chartData.x.filter(
      hour => hour >= this.min && hour <= this.max
    );
  }

  get rawData() {
    return MetricsStore.rawChartData;
  }

  get maxHeatMapValue() {
    let max = 0;
    if (MetricsStore.rawChartData.weekGroupLoadResponse)
      Object.keys(MetricsStore.rawChartData.weekGroupLoadResponse).map(
        (key: any) => {
          Object.keys(
            (MetricsStore.rawChartData.weekGroupLoadResponse as any)[key]
          ).map(nr => {
            max = Math.max(
              max,
              (MetricsStore.rawChartData.weekGroupLoadResponse as any)[key][nr]
            );
          });
        }
      );
    return max;
  }

  mounted() {}

  getBackgroundColor(value: number) {
    let index = (value * 100) / this.maxHeatMapValue;
    switch (true) {
      case index >= 80:
        return this.colors[this.colors.length - 1];
      case index <= 80 && index >= 60:
        return this.colors[this.colors.length - 2];
      case index <= 60 && index >= 40:
        return this.colors[this.colors.length - 3];
      case index <= 40 && index >= 20:
        return this.colors[this.colors.length - 4];
      case index <= 20 && index >= 0:
        return this.colors[this.colors.length - 5];
    }
  }
}
</script>

<style lang="scss">
.heatmap-chart {
  display: flex;
  width: 100%;
  border-radius: 8px;
  flex-direction: column;
  justify-content: center;
  height: 100%;
  padding-top: 10px;
  .stat-row {
    padding: 0px var(--main-padding);
    height: 35px;
    display: flex;
    align-items: center;
    justify-content: flex-start;
    &.stat {
      &:first-child {
        .value {
          &:first-child {
            border-top-left-radius: 8px;
            span {
              border-top-left-radius: 8px;
            }
          }
          &:last-child {
            border-top-right-radius: 8px;
            span {
              border-top-right-radius: 8px;
            }
          }
        }
      }
      &:nth-child(5) {
        .value {
          &:first-child {
            border-bottom-left-radius: 8px;
            span {
              border-bottom-left-radius: 8px;
            }
          }
          &:last-child {
            border-bottom-right-radius: 8px;
            span {
              border-bottom-right-radius: 8px;
            }
          }
        }
      }
    }
    .label {
      width: 28px;
      margin-right: 10px;
      text-transform: capitalize;
      text-align: right;
    }
    .values {
      height: 100%;
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      .value {
        display: flex;
        flex: 1;
        background-color: #cfe1ff;
        height: 100%;
        border: 1px solid white;
        span {
          height: 100%;
          width: 100%;
        }
      }
    }
  }
}
</style>
