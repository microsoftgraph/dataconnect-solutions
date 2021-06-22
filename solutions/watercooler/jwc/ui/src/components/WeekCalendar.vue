<template>
  <div class="week-calendar-wrapper">
    <div class="controls">
      <span class="icon-holder" @click="decreaseDate">
        <b-icon-chevron-left></b-icon-chevron-left>
      </span>
      <span>{{ currentDisplayDate.format('MMMM YYYY') }}</span>
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
      v-for="(week, index) in weeks"
      :key="`week_calendar_${index}`"
    >
      <div
        v-if="week.length !== 0"
        class="week"
        :class="{ selected: isWeekSelected(week) }"
      >
        <div
          class="day"
          v-for="day in week"
          :key="`week_calendar_day_${day}`"
          @click="selectWeek(day)"
        >
          <span class="date">
            {{ day.date() }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
// eslint-disable-next-line no-unused-vars
import moment, { Moment } from 'moment';
import { GroupsStore } from '@/store/modules/groups.store';
// eslint-disable-next-line no-unused-vars
import { WeekType } from '@/types/Week';

@Component({
  components: {}
})
export default class WeekCalendar extends Vue {
  weeks: WeekType = {
    0: [] as Moment[],
    1: [] as Moment[],
    2: [] as Moment[],
    3: [] as Moment[],
    4: [] as Moment[]
  };

  get currentDisplayDate() {
    return this.weeks[3][0] || this.selectedDate;
  }

  get selectedWeek() {
    return GroupsStore.selectedWeek;
  }

  set selectedWeek(value: any) {
    GroupsStore.setWeek(value);
  }

  get selectedDate() {
    return GroupsStore.selectedDate;
  }

  set selectedDate(value: any) {
    GroupsStore.setDate(moment(value));
  }

  mounted() {
    this.initWeeks();
  }

  selectWeek(value = '') {
    this.selectedDate = moment(value);
    this.initWeeks();
  }

  @Watch('selectedDate')
  dateChanged() {
    this.initWeeks(null, true);
  }

  initWeeks(value: any = null, override = false) {
    // if value is provided then search for that specific month
    const start = moment(value ? value : this.selectedDate)
      .startOf('month')
      .startOf('week');
    const end = moment(value ? value : this.selectedDate)
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
    let index = 0,
      weekIndex = null;
    while (now.isSameOrBefore(end)) {
      dates[Math.floor(index / 7)].push(now.clone());
      now.add(1, 'days');
      // if override then set selectedWeek
      if (
        override &&
        moment(now.format('YYYY-MM-DD')).isSame(
          GroupsStore.selectedDate.format('YYYY-MM-DD')
        )
      )
        weekIndex = Math.floor(index / 7);
      index++;
    }
    if (weekIndex !== null) this.selectedWeek = dates[weekIndex];
    this.weeks = dates;
  }

  decreaseDate() {
    this.initWeeks(moment(this.currentDisplayDate).subtract(1, 'month'));
  }

  increaseDate() {
    this.initWeeks(moment(this.currentDisplayDate).add(1, 'month'));
  }

  isWeekSelected(week: moment.Moment[]) {
    let isSame = false;
    week.forEach(day => {
      if (
        moment(day.format('YYYY-MM-DD')).isSame(
          GroupsStore.selectedDate.format('YYYY-MM-DD')
        )
      ) {
        isSame = true;
      }
    });
    return isSame;
  }
}
</script>

<style lang="scss">
.week-calendar-wrapper {
  flex: 1;
  .controls {
    display: flex;
    height: 20px;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
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
    margin-bottom: 8px;
    display: flex;
    border: 1px solid $main-border-color;
    border-radius: 8px;
    &.selected {
      border-color: $primary;
      background-color: rgba($primary, 0.1);
    }
    &:hover {
      background-color: $main-hover-color;
    }
    .day {
      display: flex;
      text-align: center;
      flex-direction: column;
      flex: 1;
      > span {
        padding: 10px 0px;
        &.weekday {
          border-bottom: 1px solid $main-border-color;
        }
      }
      &:first-child,
      &:last-child {
        .date {
          color: #6c757d;
        }
      }
    }
  }
}
</style>
