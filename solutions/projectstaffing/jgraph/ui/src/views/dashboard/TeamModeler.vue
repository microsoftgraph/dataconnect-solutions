<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <div class="default-view-wrapper team-modeler-wrapper">
    <b-col
      :class="{ 'd-block': selectedIndex === 0 }"
      cols="12"
      class="p-0 main-search-wrapper"
    >
      <TalentPool />
    </b-col>
    <b-col
      :class="{ 'd-block': selectedIndex === 1 }"
      class=" border-left p-0 summary-wrapper"
    >
      <TeamSummary />
      <TeamModal />
    </b-col>
    <div class="d-none border-top tab-selector">
      <span :class="{ selected: selectedIndex === 0 }" @click="selectIndex(0)">
        <b-icon-search></b-icon-search>
      </span>
      <span :class="{ selected: selectedIndex === 1 }" @click="selectIndex(1)">
        <b-icon-people></b-icon-people>
      </span>
    </div>
    <DownloadModal />
    <router-view></router-view>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import TeamSummary from '@/components/team-modeler/TeamSummary.vue';
import TalentPool from '@/components/team-modeler/TalentPool.vue';
import DownloadModal from '@/components/modals/DownloadModal.vue';
import TeamModal from '@/components/modals/TeamModal.vue';

@Component({
  components: { TeamSummary, TalentPool, DownloadModal, TeamModal }
})
export default class TeamModeler extends Vue {
  mounted() {
    window.addEventListener('resize', () => {
      if (window.innerWidth > 1024) this.selectedIndex = 0;
    });
  }

  get selectedIndex() {
    return this.$store.state.shared.mobileSelectedIndex;
  }

  set selectedIndex(value) {
    this.$store.commit('SET_MOBILE_SELECTED_INDEX', value);
  }

  selectIndex(index: number) {
    this.selectedIndex = index;
    window.scrollTo(0, 0);
  }
}
</script>

<style lang="scss">
.team-modeler-wrapper {
  display: flex;
  position: relative;
  flex-direction: row;
  .custom-content-wrapper {
    max-width: calc(75% - 24px);
    &:nth-child(2) {
      background-color: white;
      box-shadow: 0px 1px 20px 0px #efefef;
      border-left: 1px solid #efefef;
    }
  }
  .summary-wrapper {
    display: none;
    height: 100%;
    right: 0;
    z-index: 1;
    background-color: #fcfbfa;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    position: absolute;
    max-width: 300px;
  }
}

@media screen and (max-width: 1024px) {
  .team-modeler-wrapper {
    display: block;
    .main-search-wrapper {
      display: none;
      height: 100%;
      max-width: 100%;
      .person-search-wrapper {
        background-color: #fcfbfa;
        margin-bottom: 8px;
        padding-bottom: 0px !important;
        margin-bottom: 0px !important;
        .search-inputs {
          flex-direction: column;

          > div {
            width: 100%;
            .search-button {
              width: 100%;
              margin-top: 12px;
            }
          }
        }
      }
    }
    .summary-wrapper {
      display: none;
      height: 100%;
      background-color: white;
      max-width: 100% !important;
      border-left: none !important;
      box-shadow: none;
      .team-summary-wrapper {
        height: calc(100% - 56px);
        .team-wrapper {
          overflow: visible;
        }
      }
    }

    .tab-selector {
      position: fixed;
      z-index: 1;
      bottom: 0px;
      background-color: white;
      width: 100%;
      height: 56px;
      display: flex !important;
      flex-direction: row;
      > span {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        &.selected {
          color: $primary;
        }
        svg {
          font-size: 22px;
        }
      }
    }
  }
}
</style>
