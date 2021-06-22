<template>
  <div class="calendar-wrapper">
    <div class="custom-calendar-layout">
      <div class="header border-bottom">
        <span>Su</span>
        <span>Mo</span>
        <span>Tu</span>
        <span>We</span>
        <span>Th</span>
        <span>Fr</span>
        <span>Sa</span>
      </div>
      <b-calendar :value="date" @input="dateChanged" hide-header></b-calendar>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Emit } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class Calendar extends Vue {
  @Prop({
    default: () => {
      return '';
    }
  })
  date!: string;

  mounted() {}

  emit(ev: string) {
    this.$root.$emit(ev);
  }

  @Emit('date-changed')
  dateChanged() {}
}
</script>

<style lang="scss">
.custom-calendar-layout {
  position: relative;
  .header {
    position: absolute;
    top: 26px;
    width: 100%;
    display: flex;
    span {
      display: flex;
      line-height: 16px;
      width: 40px;
      margin: 0px 2px;
      color: $secondary;
      height: 38px;
      align-items: center;
      justify-content: center;
    }
  }
  .b-calendar {
    .b-calendar-inner {
      width: 100% !important;
      .b-calendar-nav {
        button {
          z-index: 2;
          flex: 0 !important;
          &:first-child,
          &:last-child,
          &:nth-child(3) {
            display: none !important;
          }
          &:nth-child(2) {
            text-align: left;
            margin-top: -7px;
            margin-left: 10px;
          }
          &:nth-child(4) {
            margin-top: -7px;
            margin-left: auto;
            text-align: right;
            margin-right: 7px;
          }
          &:active,
          &:hover,
          &:focus {
            box-shadow: none !important;
            outline: none;
            background-color: transparent;
            color: $secondary;
          }
        }
      }
      .b-calendar-grid {
        border: none;
        .b-calendar-grid-body {
          margin-top: 37px;
          span {
            line-height: 16px !important;
            margin: 0px 2px;
            width: 38px !important;
            height: 38px !important;
            border-radius: $main-border-radius !important;
            font-weight: 400 !important;
            display: flex;
            align-items: center;
            justify-content: center;
            &.active {
              border: 1px solid $primary !important;
              color: black;
              background-color: rgba($primary, 0.1);
            }
          }
        }
        .b-calendar-grid-weekdays {
          display: none !important;
        }
        header {
          font-weight: 500 !important;
          color: #212529;
          position: absolute;
          width: 100%;
          margin-top: -33px;
          padding: 0px;
          height: 20px;
        }
        footer {
          display: none;
        }
      }
    }
  }
}
</style>
