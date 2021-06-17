import {
  Action,
  getModule,
  Module,
  Mutation,
  VuexModule
} from 'vuex-module-decorators';
import store from '@/store';
import moment from 'moment';
import axios from 'axios';
import {
  DayGroup,
  DayGroups,
  GroupMembersSchedule,
  WeekGroup,
  WeekGroupModel,
  WeekGroups
} from '@/types/Groups';

export const mappedHours = {
  1: '1 AM',
  2: '2 AM',
  3: '3 AM',
  4: '4 AM',
  5: '5 AM',
  6: '6 AM',
  7: '7 AM',
  8: '8 AM',
  9: '9 AM',
  10: '10 AM',
  11: '11 AM',
  12: '12 PM',
  13: '1 PM',
  14: '2 PM',
  15: '3 PM',
  16: '4 PM',
  17: '5 PM',
  18: '6 PM',
  19: '7 PM',
  20: '8 PM',
  21: '9 PM',
  22: '10 PM',
  23: '11 PM',
  0: '12 AM'
};

export interface AvailableTimezone {
  name: string;
  value: string;
}

export interface Timezone {
  offset: number;
}

export const timezones: Timezone[] = [
  {
    offset: 0
  },
  {
    offset: 1
  },
  {
    offset: 2
  },
  {
    offset: 3
  },
  {
    offset: 3.5
  },
  {
    offset: 4
  },
  {
    offset: 5
  },
  {
    offset: 5.5
  },
  {
    offset: 6
  },
  {
    offset: 7
  },
  {
    offset: 8
  },
  {
    offset: 9
  },
  {
    offset: 9.5
  },
  {
    offset: 10
  },
  {
    offset: 11
  },
  {
    offset: 12
  },
  {
    offset: -11
  },
  {
    offset: -10
  },
  {
    offset: -9
  },
  {
    offset: -8
  },
  {
    offset: -7
  },
  {
    offset: -6
  },
  {
    offset: -5
  },
  {
    offset: -4
  },
  {
    offset: -3.5
  },
  {
    offset: -3
  },
  {
    offset: -1
  }
];

@Module({
  dynamic: true,
  store: store,
  name: 'Groups',
  namespaced: true
})
class GroupsStoreModule extends VuexModule {
  public dayGroups: DayGroups = {};

  public availableTimezones: AvailableTimezone[] = [];
  public availableTimezonesLoaded: boolean = false;

  public weekGroups: WeekGroups = {};
  public groupsMembersSchedule: GroupMembersSchedule = {};

  public selectedDate = moment();
  public selectedWeek: moment.Moment[] = [];
  public selectedFilterTimezone: string[] = [];

  public selectedTimezone: Timezone | undefined = timezones.find(
    x => moment().utcOffset() / 60 === x.offset
  );

  @Mutation
  setAvailableTimezones(data: AvailableTimezone[]) {
    this.availableTimezones = data;
    this.selectedFilterTimezone = data.map(x => x.value);
  }

  @Mutation
  setAvailableTimezonesLoaded() {
    this.availableTimezonesLoaded = true;
  }

  @Mutation
  setTimezone(timezone: Timezone) {
    this.selectedTimezone = timezone;
  }

  @Mutation
  setSelectedFilterTimezone(str: string[]) {
    this.selectedFilterTimezone = str;
  }

  @Mutation
  setDate(date: moment.Moment) {
    this.selectedDate = date;
  }

  @Mutation
  setWeek(week: moment.Moment[]) {
    this.selectedWeek = week;
  }

  @Mutation
  setDayGroups(data: {
    date: string;
    values: { hour: number; total: number }[];
  }) {
    const dayEvents: any = {};
    dayEvents[data.date] = {};
    data.values
      .sort(
        (
          a: { hour: number; total: number },
          b: { hour: number; total: number }
        ) => {
          if (a.hour > b.hour) return 1;
          else return -1;
        }
      )
      .forEach((x: { hour: number; total: number }) => {
        dayEvents[data.date][x.hour] = {
          hour: x.hour,
          total: x.total,
          groups: []
        };
      });
    this.dayGroups = dayEvents;
  }

