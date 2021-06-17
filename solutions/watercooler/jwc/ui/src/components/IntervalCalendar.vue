<template>
  <div class="interval-calendar-wrapper">
    <div class="dates-wrapper">
      <span
        class="from time"
        :class="{ selected: selectedIndex === 0 }"
        @click="selectedIndex = 0"
      >
        <span class="label">From:</span>
        <span class="value" v-if="localInterval.start">{{
          localInterval.start.format('D MMMM YYYY')
        }}</span>
      </span>
      <span
        class="to time"
        :class="{ selected: selectedIndex === 1 }"
        @click="selectedIndex = 1"
      >
        <span class="label">To:</span>
        <span class="value" v-if="localInterval.end">{{
          localInterval.end.format('D MMMM YYYY')
        }}</span>
      </span>
    </div>
    <div class="d-flex">
      <div class="interval-calendar start mr-4">
        <div class="controls mb-3">
          <span class="icon-holder" @click="decreaseDate">
            <b-icon-chevron-left></b-icon-chevron-left>
          </span>
          <span v-if="currentStartDisplayDate">{{
            currentStartDisplayDate.format('MMMM YYYY')
          }}</span>
        </div>
        <div class="header">
          <span>Su</span>
          <span>Mo</span>
          <span>Tu</span>
          <span>We</span>
          <span>Th</span>
          <span>Fr</span>
          <span>Sa</span>
        </div>
        <div
          class="week-wrapper"
          v-for="(week, index) in startWeeks"
          :key="`week_calendar_${index}`"
        >
          <div class="week">
            <div
              class="day"
              v-for="day in week"
              :key="`week_calendar_day_${day}`"
              @click="selectDay(day)"
            >
              <span
                class="date"
                :class="[
                  getBorderRadiusClass(day),
                  {
                    selected: isDateSelected(day),
                    between: isDateBetween(day),
                    hidden: !isSameMonth(day, currentStartDisplayDate)
                  }
                ]"
              >
                {{ day.date() }}
              </span>
            </div>
          </div>
        </div>
      </div>
      <div class="interval-calendar end ml-4">
        <div class="controls mb-3">
          <span v-if="currentEndDisplayDate">{{
            currentEndDisplayDate.format('MMMM YYYY')
          }}</span>
          <span class="icon-holder" @click="increaseDate">
            <b-icon-chevron-right></b-icon-chevron-right>
          </span>
        </div>
        <div class="header">
          <span>Su</span>
          <span>Mo</span>
          <span>Tu</span>
          <span>We</span>
          <span>Th</span>
          <span>Fr</span>
          <span>Sa</span>
        </div>
        <div
          class="week-wrapper"
          v-for="(week, index) in endWeeks"
          :key="`week_calendar_${index}`"
        >
          <div class="week">
            <div
              class="day"
              v-for="day in week"
              :key="`week_calendar_day_${day}`"
              @click="selectDay(day)"
            >
              <span
                class="date"
                :class="[
                  getBorderRadiusClass(day),
                  {
                    selected: isDateSelected(day),
                    between: isDateBetween(day),
                    hidden: !isSameMonth(day, currentEndDisplayDate)
                  }
                ]"
              >
                {{ day.date() }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
// eslint-disable-next-line no-unused-vars
import { CalendarTimeInterval } from '@/store/modules/metrics.store';
// eslint-disable-next-line no-unused-vars
import { WeekType } from '@/types/Week';
import moment from 'moment';
import { Component, Vue, Prop } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class IntervalCalendar extends Vue {
  selectedIndex = 1;

  startWeeks: WeekType = {
    0: [],
    1: [],
    2: [],
    3: [],
    4: []
  };

  endWeeks: WeekType = {
    0: [],
    1: [],
    2: [],
    3: [],
    4: []
  };

  @Prop({
    default: () => {
      return '';
    }
  })
  date!: string;

  @Prop({
    default: () => {
      return false;
    }
  })
  isInterval!: boolean;

  @Prop({
    default: () => {
      return {};
    }
  })
  interval!: CalendarTimeInterval;

  displayedMonths: CalendarTimeInterval = {} as CalendarTimeInterval;

  localInterval: CalendarTimeInterval = {} as CalendarTimeInterval;

  getBorderRadiusClass(day: moment.Moment) {
    if (day.isSame(this.localInterval.start.format('YYYY-MM-DD')))
      return 'first';
    if (day.isSame(this.localInterval.end.format('YYYY-MM-DD'))) return 'last';
  }

  isDateSelected(day: moment.Moment) {
    return (
      moment(day.format('YYYY-MM-DD')).isSame(
        this.localInterval.start.format('YYYY-MM-DD')
      ) ||
      moment(day.format('YYYY-MM-DD')).isSame(
        this.localInterval.end.format('YYYY-MM-DD')
      )
    );
  }

  isDateBetween(day: moment.Moment) {
    return (
      day.isAfter(this.localInterval.start, 'date') &&
      day.isBefore(this.localInterval.end, 'date')
    );
  }

  isSameMonth(day: moment.Moment, date: moment.Moment) {
    return day.isSame(date, 'month');
  }

  increaseDate() {
    this.displayedMonths.start.add(1, 'month'),
      this.displayedMonths.end.add(1, 'month');
    this.startWeeks = this.initWeeks(this.displayedMonths.start);
    this.endWeeks = this.initWeeks(this.displayedMonths.end);
  }

  decreaseDate() {
    this.displayedMonths.start.subtract(1, 'month'),
      this.displayedMonths.end.subtract(1, 'month');
    this.startWeeks = this.initWeeks(this.displayedMonths.start);
    this.endWeeks = this.initWeeks(this.displayedMonths.end);
  }

  get currentStartDisplayDate() {
    return this.startWeeks[3][0];
  }

  get currentEndDisplayDate() {
    return this.endWeeks[3][0];
  }

  mounted() {
    this.localInterval = {
      start: this.interval.start.clone(),
      end: this.interval.end.clone()
    };
    this.displayedMonths = {
      start: this.interval.start.clone(),
      end: this.interval.end.isSame(this.localInterval.start, 'month')
        ? this.interval.end.clone().add(1, 'month')
        : this.interval.end.clone()
    };
    this.startWeeks = this.initWeeks(this.displayedMonths.start);
    this.endWeeks = this.initWeeks(this.displayedMonths.end);
  }

  initWeeks(value: moment.Moment) {
    const start = moment(value)
      .startOf('month')
      .startOf('week');
    const end = moment(value)
      .endOf('month')
      .endOf('week');
    let now = start.clone(),
      dates: WeekType = {
        0: [],
        1: [],
        2: [],
        3: [],
        4: [],
        5: []
      };
    let index = 0;
    while (now.isSameOrBefore(end)) {
      dates[Math.floor(index / 7)].push(now.clone());
      now.add(1, 'days');
      index++;
    }
    return dates;
  }

  selectDay(day: moment.Moment) {
    if (this.selectedIndex === 0) {
      this.localInterval.start = day;
      this.selectedIndex = 1;
      // if (day.isAfter(this.localInterval.end))
    }
    if (day.isAfter(this.localInterval.end, 'date')) {
      this.localInterval.end = day;
      this.selectedIndex = 1;
      return;
    }
    if (day.isBefore(this.localInterval.start, 'date')) {
      this.localInterval.start = day;
      this.selectedIndex = 1;
      return;
    }
    if (
      day.isBefore(this.localInterval.end, 'date') &&
      day.isAfter(this.localInterval.start, 'date')
    ) {
      this.localInterval.end = day;
      this.selectedIndex = 1;
      return;
    }
  }
}
</script>

<style lang="scss">
.interval-calendar-wrapper {
  display: flex;
  flex-direction: column;
  position: relative;
  width: 100%;
  min-height: 300px;
  justify-content: space-between;
  .dates-wrapper {
    display: flex;
    flex-direction: row;
    margin-bottom: 10px;
    margin-bottom: 10px;
    .time {
      flex: 1;
      border: 1px solid #dfdfdf;
      padding: 10px;
      cursor: pointer;
      &:first-child {
        border-top-left-radius: 4px;
        border-bottom-left-radius: 4px;
      }
      &:last-child {
        border-top-right-radius: 4px;
        border-bottom-right-radius: 4px;
      }
      &:hover {
        background-color: $main-hover-color;
      }
      .label {
        font-weight: 500;
        margin-right: 2px;
      }
      &.selected {
        border: 1px solid #007bff !important;
        color: black !important;
        background-color: rgba(0, 123, 255, 0.1);
      }
    }
  }
  .interval-calendar {
    flex: 1;
    .controls {
      display: flex;
      height: 20px;
      justify-content: center;
      align-items: center;
      font-weight: 500 !important;
      color: #212529;
      .icon-holder {
        width: 32px;
        height: 32px;
        display: flex;
        align-items: center;
        border-radius: $main-border-radius;
        cursor: pointer;
        justify-content: center;
        position: absolute;
        svg {
          color: $secondary;
        }
        &:first-child {
          margin-left: 6px;
        }
        &:last-child {
          margin-right: 2px;
        }
        &:hover {
          background-color: $main-hover-color;
          svg {
            fill: black;
          }
        }
      }
    }
    .header {
      display: flex;
      margin-bottom: 12px;
      span {
        flex: 1;
        text-align: center;
        color: $secondary;
      }
    }
    .week {
      cursor: pointer;
      display: flex;
      .day {
        display: flex;
        text-align: center;
        flex-direction: column;
        width: 14.28%;
        .date {
          padding: 10px 0px;
          &.hidden {
            visibility: hidden;
          }
          &.selected {
            border: 1px solid #007bff !important;
            color: black !important;
            background-color: rgba(0, 123, 255, 0.1);
            &.first {
              border-top-left-radius: $main-border-radius;
              border-bottom-left-radius: $main-border-radius;
            }
            &.last {
              border-top-right-radius: $main-border-radius;
              border-bottom-right-radius: $main-border-radius;
            }
            &.single {
              border-radius: $main-border-radius;
            }
          }
          &.between {
            color: black !important;
            border: 1px solid transparent !important;
            border-radius: 0px;
            background-color: rgba(0, 123, 255, 0.1);
          }
        }
        &:hover {
          background-color: $main-hover-color;
        }
        &:first-child,
        &:last-child {
          .date {
            color: #6c757d;
          }
        }
      }
    }
    &:first-child {
      .icon-holder {
        left: 0;
      }
    }
    &:last-child {
      .icon-holder {
        right: 0;
      }
    }
  }
}
</style>
