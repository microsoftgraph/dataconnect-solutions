<template>
  <div class="groups-wrapper default-layout">
    <transition name="slide-left">
      <div class="left-wrapper calendar-wrapper" v-if="showLeftPanel">
        <WeekCalendar />
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
        <span v-if="selectedWeek[0] && selectedWeek[6]">
          {{ selectedWeek[0] | moment('D MMMM') }} -
          {{ selectedWeek[6] | moment('D MMMM YYYY') }}
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
        <WeekCalendarEvents
          :clickable="true"
          :week="weekGroups"
          @entity-clicked="groupClicked"
        />
      </div>
    </div>
    <GroupMembersModal :members="selectedMembers" />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
// eslint-disable-next-line no-unused-vars
import { GroupsStore } from '@/store/modules/groups.store';
import WeekCalendar from '@/components/WeekCalendar.vue';
import WeekCalendarEvents from '@/components/WeekCalendarEvents.vue';
import moment from 'moment';
import GroupMembersModal from '@/components/modals/GroupMembersModal.vue';
// eslint-disable-next-line no-unused-vars
import { WeekGroup } from '@/types/Groups';
import CurrentTimezoneDropdown from '@/components/CurrentTimezoneDropdown.vue';

@Component({
  components: {
    CurrentTimezoneDropdown,
    WeekCalendar,
    WeekCalendarEvents,
    GroupMembersModal
  }
})
export default class Week extends Vue {
  selectedMembersStatus: any = {};
  selectedMembersAttendaceStatus: any = {};

  selectedMembers = [];

  showLeftPanel = true;
  showLeftPanelAnimation = '';

  get weekGroups() {
    return GroupsStore.weekGroups;
  }

  get selectedWeek() {
    return GroupsStore.selectedWeek;
  }

  get selectedDate() {
    return GroupsStore.selectedDate;
  }

  set selectedDate(value: any) {
    GroupsStore.setDate(moment(value));
  }

  @Watch('selectedWeek')
  selectedWeekChanged() {
    this.getWeekSchedule(
      this.selectedWeek[0].format('YYYY-MM-DD'),
      this.selectedWeek[6].format('YYYY-MM-DD')
    );
  }

  mounted() {
    if (GroupsStore.availableTimezonesLoaded) this.getWeekScheduleInit();
    this.$root.$on('available-timezones-loaded', this.getWeekScheduleInit);
    // listener for timezone change
    this.$root.$on('timezone-changed', this.getWeekScheduleInit);
  }

  getWeekScheduleInit() {
    this.getWeekSchedule(
      this.selectedWeek[0].format('YYYY-MM-DD'),
      this.selectedWeek[6].format('YYYY-MM-DD')
    );
  }

  getWeekSchedule(start: string, end: string) {
    GroupsStore.getWeekSchedule({
      startDate: start,
      endDate: end
    });
  }

  selectToday() {
    this.selectedDate = moment();
  }

  decreaseDate() {
    this.selectedDate = moment(this.selectedDate).subtract('7', 'days');
  }

  increaseDate() {
    this.selectedDate = moment(this.selectedDate).add('7', 'days');
  }

  groupClicked(data: { date: string; group: WeekGroup }) {
    this.selectedMembers = data.group.members.map(x => {
      return {
        name: x.name,
        email: x.mail
      };
    }) as any;
    this.$root.$emit('bv::hide::popover');
    this.$bvModal.show('group-members-modal');
  }

  beforeDestroy() {
    this.$root.$off('timezone-changed', this.getWeekScheduleInit);
    this.$root.$off('available-timezones-loaded', this.getWeekScheduleInit);
  }

  toggleLeftPanel() {
    this.showLeftPanel = !this.showLeftPanel;
    this.$root.$emit('show-week-calendar-panel');
    this.showLeftPanelAnimation = 'throb';
    setTimeout(() => {
      this.showLeftPanelAnimation = '';
    }, 500);
  }
}
</script>

<style lang="scss"></style>