  @Mutation
  setDayHourGroups(data: { groups: DayGroup[]; date: string; hour: number }) {
    this.dayGroups[data.date][data.hour].groups = data.groups;
  }

  @Mutation
  setWeekGroups(data: WeekGroupModel[]) {
    let dataOb: WeekGroups = {} as WeekGroups;
    data
      .sort((a, b) => {
        if (a.day < b.day) return -1;
        else return 1;
      })
      .forEach(bundle => {
        dataOb[bundle.day] = {};
        bundle.groups.forEach(group => {
          if (dataOb[bundle.day][group.hour])
            dataOb[bundle.day][group.hour].entities.push(group);
          else
            dataOb[bundle.day][group.hour] = {
              entities: [group],
              entityP: 'Groups',
              entityS: 'Group'
            };
        });
      });
    this.weekGroups = dataOb;
  }

  @Mutation
  setGroupMembersSchedule(data: any) {
    let dataOb: GroupMembersSchedule = {} as GroupMembersSchedule;
    dataOb[data.groupName] = {};
    Object.keys(data.groupBusySlots).forEach((timeslot: any) => {
      data.groupBusySlots[timeslot].members.forEach((member: any) => {
        if (dataOb[data.groupName][timeslot])
          dataOb[data.groupName][timeslot].entities.push({
            display_name: member.name
          } as WeekGroup);
        else
          dataOb[data.groupName][timeslot] = {
            entities: [
              {
                display_name: member.name
              } as WeekGroup
            ],
            entityP: 'Members',
            entityS: 'Member'
          };
      });
    });
    this.groupsMembersSchedule = dataOb;
  }

  @Action({ rawError: true })
  getWeekSchedule(data: { startDate: string; endDate: string }) {
    return axios
      .get(
        `/jwc/group/week?startDate=${data.startDate}&endDate=${data.endDate}&timezone=${this.selectedTimezone?.offset}&filterTimezone=${this.selectedFilterTimezone}`
      )
      .then(response => {
        if (response.data) {
          this.setWeekGroups(response.data);
        }
      });
  }

  @Action({ rawError: true })
  getDaySchedule() {
    const dateFreeze = this.selectedDate.format('YYYY-MM-DD');
    return axios
      .get(
        `/jwc/group/day/${dateFreeze}?timezone=${this.selectedTimezone?.offset}&filterTimezone=${this.selectedFilterTimezone}`
      )
      .then(response => {
        if (response.data) {
          this.setDayGroups({ date: dateFreeze, values: response.data });
        }
      });
  }

  @Action({ rawError: true })
  getGroupMembersSchedule(name: string) {
    return axios
      .get(
        `/jwc/group/slots?name=${encodeURIComponent(name)}&timezone=${
          this.selectedTimezone?.offset
        }`
      )
      .then(response => {
        if (response.data) {
          this.setGroupMembersSchedule(response.data);
        }
      });
  }

  @Action({ rawError: true })
  getDayHourGroups(hour: number) {
    const dateFreeze = this.selectedDate.format('YYYY-MM-DD');
    return axios
      .get(
        `/jwc/group/day/${dateFreeze}?hour=${hour}&timezone=${this.selectedTimezone?.offset}`
      )
      .then(response => {
        if (response.data) {
          this.setDayHourGroups({
            groups: response.data,
            date: dateFreeze,
            hour
          });
        }
      });
  }

  @Action({ rawError: true })
  getAvailableTimezones(t: any) {
    return axios
      .get(`/jwc/meta/available/timezone-filters`)
      .then(response => {
        if (response.data) {
          this.setAvailableTimezones(response.data);
        }
      })
      .finally(() => {
        this.setAvailableTimezonesLoaded();
        t.$root.$emit('available-timezones-loaded');
      });
  }
}
export const GroupsStore = getModule(GroupsStoreModule);
