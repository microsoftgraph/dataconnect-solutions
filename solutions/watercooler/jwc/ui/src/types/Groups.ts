export interface GroupMember {
  email: string;
  image: string;
  wcStatus: string;
  attendanceStatus: string;
  name: string;
}

export interface DayGroup {
  day: string;
  attendanceRate: number;
  name: string;
  time: string;
  displayName: string;
  members: GroupMember[];
}

export interface GroupHourEvent {
  [hour: number]: {
    groups: DayGroup[];
    hour: number;
    total: number;
  };
}

export interface DayGroups {
  [key: string]: GroupHourEvent;
}

export interface WeekGroup {
  display_name: string;
  group_name: string;
  hour: number;
  hour_time_slot: string;
  members: {
    mail: string;
    name: string;
  }[];
  tzinfo: string;
}

export interface WeekGroups {
  [date: string]: {
    [hour: string]: {
      entities: WeekGroup[];
      entityS: string;
      entityP: string;
    };
  };
}

export interface GroupMembersSchedule {
  [groupName: string]: {
    [hour: string]: {
      entities: WeekGroup[];
      entityS: string;
      entityP: string;
    };
  };
}

export interface WeekGroupModel {
  day: string;
  groups: WeekGroup[];
}
