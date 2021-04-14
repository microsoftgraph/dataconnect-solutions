<template>
  <div class="search-filters">
    <h5 class="border-bottom title">Search Filters</h5>
    <div class="filters-wrapper">
      <div class="filter">
        <AvailabilityCalendar />
      </div>
      <div
        class="filter"
        v-for="filter in filters"
        :key="`filter_${filter.filterType}`"
      >
        <span class="text-sb label">{{
          constants.settings.facets[filter.filterType]
            ? constants.settings.facets[filter.filterType].searchLabel
              ? constants.settings.facets[filter.filterType].searchLabel
              : constants.settings.facets[filter.filterType]
            : getFilterLabel(filter.filterType)
        }}</span>
        <b-tooltip
          :delay="{ show: 500, hide: 100 }"
          :target="`tooltip_filter_${filter.filterType}`"
          triggers="hover"
          v-if="filter.selected.length !== 0"
        >
          <span>{{ filter.selected.join(', ') }}</span>
        </b-tooltip>
        <b-dropdown
          :id="`tooltip_filter_${filter.filterType}`"
          class="custom-filter-dropdown"
          :text="
            filter.selected.length !== 0 ? filter.selected.join(', ') : 'Any'
          "
          variant="outline-primary"
        >
          <div class="clear-select-group">
            <b-button-group>
              <b-button
                variant="outline-secondary"
                @click="selectFilter(filter.filterType)"
                >Select All</b-button
              >
              <b-button
                variant="outline-secondary"
                @click="clearFilter(filter.filterType)"
                >Clear All</b-button
              >
            </b-button-group>
          </div>
          <div class="options">
            <div
              class="option-row"
              v-for="option in filter.values"
              :key="`option_${filter.filterType}_${option}`"
            >
              <b-form-checkbox
                :value="option"
                v-model="filter.selected"
                @change="persistSearchFilters"
                name="flavour"
                >{{ option }}</b-form-checkbox
              >
            </div>
          </div>
        </b-dropdown>
      </div>
    </div>
    <div class="reset-btn">
      <b-button variant="outline-secondary default-size" @click="clearFilters"
        >Clear Filters</b-button
      >
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import AvailabilityCalendar from '@/components/team-modeler/AvailabilityCalendar.vue';
import { constants } from '@/helpers/constants';
import moment from 'moment';

@Component({
  components: { AvailabilityCalendar },
  data() {
    return {
      constants
    };
  }
})
export default class SearchFilters extends Vue {
  calendarToggled = false;

  persistSearchFilters() {
    setTimeout(() => {
      this.$store.dispatch('persistSearchFilters');
    }, 0);
  }

  get filters() {
    return this.$store.state.search.facets;
  }

  get date() {
    return this.$store.state.search.date;
  }

  set date(value: string) {
    this.$store.commit('SET_DATE', value);
  }

  mounted() {
    this.$store.dispatch('setCachedAvailabilityDate');
    this.$store.dispatch('getSearchFacets');
  }

  getFilterLabel(str: string) {
    // eslint-disable-next-line no-unused-vars
    let strClone = str;
    ['hr_data_', 'm365_'].forEach(key => {
      strClone = strClone.replace(key, '');
    });
    strClone = strClone.replaceAll('_', ' ');
    return strClone.trim() !== '' ? strClone : str;
  }

  selectFilter(filter: string) {
    this.$store.commit('SELECT_SEARCH_FILTER', filter);
    this.$store.dispatch('persistSearchFilters');
  }

  clearFilter(filter: string) {
    this.$store.commit('CLEAR_SEARCH_FILTER', filter);
    this.$store.dispatch('persistSearchFilters');
  }

  clearFilters() {
    this.date = moment().format('YYYY-MM-DD');
    this.$store.commit('CLEAR_SEARCH_FILTERS');
    this.$store.dispatch('persistSearchFilters');
  }
}
</script>

<style lang="scss">
.search-filters {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow-y: hidden;
  h5 {
    font-weight: 600;
    height: 42px;
    margin: 18px 24px 18px 3px !important;
    padding-bottom: 18px !important;
  }
  .filters-wrapper {
    height: calc(100% - 162px);

    padding-right: 24px;
    overflow-y: auto;
    .filter {
      padding-left: 3px;
      margin-bottom: 16px;
      .label {
        text-transform: capitalize;
      }
      .custom-filter-dropdown {
        margin-top: 8px;
        .dropdown-menu {
          overflow: hidden;
          border-color: #007bff;
          box-shadow: 0 0 0 0.2rem #007bff40;

          padding: 0px;
          left: -3px !important;
          width: calc(100% + 1px);
          .clear-select-group {
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 16px 0px;
            margin-bottom: 8px;
            border-bottom: 1px solid $main-border-color;
            button {
              text-align: center;
              padding: 0px;
              width: 100px;
              min-width: 100px;
            }
          }
          .options {
            overflow-y: auto;
            max-height: 216px;
          }
        }
      }
    }
  }
  .reset-btn {
    padding-right: var(--main-padding);
    margin: auto;
  }
}
@media screen and (max-width: 1024px) {
  .search-filters {
    h5 {
      display: none;
    }
    .filters-wrapper {
      padding-right: 0px;
      .filter {
        .custom-filter-dropdown {
          .dropdown-menu {
            left: 7px !important;
            width: calc(100% + 1px);
          }
        }
      }
    }
  }
}
</style>
