<template>
  <div class="custom-range-input-wrapper">
    <span class="label text-secondary">
      {{ label }}
    </span>
    <span class="input-wr">
      <input
        id="myinpput"
        type="range"
        :value="value"
        :style="{ background: background }"
        @input="onInput"
        @click="onInput"
        :min="min"
        :max="max"
      />
      <span class="ml-3">{{ value }}%</span>
    </span>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, PropSync, Watch } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class RangeInput extends Vue {
  min = 1;
  max = 100;
  background = '';

  @Prop({
    default: () => {
      return '';
    }
  })
  label!: string;

  @PropSync('val')
  value!: number;

  @Watch('value')
  valueChanged() {
    this.changeBackground();
  }

  changeBackground() {
    this.background =
      'linear-gradient(to right, #007bff 0%, #007bff ' +
      (this.value / this.max) * 100 +
      '%, #afafaf ' +
      (this.value / this.max) * 100 +
      '%, #afafaf 100%)';
  }

  mounted() {
    this.changeBackground();
  }

  onInput(ev: any) {
    if (ev.srcElement) this.value = ev.srcElement.valueAsNumber;
  }
}
</script>

<style lang="scss">
@mixin thumb() {
  width: 18px;
  height: 18px;
  border-radius: 100%;
  background: white;
  position: relative;
  border: 2px solid $primary;
  z-index: 3;
  cursor: pointer;
}

@mixin track() {
  height: 1px;
  // background: #dfdfdf;
  cursor: pointer;
  transition: all 0.2s ease;
}

.custom-range-input-wrapper {
  display: flex;
  flex-direction: column;
  padding: 6px 0px;
  input {
    margin: 12px 0px;
    background: linear-gradient(
      to right,
      #82cfd0 0%,
      #82cfd0 50%,
      #fff 50%,
      #fff 100%
    );
    border-radius: 8px;
    height: 2px;
    width: 356px;
    outline: none;
    -webkit-appearance: none;
    // track
    &::-webkit-slider-runnable-track {
      @include track();
    }
    &::-moz-range-track {
      @include track();
    }
    &::-ms-track {
      @include track();
    }
    // thumb
    &::-webkit-slider-thumb {
      -webkit-appearance: none;
      @include thumb();
      transform: translateY(-50%);
    }
    &::-moz-range-thumb {
      -webkit-appearance: none;
      @include thumb();
    }
    &::-ms-thumb {
      @include thumb();
      transform: translateY(0%);
    }
  }
}
</style>
