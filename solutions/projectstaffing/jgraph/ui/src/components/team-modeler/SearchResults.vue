<template>
  <div class="persons-result-wrapper">
    <div class="text-secondary persons-count">
      <b-dropdown
        v-if="isSkillSelectedSearchType"
        class="custom-dropdown single-select desktop"
        left
        :text="`Sort By: ${sortOptions[selectedSort]}`"
        variant="outline-secondary"
      >
        <b-dropdown-item
          class="custom-row"
          v-for="(option, key) in sortOptions"
          :key="`sortOption_${key}`"
          @click="selectSort(key)"
        >
          {{ option }}
        </b-dropdown-item>
      </b-dropdown>

      <b-dropdown
        v-if="isSkillSelectedSearchType"
        class="custom-dropdown single-select mobile "
        left
        variant="outline-secondary"
      >
        <template #button-content>
          <b-icon-arrow-down></b-icon-arrow-down>
          <span class=""> {{ sortOptions[selectedSort] }}</span>
        </template>
        <b-dropdown-item
          class="custom-row"
          v-for="(option, key) in sortOptions"
          :key="`sortOption_${key}`"
          @click="selectSort(key)"
        >
          {{ option }}
        </b-dropdown-item>
      </b-dropdown>
      <span class="results-number">
        Displaying {{ employees.length }} results
        <span v-if="moreResultsAvailable"> . Scroll down to load more</span>
        <b-icon-dash class="dash-icon ml-1 mr-1"></b-icon-dash>
        <b-link
          :disabled="employees.length === 0"
          class="show-more"
          @click="viewDownload"
        >
          <span class="label">Export</span>
          <b-icon-download></b-icon-download>
        </b-link>
      </span>
    </div>
    <div
      id="searchResultsWrapper"
      class="results-scrollable-wrapper"
      no-gutters
    >
      <virtual-list
        ref="virtualSrollingRef"
        :data-key="'id'"
        :keeps="20"
        :data-sources="employees"
        :data-component="itemComponent"
        :page-mode="pageMode"
        :item-class="'list-item'"
        :style="virtualScrollStyles"
        @tobottom="loadMore()"
      >
        <div
          v-if="!moreResultsAvailable && employees.length"
          class="footer"
          slot="footer"
        >
          - There are no more relevant results -
        </div></virtual-list
      >
      <b-row no-gutters class="not-found" v-if="!employees.length">
        <img src="@/assets/icons/not-found.svg" />
        <span class="text-secondary text-center"
          >There are no profiles that match the <br />
          Search and Availability criteria</span
        >
      </b-row>
    </div>
    <EmployeeModal :employee="$store.state.team.selectedEmployee" />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import EmployeeRow from '@/components/EmployeeRow.vue';
import EmployeeModal from '@/components/modals/EmployeeModal.vue';
// eslint-disable-next-line no-unused-vars
import { Employee } from '@/types/Employee';
// eslint-disable-next-line no-unused-vars
import VirtualList from 'vue-virtual-scroll-list';
import { SearchTypes } from '@/store/modules/team';

@Component({
  components: {
    EmployeeRow,
    EmployeeModal,
    VirtualList
  }
})
export default class SearchResults extends Vue {
  loadingMore = false;
  id = 'root-element';
  virtualScrollStyles = '';
  itemComponent = EmployeeRow;
  showList = true;

  get isSkillSelectedSearchType() {
    return this.$store.state.team.selectedSearchType === SearchTypes.SKILLS;
  }

  get selectedIndex() {
    return this.$store.state.shared.mobileSelectedIndex;
  }

  get pageMode() {
    return this.id === 'root-element' ? true : false;
  }

  get selectedSort() {
    return this.$store.state.team[this.$store.state.team.selectedSearchType]
      .sort;
  }

  set selectedSort(value: string) {
    this.$store.commit('SET_SORT', value);
  }

