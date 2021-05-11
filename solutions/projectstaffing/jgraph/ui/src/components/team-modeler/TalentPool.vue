<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <div class="talent-pool-wrapper">
    <div class="mobile-header-wrapper border-bottom">
      <span class="title">Talent Pool</span>
    </div>
    <transition name="slide-left">
      <div id="leftWrapper" v-if="showLeftPanel" class="left-wrapper">
        <div class="content">
          <SearchFilters />
        </div>
      </div>
    </transition>
    <span class="left-toggler" :class="{ expanded: showLeftPanel }">
      <span
        id="leftBtnToggler"
        @click="toggleLeftPanel()"
        v-b-tooltip.hover
        :title="showLeftPanel ? 'Hide Filters' : 'Show Filters'"
        class="round-icon"
      >
        <b-icon
          ref="showLeftPanel"
          :icon="
            showLeftPanel
              ? 'chevron-left'
              : isFiltersSelected
              ? 'funnel-fill'
              : 'funnel'
          "
          :class="isFiltersSelected && !showLeftPanel ? 'selected' : ''"
          :animation="showLeftPanelAnimation"
        ></b-icon> </span
    ></span>
    <div class="main-wrapper">
      <div class="alert-wrapper" v-if="ingestionModeOngoing">
        <b-alert show variant="primary">
          <b-icon-info-circle-fill
            variant="primary"
            class="mr-2"
          ></b-icon-info-circle-fill
          >{{ constants.ingestion.talentPool.info }}</b-alert
        >
        <b-alert
          show
          class="mt-3"
          variant="danger"
          v-if="
            ingestionMode.modeSwitchPhase === 'error' ||
              ingestionMode.modeSwitchPaused
          "
        >
          <b-icon-x-circle-fill
            variant="danger"
            class="mr-2"
          ></b-icon-x-circle-fill>
          <span v-if="isAdmin">
            {{
              ingestionMode.modeSwitchPhase !== 'error'
                ? constants.ingestion.talentPool.pausedAdmin
                : constants.ingestion.talentPool.failedAdmin
            }}</span
          ><span v-else>{{
            ingestionMode.modeSwitchPhase !== 'error'
              ? constants.ingestion.talentPool.pausedNonAdmin
              : constants.ingestion.talentPool.failedNonAdmin
          }}</span></b-alert
        >
      </div>
      <EmployeeSearch />
      <div class="results">
        <SearchResults />
      </div>
    </div>
    <span class="right-toggler">
      <span
        id="rightBtnToggler"
        @click="toggleRightPanel()"
        v-b-tooltip.hover
        :title="showRightPanel ? 'Hide Team' : 'Show Team'"
        class="round-icon"
      >
        <b-icon
          :icon="showRightPanel ? 'chevron-right' : 'people'"
          :animation="showRightPanelAnimation"
        ></b-icon> </span
    ></span>
    <transition name="slide-right">
      <div v-if="showRightPanel" class="right-wrapper">
        <div class="content">
          <TeamSummary />
        </div>
      </div>
    </transition>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Team from '@/components/Team.vue';
import EmployeeSearch from '@/components/team-modeler/EmployeeSearch.vue';
import SearchResults from '@/components/team-modeler/SearchResults.vue';
import { constants } from '@/helpers/constants';
import SearchFilters from '@/components/team-modeler/SearchFilters.vue';
import TeamSummary from '@/components/team-modeler/TeamSummary.vue';
import moment from 'moment';

@Component({
  components: {
    Team,
    EmployeeSearch,
    SearchResults,
    SearchFilters,
    TeamSummary
  },
  data() {
    return {
      constants
    };
  }
})
export default class TalentPool extends Vue {
  showLeftPanel = false;
  showRightPanel = false;
  showLeftPanelAnimation = '';
  showRightPanelAnimation = '';

  mounted() {}

  get isAdmin() {
    return this.$store.state.auth.currentUser.isAdmin();
  }

  get ingestionMode() {
    return this.$store.state.ingestion.ingestionModeDetails;
  }

  get ingestionModeOngoing() {
    return (
      this.$store.state.ingestion.ingestionModeDetails.modeSwitchPhase !==
      'completed'
    );
  }

