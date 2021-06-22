<template>
  <div class="week-calendar-events-wrapper" :class="modalClass">
    <div class="header">
      <span class="empty-space"></span>
      <span
        class="day"
        v-for="(day, key) in week"
        :key="`week_calendar_events_${key}`"
      >
        <span
          v-if="modalClass !== 'single-day'"
          :class="isToday(key) ? 'text-primary fw-500' : ''"
        >
          {{ key | moment('D') }} {{ key | moment('dddd') }}
        </span>
        <span v-else>
          {{ key | moment('dddd, D MMMM') }}
        </span>
      </span>
    </div>
    <div
      v-if="Object.keys(week).length !== 0"
      class="week-calendar-content"
      id="weekCalendarContent"
    >
      <span class="hours">
        <span
          class="hour-row"
          v-for="hour in filteredHours"
          :key="`week_calendar_events_${hour}`"
        >
          <span class="hour">{{ mappedHours[hour] }}</span>
          <span
            class="hour-event"
            v-for="(day, key) in week"
            :key="`week_calendar_events_hour_${key}`"
          >
            <span
              class="w-100 event root"
              v-if="rootEvent && rootEvent.hour === hour"
            >
              <span class="">Watercooler: </span>
              <span class="fw-r ml-1">{{ rootEvent.displayName }}</span>
              <span class="" v-if="rootEvent.attendanceRate">
                <b-icon-dash class="ml-1"></b-icon-dash> ({{
                  `${rootEvent.attendanceRate}% Attended`
                }})</span
              >
            </span>
            <span
              class="w-100 event"
              v-if="day[hour] && day[hour].entities"
              :id="`${key}_${hour}_event`"
              tabindex="0"
            >
              {{ day[hour].entities.length }}
              {{
                day[hour].entities.length === 1
                  ? day[hour].entityS
                  : day[hour].entityP
              }}
              <!-- <span
                v-if="day.events[hour].attendance_rate"
                class="ml-auto att-rate"
                >{{ `${Math.floor(day.events[hour].attendance_rate * 100)}%` }}
                <span class="hover-hide">Attended</span></span
              > -->
              <b-popover
                :target="`${key}_${hour}_event`"
                triggers="focus"
                :boundary="boundary"
              >
                <div
                  v-for="(entity, index) in day[hour].entities"
                  class="hover-event"
                  :key="
                    `entities_event_popover_${entity.display_name}_${hour}_${key}_${index}`
                  "
                  @click="entityClicked(entity, key)"
                  :class="{ clickable: clickable }"
                >
                  <b-avatar
                    :style="{
                      backgroundColor: getRandomColor(
                        entity.display_name.charCodeAt(
                          entity.display_name.length - 1
                        )
                      )
                    }"
                    :text="initials(entity.display_name)"
                  ></b-avatar>
                  <span class="body">
                    <span>{{ entity.display_name }}</span>
                    <span v-if="entity.members" class="att"
                      >{{ entity.members.length
                      }}{{
                        entity.members.length === 1 ? ' Member' : ' Members'
                      }}</span
                    >
                  </span>
                </div>
              </b-popover>
            </span>
          </span>
        </span>
      </span>
    </div>
    <div v-else class="no-ev">No events..</div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Emit } from 'vue-property-decorator';
// eslint-disable-next-line no-unused-vars
import moment from 'moment';
// eslint-disable-next-line no-unused-vars
import { mappedHours } from '@/store/modules/groups.store';
import { getRandomColor } from '@/shared/helpers';
// eslint-disable-next-line no-unused-vars
import { WeekGroup, WeekGroups } from '@/types/Groups';

@Component({
  components: {},
  methods: {
    getRandomColor
  },
  data() {
    return {
      mappedHours
    };
  }
})
export default class WeekCalendarEvents extends Vue {
  hours = [
    1,
    2,
    3,
    4,
    5,
    6,
    7,
    8,
    9,
    10,
    11,
    12,
    13,
    14,
    15,
    16,
    17,
    18,
    19,
    20,
    21,
    22,
    23,
    0
  ];

