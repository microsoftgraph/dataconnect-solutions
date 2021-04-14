<template>
  <div class="team-member-wrapper">
    <b-avatar
      button
      @click="view"
      :src="'data:image/jpeg;base64,' + teamMember.employeeProfilePicture"
      :style="{
        backgroundColor: getRandomColor(
          teamMember.name.charCodeAt(teamMember.name.length - 1)
        )
      }"
      class="mr-3"
      :text="initials"
    ></b-avatar>
    <b-row class="user-info" no-gutters>
      <b-row no-gutters>
        <span class="name clickable" @click="view">{{ teamMember.name }}</span>
        <b-icon-dash v-if="teamMember.employeeRole"></b-icon-dash>
        <span class="text-secondary">{{ teamMember.employeeRole }}</span>
      </b-row>
      <b-row no-gutters class="text-success mobile-av">
        <span v-if="teamMember.availableSince" class="text-success">
          Available since
          {{ teamMember.availableSince | moment('MMMM DD, YYYY') }}</span
        >
        <span v-else class="text-success">
          Available Now
        </span>
      </b-row>
      <b-row
        no-gutters
        class="skills"
        :id="`skills_${teamMember.email}`"
        :class="{ 'text-ellipsis': showSummary }"
      >
        <span class=" text-secondary">
          <span
            v-for="(skill, index) in teamMember.skills"
            :key="`${teamMember.name}_skill_${skill}_${index}`"
          >
            {{ skill }}{{ index + 1 !== teamMember.skills.length ? ',' : '' }}
          </span>
        </span>
      </b-row>
      <b-row no-gutters class="text-success desktop-av">
        <span v-if="teamMember.availableSince" class="text-success">
          Available since
          {{ teamMember.availableSince | moment('MMMM DD, YYYY') }}</span
        >
        <span v-else class="text-success">
          Available Now
        </span>
      </b-row>
      <b-link
        class="show-more"
        v-if="isSkillEllipsis"
        @click="showSummary = !showSummary"
        >{{ showSummary ? 'Show more' : 'Show less' }}
      </b-link>
      <b-row no-gutters class="mobile-controls border-bottom">
        <div class="control" @click="view">
          <b-icon-eye></b-icon-eye>
          View Profile
        </div>
        <div class="control" @click="remove">
          <b-icon-x></b-icon-x>
          Remove from Team
        </div>
      </b-row>
    </b-row>

    <div class="controls">
      <div
        class="control"
        title="View profile"
        v-b-tooltip.hover.top
        @click="view"
      >
        <b-icon-eye></b-icon-eye>
      </div>

      <div
        class="control"
        v-b-tooltip.hover
        title="Remove from Team"
        @click="remove"
      >
        <b-icon-x></b-icon-x>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch } from 'vue-property-decorator';
// eslint-disable-next-line no-unused-vars
import { TeamMember } from '@/types/Employee';
import { getRandomColor } from '@/utils/helpers';

@Component({
  components: {},
  methods: { getRandomColor }
})
export default class TeamMemberRow extends Vue {
  isSkillEllipsis = false;
  showSummary = true;

  @Prop({
    default: () => {
      return {};
    }
  })
  teamMember!: TeamMember;

  @Prop({
    default: false
  })
  adding!: boolean;

  @Watch('teamMember', { deep: true })
  onPropertyChange() {
    this.showSummary = true;
  }

  @Watch('selectedIndex')
  onSelectedIndexChange(newval: number) {
    if (newval === 1) {
      this.setSkillEllipsis();
    }
  }

  get selectedIndex() {
    return this.$store.state.shared.mobileSelectedIndex;
  }

  setSkillEllipsis() {
    setTimeout(() => {
      const skills = document.getElementById(`skills_${this.teamMember.email}`);
      if (skills) {
        this.isSkillEllipsis =
          skills?.offsetWidth < skills?.scrollWidth ||
          skills?.offsetHeight < skills?.scrollHeight;
      }
      if (this.isSkillEllipsis) return;
      this.isSkillEllipsis = false;
    }, 0);
  }

