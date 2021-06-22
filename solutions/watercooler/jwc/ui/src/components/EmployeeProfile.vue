<template>
  <div class="employee-profile">
    <b-row no-gutters>
      <b-avatar
        :src="'data:image/jpeg;base64,' + employee.image"
        class="mr-3"
        badge-top
        :class="employee.wcStatus"
      >
        <template v-if="employee.wcStatus" #badge
          ><b-icon :icon="icons[employee.wcStatus]"></b-icon></template
      ></b-avatar>
      <b-col align-self="center" no-gutters>
        <span class="title">{{ employee.displayName }}</span>
        <span
          v-if="selectedMember && selectedMember.attendanceStatus"
          class="d-flex mb-1 text-secondary"
        >
          {{
            selectedMember && selectedMember.attendanceStatus === 'attended'
              ? 'Attended'
              : 'Not attended'
          }}
        </span>
        <span class="d-flex">{{ employee.jobTitle }}</span>
        <div
          class="text-secondary mt-2"
          v-if="employee.state || employee.city || employee.country"
        >
          <b-icon-geo-alt class="mr-1"></b-icon-geo-alt>
          <span>
            {{ employee.city }} {{ employee.state }}
            {{
              `${employee.city || employee.state ? ',' : ''}${employee.country}`
            }}
          </span>
        </div>
        <div class="text-secondary" v-if="employee.mail">
          <a :href="`mailto:${employee.mail}`">
            <b-icon-envelope class="mr-1"></b-icon-envelope>
            <span>
              {{ employee.mail }}
            </span>
          </a>
        </div>
      </b-col>
    </b-row>
    <b-col class="p-0 mt-4" v-if="employee.aboutMe">
      <h6 class="mb-2 text-sb">About</h6>
      <div v-html="employee.aboutMe"></div>
    </b-col>
    <b-col class="p-0 mt-4" v-if="employee.skills">
      <h6 class="mb-2 text-sb">Skills</h6>
      <div>{{ employee.skills }}</div>
    </b-col>
    <b-col no-gutters class="p-0 mt-4 mb-4 text-secondary">
      <div v-if="employee.engagement">
        Currently part of the
        <span class="text-sb">{{ employee.engagement }}</span> team
      </div>
    </b-col>
  </div>
</template>

<script lang="ts">
// eslint-disable-next-line no-unused-vars
import { EmployeeInfo } from '@/types/Employee';
// eslint-disable-next-line no-unused-vars
import { GroupMember } from '@/types/Groups';
import { Component, Vue, Prop } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class EmployeePorfile extends Vue {
  @Prop({
    default: () => {
      return {};
    }
  })
  selectedMember!: GroupMember;

  @Prop({
    default: () => {
      return {};
    }
  })
  employee!: EmployeeInfo;

  mounted() {}
}
</script>

<style lang="scss">
.employee-profile {
  overflow-y: auto;
  flex: 1;
  .b-avatar {
    width: 108px;
    height: 108px;
    .b-avatar-badge {
      width: 30px;
      height: 30px;
      line-height: 28px;
      border: 2px solid white;
      svg {
        width: 16px;
        height: 16px;
      }
    }
  }
}
</style>
