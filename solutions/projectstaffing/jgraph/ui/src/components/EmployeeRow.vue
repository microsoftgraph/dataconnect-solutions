<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <div class="user-profile-wrapper">
    <b-avatar
      button
      @click="view"
      :src="
        source.profilePicture
          ? 'data:image/jpeg;base64,' + source.profilePicture
          : ''
      "
      :style="{
        backgroundColor: getRandomColor(
          source.name.charCodeAt(source.name.length - 1)
        )
      }"
      class="mr-3"
      :text="initials"
    >
    </b-avatar>
    <transition name="bounce">
      <span v-if="source.includedInCurrentTeam" class="selected-icon-check">
        <b-icon-check2></b-icon-check2>
      </span>
    </transition>
    <b-row class="user-info" no-gutters>
      <b-row no-gutters>
        <span>
          <span class="name mr-1 clickable" @click="view">{{
            source.name || source.mail
          }}</span>
          <span>
            <b-icon-dash v-if="source.role"></b-icon-dash>
            <span v-if="source.role">
              {{ source.role }}
            </span>
          </span>
          <span class="text-secondary">
            <span class="location" v-if="source.location">
              <b-icon-dash></b-icon-dash>
              {{ source.location }}
            </span>
            <span
              v-if="source.availableSince"
              class="text-success availability"
            >
              <b-icon-dash></b-icon-dash> Available since
              {{ source.availableSince | moment('MMMM DD, YYYY') }}</span
            >
            <span v-else class="text-success availability">
              <b-icon-dash class="mr-1"></b-icon-dash>Available Now
            </span>
          </span>
        </span>
      </b-row>
      <!-- relevant skills -->
      <b-row
        no-gutters
        class="skills-row"
        v-if="source.relevantSkills && source.relevantSkills.length !== 0"
      >
        <div
          :id="`${type}_relevantSkills_${this.source.mail}`"
          class="skills-list text-secondary"
          :class="{
            'text-ellipsis': source.showMore,
            'd-expanded': !source.showMore
          }"
          v-html="source.relevantSkillsHTML"
        ></div>
      </b-row>
      <!-- domain map skills -->
      <b-row
        :key="`${key}_${index}`"
        v-for="(domain, key) in source.domainToSkillMap"
        no-gutters
        class="skills-row"
      >
        <div
          :id="`${type}_${key}Skills_${source.mail}`"
          class="skills-list text-secondary"
          :class="{
            'text-ellipsis': source.showMore,
            'd-expanded': !source.showMore
          }"
          v-html="source.domainToSkillMapHTML[key]"
        ></div>
      </b-row>

      <!-- inffered skills -->
      <b-row
        no-gutters
        class="skills-row"
        v-if="source.inferredSkills && source.inferredSkills.length !== 0"
      >
        <div
          :id="`${type}_infferedSkills_${this.source.mail}`"
          class="skills-list text-secondary"
          :class="{
            'text-ellipsis': source.showMore,
            'd-expanded': !source.showMore
          }"
        >
          <span class="mr-1 text-black text-r">Inferred Skills:</span>
          <span
            v-for="(skill, index) in source.inferredSkills"
            :key="`${source.name}_inff_skill_${skill.value}_${index}`"
            class="skill"
            :class="{
              'highlighted-skill': skill.highlighted
            }"
            >{{ skill.value }}</span
          >
        </div>
      </b-row>
      <b-row
        no-gutters
        class="skills-row"
        v-if="source.inferredRoles && source.inferredRoles.length !== 0"
      >
        <div
          :id="`${type}_infferedRoles_${this.source.mail}`"
          class="skills-list text-secondary"
          :class="{
            'text-ellipsis': source.showMore,
            'd-expanded': !source.showMore
          }"
        >
          <span class="mr-1 text-black text-r">Inferred Roles:</span
          >{{ source.inferredRoles.join(', ') }}
        </div>
      </b-row>
      <b-link
        class="show-more"
        v-if="source.isSkillEllipsis"
        @click="dispatchShowMore"
        >{{ source.showMore ? 'Show more' : 'Show less' }}
      </b-link>
      <b-row no-gutters class="mobile-controls border-bottom">
        <div class="control" @click="view">
          <b-icon-eye></b-icon-eye>
          View Profile
        </div>

        <div
          v-if="!source.includedInCurrentTeam"
          :class="{ 'disabled-control': !$store.getters.isTeamCreated }"
          class="control"
          @click="add"
        >
          <b-icon-plus></b-icon-plus>
          Add to Team
        </div>
        <div v-else class="control" @click="remove">
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
        v-if="!source.includedInCurrentTeam"
        :class="{ 'disabled-control': !$store.getters.isTeamCreated }"
        class="control"
        v-b-tooltip.hover
        :title="
          $store.getters.isTeamCreated
            ? 'Add to Team'
            : 'Please create a team before adding members to it'
        "
        @click="add"
      >
        <b-icon-plus></b-icon-plus>
      </div>
      <div
        v-else
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
import { Component, Vue, Prop, Emit } from 'vue-property-decorator';
// eslint-disable-next-line no-unused-vars
import { Employee } from '@/types/Employee';
import { getRandomColor } from '@/utils/helpers';
import mixins from '@/mixins';