  get sortOptions() {
    return this.$store.state.search.sortOptions;
  }

  get allEmployees(): Employee[] {
    return this.$store.state.team.resultsEmployees;
  }

  get employees(): Employee[] {
    return this.$store.getters.employeesToDisplay;
  }

  get moreResultsAvailable(): boolean {
    return !this.$store.getters.currentPagination.reachedEndOfResults;
  }

  selectSort(key: string) {
    this.selectedSort = key;
  }

  bottomOfWindow(): boolean {
    return (
      document.getElementById(this.id)!.scrollTop +
        document.getElementById(this.id)!.offsetHeight +
        20 >=
      document.getElementById(this.id)!.scrollHeight
    );
  }

  initId() {
    this.virtualScrollStyles =
      window.innerWidth <= 1024 ? '' : 'height:100%;overflow-y:auto';
    this.id =
      window.innerWidth <= 1024 ? 'root-element' : 'searchResultsWrapper';
  }

  mounted() {
    window.addEventListener('resize', () => {
      setTimeout(() => {
        this.initId();
      }, 0);
    });
    this.initId();
    this.$store.commit('SET_SCROLLING_REF', this.$refs['virtualSrollingRef']);
  }

  viewDownload() {
    this.$store.commit('SHOW_DOWNLOAD_MODAL', {
      type: 'search',
      modal: this.$bvModal
    });
  }

  async loadMore() {
    if (
      this.moreResultsAvailable &&
      this.selectedIndex === 0 &&
      !this.loadingMore
    ) {
      this.loadingMore = true;
      try {
        await this.$store.dispatch('loadMoreEmployees');
      } finally {
        this.loadingMore = false;
      }
    }
  }

  beforeDestroy() {
    window.removeEventListener('resize', this.initId);
  }
}
</script>

<style lang="scss">
.persons-result-wrapper {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  .persons-count {
    margin-bottom: 12px;
    min-height: 30px;
    display: flex;
    .results-number {
      align-items: center;
      display: flex;
      .show-more {
        svg {
          display: none;
        }
      }
    }
    .custom-dropdown {
      margin-right: 24px;
      align-items: center;
      display: flex;
      .custom-row {
        padding: 0px;
        a {
          padding: 6px 12px;
          &:active {
            background-color: white;
            color: inherit;
          }
        }
      }
      &.mobile {
        display: none;
      }
      button {
        justify-content: space-between;
        height: 28px;
      }
    }
  }
  .results-scrollable-wrapper {
    position: relative;
    overflow-y: auto;
    height: calc(100% - 40px);
    .list-item {
      display: flex;
      align-items: center;
      flex-direction: column;
    }
    .footer {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 12px;
    }
    .not-found {
      position: absolute;
      top: 0;
      left: calc(50% - 107px);
    }
  }
}

@media screen and (max-width: 1024px) {
  .persons-result-wrapper {
    width: 100% !important;
    overflow: visible;
    .results-scrollable-wrapper {
      padding: 0px var(--main-padding);
      overflow: visible;
    }
    .persons-count {
      background-color: #fcfbfa;
      border-bottom: 1px solid $main-border-color;
      display: flex;
      min-height: 50px;
      flex-direction: row-reverse;
      padding: 0px var(--main-padding);
      justify-content: space-between;
      margin-bottom: 0px;
      .custom-dropdown {
        padding: 0px;
        margin-right: 0px !important;
        &.desktop {
          display: none;
        }
        &.mobile {
          display: flex;
          button {
            border: none;
            background-color: transparent !important;
          }
        }
      }
      .results-number {
        flex: 1;
        .dash-icon {
          display: none;
        }
        .show-more {
          svg {
            margin-left: 16px;
            margin-top: 4px;
            display: inline-block;
            width: 20px;
            height: 20px;
          }
          .label {
            display: none;
          }
        }
      }
      > div {
        padding: 12px 0px;
      }
    }
  }
}
</style>