  @Prop({
    default: () => {
      return {};
    }
  })
  week!: WeekGroups;

  @Prop({
    default: () => {
      return {};
    }
  })
  rootEvent!: { displayName: string; hour: number };

  @Prop({
    default: () => {
      return false;
    }
  })
  clickable!: boolean;

  @Prop({
    default: () => {
      return '';
    }
  })
  modalClass!: string;

  @Prop({
    default: () => {
      return 'scrollParent';
    }
  })
  boundary!: string;

  @Emit('entity-clicked')
  entityClicked(group: WeekGroup, date: any) {
    return {
      group,
      date
    };
  }

  isToday(date: string) {
    return moment().isSame(moment(date), 'day');
  }

  get filteredHours() {
    return this.hours.filter(
      hour =>
        Object.keys(this.week).some(day => {
          return this.week[day][hour] !== undefined;
        }) || hour === this.rootEvent.hour
    );
  }

  initials(text: string) {
    let match = text?.match(/\b\w/g) || [];
    let initials = ((match.shift() || '') + (match.pop() || '')).toUpperCase();
    return initials;
  }

  hideAllPops() {
    this.$root.$emit('bv::hide::popover');
  }
}
</script>

<style lang="scss">
.week-calendar-events-wrapper {
  height: 100%;
  border: 1px solid $main-border-color;
  border-radius: $main-border-radius;
  background-color: white;
  color: $secondary;
  &.single-day {
    border: none;
    .header {
      padding: 0px var(--main-padding);
      height: 70px;
    }
    .empty-space {
      display: none;
      border: none;
    }
    .day {
      span {
        width: 100%;
        font-size: 24px;
        font-weight: 400;
        color: #212529;
      }
    }
  }
  .header {
    display: flex;
    height: 40px;
    border-bottom: 1px solid $main-border-color;
    .empty-space {
      width: 68px;
    }
    .day {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }
  .week-calendar-content {
    overflow-y: auto;
    height: calc(100% - 40px);
    position: relative;
    .hours {
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;

      .hour-row {
        display: flex;
        flex: 1;
        .hour {
          width: 78px;
          font-size: 12px;
          padding-left: 24px !important;
          padding-top: 24px !important;
          justify-content: flex-start;
        }
        .hour-event {
          flex: 1;
          align-items: center;
          justify-content: center;
          .event {
            position: relative;
            display: flex;
            font-size: 12px;
            border-radius: 50px;
            padding: 4px 12px;
            color: black;
            cursor: pointer;
            background-color: #dae7f2;
            &.root {
              background-color: #e0d1ff;
              cursor: default;
            }
            &:focus {
              outline: none;
            }
            .att-rate {
              position: absolute;
              right: 12px;
              box-shadow: -3px 1px 3px 2px #dfd2ff;
              background-color: #dfd2ff;
              color: #4b3288;
              display: flex;
              .hover-hide {
                margin-left: 1px;
                display: none;
              }
            }
            &:hover {
              .hover-hide {
                display: block;
              }
            }
          }
        }
        .hour-event,
        .hour {
          display: flex;
          padding: 12px;
          min-height: 95px;
          border-right: 1px solid $main-border-color;
          border-bottom: 1px solid $main-border-color;
        }
        &:last-child {
          .hour-event,
          .hour {
            border-bottom: none;
          }
        }
      }
    }
  }
}
.hover-event {
  font-size: 14px;
  border-bottom: 1px;
  padding: 12px 6px;
  display: flex;
  align-items: center;
  .b-avatar {
    height: 30px;
    width: 30px;
    margin-right: 8px;
  }
  &:last-child {
    border-bottom: none !important;
  }
  .body {
    display: flex;
    flex-direction: column;
    font-weight: 500;
    flex: 1;
    .att {
      font-size: 12px;
      font-weight: 400;
      color: $secondary;
    }
  }
}
.no-ev {
  text-align: center;
  padding: 10px 0px;
}
</style>
