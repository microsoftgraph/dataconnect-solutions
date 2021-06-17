<template>
  <b-modal :id="id" dialog-class="default-modal" centered @change="init">
    <template v-slot:modal-header="{ close }">
      <span class="title">{{ group.displayName }} Schedule</span>
      <b-icon-x @click="close"> </b-icon-x>
    </template>
    <div class="content">
      <div class="calendar-wrapper"></div>
      <div class="content-wrapper">
        <WeekCalendarEvents
          :modalClass="'single-day'"
          :boundary="'viewport'"
          :week="membersEvents"
          :rootEvent="rootEvent"
        />
      </div>
    </div>
    <template v-slot:modal-footer>
      <b-button variant="primary" class="default-size" @click="close">
        Close
      </b-button>
    </template>
  </b-modal>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import WeekCalendar from '@/components/WeekCalendar.vue';
import WeekCalendarEvents from '@/components/WeekCalendarEvents.vue';
// eslint-disable-next-line no-unused-vars
import moment from 'moment';
import { GroupsStore } from '@/store/modules/groups.store';
// eslint-disable-next-line no-unused-vars
import { DayGroup } from '@/types/Groups';

@Component({
  components: { WeekCalendar, WeekCalendarEvents }
})
export default class GroupModal extends Vue {
  id = 'group-modal';
  rootEvent: any = {};

  @Prop({
    default: () => {
      return {};
    }
  })
  group!: DayGroup;

  get selectedDate() {
    return GroupsStore.selectedDate;
  }

  get membersEvents() {
    const formattedDate = this.selectedDate.format('YYYY-MM-DD');
    return {
      [formattedDate]: GroupsStore.groupsMembersSchedule[this.group.name]
        ? GroupsStore.groupsMembersSchedule[this.group.name]
        : {}
    };
  }

  mounted() {}

  init() {
    this.getGroupMembersSchedule(this.group.name);
    this.rootEvent = {
      displayName: this.group.displayName,
      hour: this.group.time,
      attendanceRate: this.group.attendanceRate
    };
  }

  getGroupMembersSchedule(name: string) {
    GroupsStore.getGroupMembersSchedule(name);
  }

  close() {
    this.$bvModal.hide(this.id);
  }

  open() {
    this.$bvModal.show(this.id);
  }

  refresh() {
    location.reload();
  }
}
</script>

<style lang="scss">
#group-modal {
  overflow: hidden;
  .modal-dialog {
    max-width: calc(500px) !important;
    .modal-content {
      .modal-body {
        padding: 0px !important;
        max-height: calc(100vh - 200px);
        overflow-y: auto;
        .content {
          display: flex;
          .content-wrapper {
            flex: 1;
          }
        }
      }
    }
  }
}
</style>