@Component({
  components: {},
  methods: {
    getRandomColor
  },
  mixins: [mixins]
})
export default class EmployeeRow extends Vue {
  localeTimeZone: string;
  loaded = false;
  showSummary = true;
  isSkillEllipsis = false;
  maxWidth = 0;

  @Prop({
    default: 0
  })
  index: Number;

  @Prop({
    default: () => {
      return {};
    }
  })
  source: Employee;

  @Prop({
    default: false
  })
  adding!: boolean;

  @Prop({
    default: ''
  })
  type!: string;

  dispatchShowMore() {
    mixins.methods.dispatch(
      'keep-state',
      'checkBoxValueChange',
      this,
      this.source.id,
      !this.source.showMore
    );
    this.source.showMore = !this.source.showMore;
  }

  setSkillEllipsis() {
    if (this.loaded) {
      // Verify ellipsis for relevant skills
      const primary = document.getElementById(
        `${this.type}_relevantSkills_${this.source.mail}`
      );
      let primaryOverflow = false;
      if (primary) {
        primaryOverflow = primary?.offsetWidth < primary?.scrollWidth;
        this.maxWidth = primary?.scrollWidth;
      }

      // Verify ellipsis for inferred skills
      const secondary = document.getElementById(
        `${this.type}_infferedSkills_${this.source.mail}`
      );
      let secondaryOverflow = false;
      if (secondary) {
        secondaryOverflow = secondary!.offsetWidth < secondary!.scrollWidth;
        this.maxWidth = Math.max(this.maxWidth, secondary!.scrollWidth);
      }

      // Verify ellipsis for inferred roles
      const roles = document.getElementById(
        `${this.type}_infferedRoles_${this.source.mail}`
      );
      let rolesOverflow = false;
      if (roles) {
        rolesOverflow = roles!.offsetWidth < roles!.scrollWidth;
        this.maxWidth = Math.max(this.maxWidth, roles!.scrollWidth);
      }

      // Verify ellipsis for all domains skills
      let domainsToSkillsMapOverflow = false;
      for (const domain of Object.keys(this.source.domainToSkillMap)) {
        const domainsToSkillsMap = document.getElementById(
          `${this.type}_${domain}Skills_${this.source.mail}`
        );
        if (domainsToSkillsMap) {
          domainsToSkillsMapOverflow =
            domainsToSkillsMap!.offsetWidth < domainsToSkillsMap!.scrollWidth ||
            domainsToSkillsMapOverflow;
          this.maxWidth = Math.max(
            this.maxWidth,
            domainsToSkillsMap!.scrollWidth
          );
        }
      }
      this.source.isSkillEllipsis =
        this.source.isSkillEllipsis ||
        primaryOverflow ||
        secondaryOverflow ||
        domainsToSkillsMapOverflow ||
        rolesOverflow;
      if (this.source.isSkillEllipsis) return;
    }
    this.source.isSkillEllipsis = false;
  }