  get isFiltersSelected() {
    return (
      this.$store.getters.isFiltersSelected ||
      !moment().isSame(this.$store.state.search.date, 'day')
    );
  }

  toggleLeftPanel() {
    this.showLeftPanel = !this.showLeftPanel;
    this.$root.$emit('show-filters-panel');
    this.showLeftPanelAnimation = 'throb';
    this.$root.$emit('bv::hide::tooltip', 'leftBtnToggler');
    setTimeout(() => {
      this.showLeftPanelAnimation = '';
    }, 500);
  }

  toggleRightPanel() {
    this.showRightPanel = !this.showRightPanel;
    this.$root.$emit('show-team-panel');
    this.showRightPanelAnimation = 'throb';
    this.$root.$emit('bv::hide::tooltip', 'rightBtnToggler');
    setTimeout(() => {
      this.showRightPanelAnimation = '';
    }, 500);
  }
}
</script>

<style lang="scss">
.slide-right-enter-active,
.slide-right-leave-active {
  margin-right: 0px;
  transition: all 0.25s linear;
}

.slide-right-enter,
.slide-right-leave-to {
  margin-right: -426px;
}

.slide-left-enter-active,
.slide-left-leave-active {
  margin-left: 0px;
  transition: all 0.25s linear;
}

.slide-left-enter,
.slide-left-leave-to {
  margin-left: -256px;
}

.talent-pool-wrapper {
  height: 100%;
  display: flex;
  flex-direction: row;
  .right-wrapper {
    background-color: rgb(252, 251, 250);
    position: relative;
    // Actually is 450 because of padding from toggler
    flex: 0 0 426px;
    max-width: 426px;
    .content {
      height: 100%;
    }
  }
  .right-toggler {
    background-color: rgb(252, 251, 250);
    border-left: 1px solid $main-border-color;
    position: relative;
    padding: 12px;
    box-shadow: 0 2px 10px #0000001a;
    .round-icon {
      position: absolute;
      left: -18px;
      svg {
        width: 20px;
        height: 20px;
      }
    }
  }
  .left-toggler {
    border-right: 1px solid $main-border-color;
    position: relative;
    padding: 12px;
    &.expanded {
      padding: 12px 0px;
    }
    .round-icon {
      position: absolute;
      right: -18px;
      svg {
        width: 20px;
        height: 20px;
        &.selected {
          fill: $primary;
        }
      }
    }
  }
  .left-wrapper {
    display: flex;
    padding-left: calc(var(--main-padding) - 3px);
    overflow-y: hidden;
    flex: 0 0 280px;
    &.hide {
      flex: 0;
    }
    .content {
      display: flex;
      overflow: hidden;
      flex: 1;
    }
  }
  .main-wrapper {
    overflow: hidden;
    height: 100%;
    width: 100%;
    display: flex;
    flex-direction: column;
    .alert-wrapper {
      padding: var(--main-padding);
      padding-bottom: 0px;
    }
    .person-search-wrapper {
      padding: var(--main-padding);
      padding-bottom: 4px;
    }
    .results {
      flex-direction: column;
      min-height: 200px;
      overflow-x: auto;
      display: flex;
      padding-left: var(--main-padding);
    }
  }
}

@media screen and (max-width: 1250px) {
  .slide-right-enter,
  .slide-right-leave-to {
    margin-right: -250px;
  }

  .talent-pool-wrapper {
    .right-wrapper {
      flex: 0 0 250px;
      max-width: 250px;
    }
  }
}

@media screen and (max-width: 1024px) {
  .talent-pool-wrapper {
    flex-direction: column;
    .left-toggler,
    .right-toggler,
    .left-wrapper,
    .right-wrapper {
      display: none;
    }
    .talent-pool-wrapper {
      flex-direction: column;
    }
    .person-search-wrapper {
      max-width: 1024px !important;
      width: 100% !important;
    }
    .results {
      max-width: 100% !important;
      // padding: var(--main-padding) !important;
      padding: 0px !important;
      .alert-wrapper {
        margin-right: 0px;
      }
    }
  }
}
</style>
