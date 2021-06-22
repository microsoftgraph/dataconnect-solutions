<template>
  <div class="groups-wrapper default-layout">
    <transition name="slide-left">
      <div class="left-wrapper" v-if="showLeftPanel">
        <Calendar
          class="h-100"
          :date="selectedDateFormated"
          @date-changed="calendarDateChanged"
        />
      </div>
    </transition>
    <span class="left-toggler" :class="{ expanded: showLeftPanel }">
      <span
        @click="toggleLeftPanel()"
        v-b-tooltip.hover
        :title="showLeftPanel ? 'Hide Calendar' : 'Show Calendar'"
        class="round-icon"
      >
        <b-icon
          ref="showLeftPanel"
          :icon="showLeftPanel ? 'chevron-left' : 'calendar3'"
          :animation="showLeftPanelAnimation"
        ></b-icon> </span
    ></span>
    <div class="right-wrapper">
      <div class="header-wrapper border-bottom">
        <span>
          {{ selectedDate | moment('D MMMM YYYY') }}
        </span>
        <CurrentTimezoneDropdown class="ml-auto mr-3" />
        <b-button-group class="custom-btn-group">
          <b-button variant="none" @click="decreaseDate"
            ><b-icon-chevron-left></b-icon-chevron-left>
          </b-button>
          <b-button variant="none" @click="selectToday">Today</b-button>
          <b-button variant="none" @click="increaseDate"
            ><b-icon-chevron-right></b-icon-chevron-right
          ></b-button>
        </b-button-group>
      </div>
      <div class="right-content-wrapper">
        <b-overlay :show="loading" rounded="sm">
          <div
            class="group-bundle"
            v-for="(timeslot, key) in dayGroups"
            :key="`group_bundle_${key}`"
          >
            <div
              class="text-secondary time-label border"
              :class="{
                collapsed: openedHourTabs[key],
                'not-collapsed': !openedHourTabs[key]
              }"
              @click="openTab(key, timeslot.hour)"
            >
              <span class="fw-r"
                >{{ mappedHours[timeslot.hour] }} <b-icon-dash></b-icon-dash
              ></span>
              <span class="ml-1">
                <span class="mr-1">{{ timeslot.total }}</span>
                {{ timeslot.total === 1 ? 'Group' : 'Groups' }}
              </span>
            </div>
            <div
              v-if="openedHourTabs[key]"
              :key="`tab_day_view_${openedHourTabs[key]}`"
              class="group-rows-wrapper"
            >
              <GroupRow
                v-for="(group, ind) in timeslot.groups"
                :group="group"
                :key="`group_${ind}_${group.displayName}`"
                @viewMembers="viewMembers(group)"
                @viewGroup="viewGroup(group)"
              />
            </div>
          </div>
          <div v-if="dayGroups && Object.keys(dayGroups).length === 0">
            No events ...
          </div>
        </b-overlay>
      </div>
    </div>
    <GroupModal :group="selectedGroup" />
    <GroupMembersModal :members="selectedMembers" />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import { GroupsStore, mappedHours } from '@/store/modules/groups.store';
import GroupRow from '@/components/GroupRow.vue';
import GroupModal from '@/components/modals/GroupModal.vue';
import GroupMembersModal from '@/components/modals/GroupMembersModal.vue';
import Calendar from '@/components/Calendar.vue';
import moment from 'moment';
// eslint-disable-next-line no-unused-vars
import { DayGroup, GroupMember } from '@/types/Groups';
import CurrentTimezoneDropdown from '@/components/CurrentTimezoneDropdown.vue';

@Component({
  components: {
    GroupRow,
    GroupModal,
    GroupMembersModal,
    Calendar,
    CurrentTimezoneDropdown
  },
  data() {
    return {
      mappedHours
    };
  }
})
export default class Day extends Vue {
  selectedMembers: GroupMember[] = [];
  selectedGroup: DayGroup = {} as DayGroup;
  openedHourTabs: any = {};
  loading = false;

  showLeftPanel = true;
  showLeftPanelAnimation = '';

  mounted() {
    this.$root.$on('available-timezones-loaded', GroupsStore.getDaySchedule);
    this.$root.$on('timezone-changed', GroupsStore.getDaySchedule);
    // when visiting page
    if (GroupsStore.availableTimezonesLoaded) GroupsStore.getDaySchedule();
  }

  viewGroup(group: DayGroup) {
    this.selectedGroup = group;
    this.$bvModal.show('group-modal');
  }

  viewMembers(group: DayGroup) {
    this.selectedMembers = group.members;
    this.$bvModal.show('group-members-modal');
  }

  openTab(index: number, hour: number) {
    Vue.set(this.openedHourTabs, index, !this.openedHourTabs[index]);
    if (this.openedHourTabs[index]) GroupsStore.getDayHourGroups(hour);
  }

  get dayGroups() {
    return GroupsStore.dayGroups[this.selectedDate.format('YYYY-MM-DD')];
  }

  calendarDateChanged(val: string) {
    this.selectedDateFormated = val;
  }

  @Watch('selectedDate')
  dateChanged() {
    // Close all tabs when date changes
    Object.keys(this.openedHourTabs).forEach((key: any) => {
      this.openedHourTabs[key] = false;
    });
    // Refetch groups every time date changes
    GroupsStore.getDaySchedule();
  }

  get selectedDateFormated() {
    return GroupsStore.selectedDate.toDate();
  }

  set selectedDateFormated(value: any) {
    GroupsStore.setDate(moment(value));
  }

  get selectedDate() {
    return GroupsStore.selectedDate;
  }

  set selectedDate(value: any) {
    GroupsStore.setDate(moment(value));
  }

  selectToday() {
    this.selectedDate = moment();
  }

  decreaseDate() {
    this.selectedDate = moment(this.selectedDate).subtract('1', 'days');
  }

  increaseDate() {
    this.selectedDate = moment(this.selectedDate).add('1', 'days');
  }

  beforeDestroy() {
    this.$root.$off('timezone-changed', GroupsStore.getDaySchedule);
    this.$root.$off('available-timezones-loaded', GroupsStore.getDaySchedule);
  }

  toggleLeftPanel() {
    this.showLeftPanel = !this.showLeftPanel;
    this.$root.$emit('show-day-calendar-panel');
    this.showLeftPanelAnimation = 'throb';
    setTimeout(() => {
      this.showLeftPanelAnimation = '';
    }, 500);
  }
}
</script>

<style lang="scss">
.groups-wrapper {
  .left-wrapper {
    flex: 0 0 345px;
  }
  .right-wrapper {
    .group-row-wrapper,
    .time-label {
      margin-bottom: 8px;
    }
    .time-label {
      padding: 0px 12px;
      height: 40px;
      display: flex;
      align-items: center;
      cursor: pointer;
      border-radius: $main-border-radius;
      &.collapsed {
        &::before {
          display: inline-block;
          margin-left: 0.255em;
          vertical-align: 1px;
          content: '';
          border-top: 7px solid;
          border-right: 5px solid transparent;
          border-bottom: 0;
          border-left: 5px solid transparent;
          margin-right: 7px;
        }
      }
      &:hover {
        background-color: $main-hover-color;
      }
      &.not-collapsed {
        &::before {
          display: inline-block;
          margin-left: 0.255em;
          vertical-align: 1px;
          content: '';
          margin-right: 7px;
          border-top: 5px solid transparent;
          border-bottom: 5px solid transparent;
          border-left: 8px solid;
        }
      }
      &:focus {
        outline: none;
      }
    }
    .group-rows-wrapper {
      padding-left: 36px;
    }
  }
}
</style>
