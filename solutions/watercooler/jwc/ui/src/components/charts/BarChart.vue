<template>
  <div class="bar-horizontal-chart">
    <div
      class="stat-row"
      v-for="(data, index) in chartData"
      :key="`chart_stat_${index}`"
    >
      <span class="label" :class="data.cssClass"> {{ data.label }}</span>
      <span
        v-if="!data.labelOnly"
        class="bar"
        :style="{ backgroundColor: data.backgroundColor }"
      >
        <span
          class="fill"
          :style="{
            backgroundColor: data.borderColor,
            width: `${(data.value * 100) / chartData[0].value}%`
          }"
        ></span>
      </span>
      <span v-if="!data.labelOnly" class="value"> {{ data.value }}</span>
    </div>
  </div>
</template>

<script lang="ts">
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

  mounted() {}
}
</script>

<style lang="scss">
.bar-horizontal-chart {
  display: flex;
  width: 100%;
  flex-direction: column;
  justify-content: center;
  height: 100%;
  .stat-row {
    display: flex;
    width: 100%;
    padding: 4px var(--main-padding);
    display: flex;
    align-items: center;
    .value {
      width: 60px;
      justify-content: flex-end;
    }
    .label {
      width: 90px;
    }
    .value {
      display: flex;
      align-items: center;
    }
    .bar {
      height: 12px;
      border-radius: 50px;
      width: calc(100% - 145px);
      display: flex;
      .fill {
        height: 100%;
        border-radius: 50px;
      }
    }
  }
}
</style>