  view() {
    this.$store.commit(
      'SET_SELECTED_EMPLOYEE_SKILLS',
      this.source.relevantSkills!.map(x => x.value)
    );
    this.$router.push(`employees/${this.source.id}`);
  }

  @Emit('add')
  async add() {
    await this.$store.dispatch('addEmployeeToTeam', this.source);
  }

  async remove() {
    await this.$store.dispatch('removeEmployeeFromTeam', {
      id: this.$store.getters.getTeamMemberIdForEmployeeId(this.source.id),
      name: this.source.name
    });
  }

  mounted() {
    // In case left or right panel open then reset skill ellipsis
    this.$root.$on('show-team-panel', () => {
      setTimeout(() => {
        this.resizeCallback();
      }, 500);
    });
    this.$root.$on('show-filters-panel', () => {
      setTimeout(() => {
        this.resizeCallback();
      }, 500);
    });
    // resize callback for setskillellipsis
    window.addEventListener('resize', this.resizeCallback);
    this.loaded = true;
    setTimeout(() => {
      this.setSkillEllipsis();
    }, 100);
  }

  resizeCallback() {
    this.source.isSkillEllipsis = false;
    this.source.showMore = true;
    this.setSkillEllipsis();
  }

  get initials() {
    let match = this.source.name?.match(/\b\w/g) || [];
    let initials = ((match.shift() || '') + (match.pop() || '')).toUpperCase();
    return initials;
  }

  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCallback);
  }
}
</script>

<style lang="scss">
.bounce-enter-active {
  animation: bounce-in 0.5s;
}
.bounce-leave-active {
  animation: bounce-in 0.5s reverse;
}
@keyframes bounce-in {
  0% {
    transform: scale(0);
  }
  50% {
    transform: scale(1.3);
  }
  100% {
    transform: scale(1);
  }
}

.user-profile-wrapper {
  padding: 10px 0px;
  margin: 2px 0px;
  width: 100%;
  min-height: 88px;
  display: flex;
  word-break: break-word;
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
    min-width: 52px;
    min-height: 52px;
    margin-top: 5px;
  }
  .selected-icon-check {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 23px;
    height: 23px;
    border: 2px solid white;
    position: absolute;
    left: 36px;
    top: 14px;
    background-color: $success;
    border-radius: 50px;
    svg {
      width: 10px;
      height: 10px;
      fill: white;
      stroke: white;
      margin-left: -1px;
    }
  }
  .user-info {
    position: relative;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    justify-content: center;
    flex: 1;
    .row {
      display: flex;
      flex-direction: row;
      align-items: center;
      .name {
        font-size: 16px;
        font-weight: 600;
      }
    }
    .skills-row {
      width: 100%;
      .skills-list {
        display: inline-block;
        &.d-expanded {
          display: inline-block;
        }
      }
    }
  }
  .show-more {
    color: #007bff;
    &:hover {
      color: #0056b3;
      text-decoration: underline;
    }
  }
  .controls {
    border-radius: 4px;
    top: calc(50% - 17.5px);
    z-index: 1;
    border-collapse: separate;
    table-layout: fixed;
    margin-left: auto;
    visibility: hidden;
    position: absolute;
    right: 24px;
    background-color: white;
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
      cursor: pointer;
      align-items: center;
      justify-content: center;
      text-align: center;

      svg {
        width: 20px;
        height: 20px;
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
  .disabled-control {
    cursor: not-allowed;
    color: #b3b3b3;
    &:hover {
      background-color: white !important;
    }
  }
}

@media screen and (max-width: 1024px) {
  .list-item {
    &:last-of-type {
      .user-profile-wrapper {
        margin-bottom: 64px;
      }
    }
    .user-profile-wrapper {
      padding: 12px 0px 0px;
      margin: 0px;
      .user-info {
        span {
          line-height: 20px;
        }
      }
      .skills-row {
        margin-top: 12px;
      }
      .show-more {
        line-height: 20px;
      }
      .mobile-controls {
        margin-top: 12px;
        padding-bottom: 12px;
      }
      .location,
      .availability {
        display: block;
        svg {
          display: none;
        }
      }
      .controls {
        display: none;
      }
      .mobile-controls {
        display: flex !important;
      }
    }
  }
}
</style>
