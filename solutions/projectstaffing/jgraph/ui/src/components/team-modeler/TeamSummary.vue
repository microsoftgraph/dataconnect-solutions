<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <div class="team-summary-wrapper">
    <div v-if="team.name" class="team-content">
      <b-row no-gutters class="custom-header ">
        <b-row no-gutters class="w-100  title-wrapper">
          <h5>{{ team.name }}</h5>
          <span
            class="btn-icon"
            title="Edit team"
            v-b-tooltip.hover.top
            v-b-tooltip.hover="{ customClass: 'custom-tooltip' }"
          >
            <b-icon-pencil @click="viewTeam"></b-icon-pencil>
          </span>
        </b-row>

        <div
          class="text-secondary border-top w-100 description-wrapper"
          v-html="team.description"
        ></div>
      </b-row>
      <Team class="team-summary-members-wrapper" />
      <div class="footer">
        <b-button
          :disabled="teamMembers.length === 0"
          @click="viewDownload"
          class="default-size"
          variant="outline-secondary"
        >
          <b-icon-download class="mr-2"></b-icon-download>
          Export</b-button
        >
      </div>
    </div>
    <div v-else>
      <b-row no-gutters class="not-found">
        <img src="@/assets/icons/not-found.svg" />
        <b-button
          v-b-tooltip.hover
          title="Create a new team roster"
          variant="primary"
          class="default-size mt-3"
          @click="viewTeam"
          v-b-tooltip.ds500
        >
          Create team</b-button
        >
        <span class="mt-3 text-secondary text-center"
          >Please create a team to add relevant employees to</span
        >
      </b-row>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Team from '@/components/Team.vue';

@Component({
  components: { Team }
})
export default class TeamSummary extends Vue {
  get team() {
    return this.$store.state.team.team;
  }

  get teamMembers() {
    return this.$store.state.team.teamMembers;
  }

  mounted() {
    this.getTeamInfo();
  }

  viewDownload() {
    this.$store.commit('SHOW_DOWNLOAD_MODAL', {
      type: 'team',
      modal: this.$bvModal
    });
  }

  async getTeamInfo() {
    await this.$store.dispatch('getTeamInfo');
  }

  async viewTeam() {
    await this.getTeamInfo();
    this.$bvModal.show('team-modal');
  }
}
</script>

<style lang="scss">
.team-summary-wrapper {
  height: 100%;
  .not-found {
    padding: 24px 24px 0px 0px;
  }
  .team-content {
    display: flex;
    height: 100%;
    flex-direction: column;
    .custom-header {
      position: relative;
      .title-wrapper {
        display: -webkit-box !important;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        margin: 18px var(--main-padding) 18px 0px;
        h5 {
          font-weight: 600;
          width: calc(100% - 20px);
        }
      }
      .description-wrapper {
        overflow: hidden;
        padding-top: 16px;
        margin: 0px var(--main-padding) 0px 0px;
        > * {
          margin: 0px;
          position: relative;
          display: -webkit-box !important;
          word-break: break-all;
          -webkit-line-clamp: 1;
          -webkit-box-orient: vertical;
          overflow: hidden;
          &:nth-of-type(1n + 3) {
            display: none !important;
          }
          &:nth-of-type(2) {
            visibility: hidden;
            &::after {
              visibility: visible;
              display: block;
              top: 0px;
              position: absolute;
              content: '...';
            }
          }
        }
      }
      .row {
        max-height: 100px;
        word-break: break-word;
      }
      .btn-icon {
        position: absolute;
        right: 24px;
        top: 12px;
      }
    }
    .custom-tabs {
      height: 100%;
      overflow-y: auto;
    }
    .footer {
      margin: 0 auto;
      display: flex;
      align-items: center;
      margin-top: auto;
      padding: 24px;
    }
  }
}
@media screen and (max-width: 1024px) {
  .team-summary-wrapper {
    .team-content {
      .custom-header {
        background-color: #fcfbfa;
        border-bottom: 1px solid $main-border-color;
        .title-wrapper {
          margin: var(--main-padding);
          .btn-icon {
            right: 12px;
            top: 6px;
          }
        }
        .description-wrapper {
          border-top: none !important;
          padding-top: 0px !important;
          margin: 0px var(--main-padding) 16px var(--main-padding);
        }
      }
      .footer {
        padding-bottom: 76px !important;
      }
    }
  }
}
</style>