  view() {
    this.$store.commit('SET_SELECTED_EMPLOYEE_SKILLS', this.teamMember.skills);
    this.$router.push(`employees/${this.teamMember.employeeId}`);
  }

  add() {
    this.$store.dispatch('addEmployeeToTeam', this.teamMember);
  }

  remove() {
    this.$root.$emit('removed-from-team');
    this.$store.dispatch('removeEmployeeFromTeam', {
      id: this.teamMember.teamMemberId,
      name: this.teamMember.name
    });
  }

  mounted() {
    this.setSkillEllipsis();
    this.$root.$on('show-team-panel', () => {
      this.setSkillEllipsis();
    });
    window.addEventListener('resize', this.setSkillEllipsis);
  }

  get initials() {
    let match = this.teamMember.name?.match(/\b\w/g) || [];
    let initials = ((match.shift() || '') + (match.pop() || '')).toUpperCase();
    return initials;
  }

  beforeDestroy() {}
}
</script>

<style lang="scss">
.team-member-wrapper {
  padding: 10px var(--main-padding) 10px 0px;
  margin: 2px 0px;
  display: flex;
  overflow: hidden;
  word-break: break-word;
  flex: 1;
  position: relative;
  border-top: 1px solid transparent;
  border-bottom: 1px solid transparent;
  &.adding {
    &::before {
      content: '';
      display: block;
      height: calc(100% - 0px);
      width: 4px;
      position: absolute;
      background-color: $primary;
      left: 0;
      border-top-right-radius: 4px;
      border-bottom-right-radius: 4px;
    }
  }
  .b-avatar {
    min-width: 40px;
    min-height: 40px;
    margin-top: 5px;
  }
  .user-info {
    width: calc(100% - 3.5em);
    display: flex;
    flex-direction: column;
    .row {
      width: 100%;
      align-items: center;
      &.skills {
        display: inline-block;
      }
      .name {
        font-weight: 600;
        font-size: 14px;
      }
    }
    .mobile-av {
      display: none;
    }
  }
  .controls {
    border-radius: 4px;
    z-index: 1;
    top: calc(50% - 17.5px);
    border-collapse: separate;
    table-layout: fixed;
    margin-left: auto;
    visibility: hidden;
    position: absolute;
    right: 24px;
    background-color: #fcfbfa;
    display: flex;
    .control {
      text-align: center;
      line-height: 35px;
      width: 35px;
      display: table-cell;
      height: 35px;
      border: 1px solid $main-border-color;
      transition: background-color 0.2s ease-out;
      border-right: none;
      display: flex;
      align-items: center;
      justify-content: center;
      text-align: center;
      svg {
        width: 20px;
        height: 20px;
      }
      &.disabled-control {
        cursor: not-allowed;
        color: #b3b3b3;
        &:hover {
          background-color: white !important;
        }
      }
      &:first-child {
        border-top-left-radius: $main-border-radius;
        border-bottom-left-radius: $main-border-radius;
      }
      &:last-child {
        border-top-right-radius: $main-border-radius;
        border-bottom-right-radius: $main-border-radius;
        border-right: 1px solid $main-border-color;
      }
      &:hover {
        cursor: pointer;
        background-color: rgba(grey, 0.1);
      }
    }
  }
  &:hover {
    border-top: 1px solid $main-border-color;
    border-bottom: 1px solid $main-border-color;
    .controls {
      visibility: visible;
    }
  }
}

@media screen and (max-width: 1024px) {
  .team-member-wrapper {
    padding: 12px;
    margin: 0px;
    .user-info {
      span {
        line-height: 20px;
      }
    }
    .skills {
      margin-top: 12px;
    }
    .show-more {
      line-height: 20px;
    }
    .mobile-controls {
      margin-top: 12px;
      padding-bottom: 12px;
    }
    .b-avatar {
      width: 52px !important;
      height: 52px !important;
      min-width: 52px;
      min-height: 52px;
    }
    .controls {
      display: none;
    }
    .mobile-av {
      display: flex !important;
    }
    .desktop-av {
      display: none;
    }
    .mobile-controls {
      display: flex !important;
    }
  }
}
</style>
