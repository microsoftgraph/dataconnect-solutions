<template>
  <b-modal
    :id="id"
    dialog-class="default-modal"
    centered
    scrollable
    @change="init"
  >
    <template v-slot:modal-header="{ close }">
      <span class="title"
        >Group Members
        <span class="text-secondary">
          <b-icon-dash class="mr-1"></b-icon-dash>{{ members.length }}
          {{ members.length === 1 ? ' Member' : ' Members' }}</span
        ></span
      >
      <b-icon-x @click="close"> </b-icon-x>
    </template>
    <div class="content">
      <div class="summary-wrapper border-right">
        <div
          v-for="(member, index) in members"
          :key="member.id"
          :class="{ selected: index === selectedIndex }"
          class="d-flex"
          @click="setIndex(index, member.mail)"
        >
          <b-avatar
            :src="
              `data:image/jpeg;base64, ${
                member.image
                  ? member.image
                  : employees[member.email] && employees[member.email].image
                  ? employees[member.email].image
                  : ''
              }`
            "
            badge-top
            :class="member.wcStatus"
          >
            <template v-if="member.wcStatus" #badge
              ><b-icon :icon="icons[member.wcStatus]"></b-icon
            ></template>
          </b-avatar>

          <span class="name">
            <span class="text"> {{ member.name }}</span>
            <span class="att" v-if="member.attendanceStatus">
              {{
                member.attendanceStatus === 'attended'
                  ? 'Attended'
                  : 'Not attended'
              }}
            </span>
          </span>
        </div>
      </div>
      <div class="content-wrapper" v-if="employeeInfo !== undefined">
        <EmployeeProfile :employee="employeeInfo" />
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
// eslint-disable-next-line no-unused-vars
import { EmployeeStore } from '@/store/modules/employee.store';
// eslint-disable-next-line no-unused-vars
import { GroupMember } from '@/types/Groups';
// eslint-disable-next-line no-unused-vars
import { EmployeeInfo } from '@/types/Employee';
import EmployeeProfile from '@/components/EmployeeProfile.vue';

@Component({
  components: { EmployeeProfile }
})
export default class GroupModal extends Vue {
  id = 'group-members-modal';
  selectedIndex = 0;
  icons = {
    accepted: 'check',
    rejected: 'x',
    unknown: 'question'
  };

  @Prop({
    default: () => {
      return [];
    }
  })
  members!: GroupMember[];

  get selectedMember(): GroupMember {
    return this.members[this.selectedIndex];
  }

  get employees() {
    return EmployeeStore.employees;
  }

  get employeeInfo(): EmployeeInfo | undefined {
    return this.selectedMember
      ? EmployeeStore.employees[this.selectedMember.email]
      : undefined;
  }

  getEmployeeProfile(email: string, silentLoad = false) {
    EmployeeStore.setEmployeeEmptyProfile(email);
    EmployeeStore.getEmployeeProfile({ email, silentLoad });
  }

  setIndex(index: number) {
    this.selectedIndex = index;
    if (!EmployeeStore.employees[this.selectedMember.email]) {
      this.getEmployeeProfile(this.selectedMember.email);
    }
  }

  mounted() {}

  init() {
    this.setIndex(0);
    this.members.forEach(emp => {
      if (!EmployeeStore.employees[emp.email]) {
        this.getEmployeeProfile(emp.email, true);
      }
    });
  }

  close() {
    this.$bvModal.hide(this.id);
  }

  open() {
    this.$bvModal.show(this.id);
  }
}
</script>

<style lang="scss">
#group-members-modal {
  .modal-dialog {
    max-width: 800px !important;
    .modal-content {
      min-height: 500px !important;
      .modal-body {
        padding: 0px;
        .content {
          height: 100%;
          display: flex;
          .summary-wrapper {
            background-color: white;
            width: 300px;
            overflow-y: auto;
            padding: 12px var(--main-padding);
            div {
              border: 1px solid transparent;
              cursor: pointer;
              padding: 8px;
              .b-avatar {
                margin-right: 5px;
              }
              &.selected {
                border: 1px solid $primary;
                border-radius: 8px;
                background-color: rgba($primary, 0.1);
              }
              .name {
                display: flex;
                justify-content: center;
                flex-direction: column;
                .text {
                  font-weight: 500;
                }
                .att {
                  font-size: 12px;
                  color: $secondary;
                }
              }
            }
          }
        }
      }
      .content-wrapper {
        overflow-y: auto;
        flex: 1;
        padding: var(--main-padding);
      }
      .b-avatar {
        &.accepted {
          .b-avatar-badge {
            stroke: white;
            background-color: #4ca761;
            border: 1px solid white;
          }
        }
        &.rejected {
          .b-avatar-badge {
            stroke: white;
            background-color: #d85d5d;
            border: 1px solid white;
          }
        }
        &.unknown {
          .b-avatar-badge {
            stroke: white;
            border: 1px solid white;
            background-color: #b3b3b3;
          }
        }
      }
    }
  }
}
</style>
