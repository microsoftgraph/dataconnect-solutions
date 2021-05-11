<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <div class="person-search-wrapper">
    <b-row no-gutters class="search-inputs">
      <div class="search-wrapper">
        <b-tabs class="skills-tabs" v-model="selectedTab" no-fade>
          <b-tab title="Search By Skills">
            <b-form-tags
              ref="searchInput"
              input-id="searchInput"
              class="mt-2 custom-tags-input mb-2"
              :class="{ clearable: isClearable }"
              tagClass="custom-tags"
              add-button-variant="outline-primary"
              add-button-text="+"
              placeholder="Enter skills"
              remove-on-delete
              v-model="terms"
              @keydown.native="keypress"
              :autofocus="true"
              :input-attrs="{ autocomplete: 'off' }"
              :disabled="search"
              :tag-validator="tagValidator"
              @input.native.paste="keypress"
              invalid-tag-text="Duplicate tag(s)"
              no-add-on-enter
            >
            </b-form-tags>
            <TaxonomiesDropdown />
            <b-link class="clear-btn" v-if="isClearable" @click="clearSearch"
              >Clear</b-link
            >
          </b-tab>
          <AutocompleteDropdown
            ref="autocompleteDropdown"
            :autocomplete="autocomplete"
            :autocompleteArr="autocompleteArr"
            @update-native-input="updateNativeInput"
            @add-tag-to-search="addTagToSearch"
          />
          <b-tab title="Search By Name">
            <b-form-input
              placeholder="Enter Employee Name"
              class="name-input mt-2 mb-2"
              v-model="nameSearchTerm"
              autocomplete="off"
              @keypress.enter="searchByName"
            ></b-form-input>
          </b-tab>
        </b-tabs>

        <span class="measure-input"></span>
      </div>
      <div class="search-btn-wrapper">
        <b-button
          @click="showFiltersModal"
          class="filters-btn"
          variant="outline-secondary"
          ><b-icon
            :icon="isFiltersSelected ? 'funnel-fill' : 'funnel'"
            class="mr-1 text-primary"
          ></b-icon
          >Filters</b-button
        >
        <b-button
          class="search-btn default-size"
          variant="primary"
          @click="searchEmployees"
          >Search</b-button
        >
      </div>
      <SearchFiltersModal />
    </b-row>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import Team from '@/components/Team.vue';
// import moment from 'moment';
import { SearchTypes } from '@/store/modules/team';
import AutocompleteDropdown from '@/components/team-modeler/AutocompleteDropdown.vue';
import SearchFilters from '@/components/team-modeler/SearchFilters.vue';
import TaxonomiesDropdown from '@/components/team-modeler/TaxonomiesDropdown.vue';
import SearchFiltersModal from '@/components/modals/SearchFiltersModal.vue';
import moment from 'moment';

@Component({
  components: {
    Team,
    AutocompleteDropdown,
    SearchFilters,
    TaxonomiesDropdown,
    SearchFiltersModal
  }
})
export default class EmployeeSearch extends Vue {
  terms: string[] = [];
  search = false;
  timerId = 0;
  currentSearchTerm = '';
  initialSearchTerm = '';
  nameSearchTerm = '';

  get isFiltersSelected() {
    return (
      this.$store.getters.isFiltersSelected ||
      !moment().isSame(this.$store.state.search.date, 'day')
    );
  }

  get date() {
    return this.$store.state.search.date;
  }

  get selectedTab() {
    return Object.keys(SearchTypes).indexOf(
      this.$store.state.team.selectedSearchType
    );
  }

  set selectedTab(value: number) {
    this.$root.$emit('hideAutocompleteDropdown');
    this.$store.commit('SET_SELECTED_SEARCH_TYPE', value);
  }

  get isClearable() {
    return this.terms.length !== 0;
  }

  get autocompleteArr() {
    return this.$store.state.team.autocomplete.flatMap((skill: any) => {
      let arr = [
        ...skill.relatedTerms.map((x: string) => {
          return {
            isRoot: false,
            value: x,
            parentValue: skill.suggestedSkill
          };
        })
      ];
      arr.unshift({
        isRoot: true,
        value: skill.suggestedSkill,
        children: [...skill.relatedTerms]
      });
      return arr;
    });
  }

  get autocomplete() {
    if (this.currentSearchTerm !== '' && !this.search) {
      return this.$store.state.team.autocomplete;
    }
    return [];
  }

  async mounted() {
    this.$store.commit('SET_SEARCH_EMPLOYEES', []);
    this.initSelectedTaxonomies();
    this.addTerms();
    this.$store.dispatch('setCachedAvailabilityDate');
    await this.$store.dispatch('getSearchFacets');
    setTimeout(() => {
      this.searchEmployees();
    }, 0);
  }

  showFiltersModal() {
    this.$bvModal.show('search-filters-modal');
  }

  initSelectedTaxonomies() {
    const tax = localStorage.getItem('selectedTaxonomies');
    if (tax) this.$store.commit('SET_SELECTED_TAXONOMIES', JSON.parse(tax));
  }

  tagValidator(tag: string) {
    return (
      this.terms.find(t => t.toLowerCase() === tag.toLowerCase()) === undefined
    );
  }

  addTerms() {
    this.terms = [...this.$store.state.team.SKILLS.searchBody.searchTerms];
    this.nameSearchTerm = this.$store.state.team.NAME.searchBody.searchTerms.join(
      ''
    );
  }

  addTagToSearch(value: string) {
    if (
      this.terms.find(x => x.toLowerCase() === value.toLowerCase()) ===
      undefined
    ) {
      const el: any = this.$refs['searchInput'];
      el.addTag(value);
      this.currentSearchTerm = '';
      this.initialSearchTerm = '';
    }
  }

  @Watch('terms')
  onPropertyChanged() {
    this.resetInputWidth();
    this.$root.$emit('hideAutocompleteDropdown');
  }

  clearSearch() {
    this.terms = [];
  }

  async searchAutocompleteTerms() {
    if (this.currentSearchTerm && this.currentSearchTerm.trim() !== '') {
      await this.$store.dispatch(
        'getAutocomplete',
        this.currentSearchTerm.trim()
      );
      // TODO move this
      this.$root.$emit('resetAutocompleteIndex');
      const dropdown = document.getElementById('autocompleteDropdown');
      if (dropdown) dropdown.scrollTo(0, 0);
    }
  }

  debounceFunctionAutocompleteSearch(func: any, delay: number) {
    // Cancels the setTimeout method execution
    clearTimeout(this.timerId);
    // Executes the func after delay time.
    this.timerId = setTimeout(func, delay);
  }

  async searchByName() {
    await this.$store.dispatch('searchEmployees', {
      date: this.date,
      body: {
        searchTerms: [this.nameSearchTerm],
        searchCriteria: SearchTypes.NAME,
        requiredAvailability: {
          minPercentage: 0,
          availableAtTheLatestOn: this.date
        },
        taxonomiesList: [],
        searchFilterValues: this.$store.getters.mapFacetsSelectedValues,
        sortBy: this.$store.state.team[
          this.$store.state.team.selectedSearchType
        ].sort
      }
    });
  }

  resetInputWidth() {
    let inputElement: any = this.$refs['searchInput'];
    inputElement.$el.getElementsByTagName('input')[0].classList.add('w-100');
  }

  adjustInputWidth() {
    let measureElement: any = document.getElementsByClassName(
      'measure-input'
    )[0];
    measureElement.textContent = this.currentSearchTerm;
    let inputElement: any = this.$refs['searchInput'];
    // Set input autocomplete off
    inputElement.$el.getElementsByTagName('input')[0].classList.remove('w-100');
    inputElement.$el
      .getElementsByTagName('input')[0]
      .classList.remove('flex-grow-1');
    inputElement.$el.getElementsByTagName(
      'input'
    )[0].style.width = `${measureElement.offsetWidth + 25}px`;
  }

  updateNativeInput() {
    const autocompleteIndex = (this.$refs['autocompleteDropdown'] as any)
      .autocompleteIndex;
    let formTagInput: any = this.$refs['searchInput'];
    if (autocompleteIndex !== -1) {
      document
        .getElementById(`autocomplete_${autocompleteIndex}`)!
        .scrollIntoView({
          behavior: 'smooth',
          block: 'end',
          inline: 'nearest'
        });
      formTagInput.newTag = this.autocompleteArr[autocompleteIndex].value;
      this.currentSearchTerm = this.autocompleteArr[autocompleteIndex].value;
      this.adjustInputWidth();
    } else {
      formTagInput.newTag = this.initialSearchTerm;
      this.currentSearchTerm = this.initialSearchTerm;
      this.adjustInputWidth();
    }
  }

  keypress(ev: any) {
    // Timeout for paste edge case - give time to input DOM element to update
    setTimeout(() => {
      // UP and DOWN keys
      if (ev.keyCode === 38 || ev.keyCode === 40) {
        ev.preventDefault();
        this.$root.$emit(
          'autocompleteIncreaseIndexUpDown',
          ev.keyCode === 38 ? false : true
        );
        return;
      }
      // LEFT and RIGHT keys
      if (ev.keyCode === 37 || ev.keyCode === 39) {
        ev.preventDefault();
        this.$root.$emit(
          'autocompleteIncreaseIndexLeftRight',
          ev.keyCode === 37 ? false : true
        );
        return;
      }
      if (ev.keyCode === 13) {
        this.$store.dispatch('clearAutocomplete');
        // if double enter then trigger search
        if (ev.srcElement.value) this.addPendingTerms();
        else this.searchEmployeesBySkills();
      }

      if (ev.keyCode !== 13) {
        this.currentSearchTerm = ev.srcElement.value;
        this.initialSearchTerm = ev.srcElement.value;
        this.adjustInputWidth();
        this.debounceFunctionAutocompleteSearch(
          this.searchAutocompleteTerms,
          500
        );
        // Reset input size if no chars in input
        if (this.currentSearchTerm.length === 0) {
          this.resetInputWidth();
          this.$store.dispatch('clearAutocomplete');
        }
      }
    }, 50);
  }

  addPendingTerms() {
    if (
      this.currentSearchTerm &&
      this.terms.find(
        x => x.toLowerCase() === this.currentSearchTerm.toLowerCase()
      ) === undefined
    ) {
      (this.$refs['searchInput'] as any).addTag();
      this.terms.push(this.currentSearchTerm);
    }
  }

  searchEmployees() {
    if (this.$store.state.team.selectedSearchType === SearchTypes.SKILLS) {
      this.searchEmployeesBySkills();
    } else this.searchByName();
  }

  async searchEmployeesBySkills() {
    if (!this.search) {
      this.addPendingTerms();
      this.search = true;
      // stop any debouncer
      clearTimeout(this.timerId);
      this.$store.dispatch('getTeamMembers');
      await this.$store.dispatch('searchEmployees', {
        date: this.date,
        body: {
          searchTerms: [...this.terms],
          searchCriteria: SearchTypes.SKILLS,
          taxonomiesList: this.$store.state.team.selectedTaxonomies,
          searchFilterValues: this.$store.getters.mapFacetsSelectedValues,
          requiredAvailability: {
            minPercentage: 0,
            availableAtTheLatestOn: this.date
          },
          sortBy: this.$store.state.team[
            this.$store.state.team.selectedSearchType
          ].sort
        }
      });
      this.$store.state.team.virtualSrollingRef.reset();
      this.$store.dispatch('clearAutocomplete');
      this.$root.$emit('hideAutocompleteDropdown');
      this.search = false;
      this.currentSearchTerm =
        this.$store.state.team.selectedSearchType === SearchTypes.SKILLS
          ? ''
          : this.currentSearchTerm;
      this.initialSearchTerm = '';
      this.$root.$emit('resetAutocompleteIndex');
    }
  }

  beforeDestroy() {
    this.selectedTab = 0;
  }
}
</script>

<style lang="scss">
.person-search-wrapper {
  .search-inputs {
    margin-top: -7px;
    .search-wrapper {
      width: calc(100% - 132px);
      margin-right: 12px;
      .nav-tabs {
        border-bottom: none;
        margin-bottom: -6px;
        flex-wrap: nowrap;
        li {
          .nav-link {
            border-top: none;
            border-radius: 0px;
            border-left: 1px solid #ced4da;
            color: $gray-dark;
            border-right: 1px solid #ced4da;
            position: relative;
            padding: 10px 16px;
            &.active {
              font-weight: 600;
              &::after {
                content: '';
                display: block;
                position: absolute;
                height: 3px;
                width: 100%;
                left: 0;
                bottom: -1px;
                z-index: 1;
                background-color: $primary;
              }
            }
            &:focus {
              text-decoration: none;
              outline: none;
            }
          }
          &:last-child {
            .nav-link {
              border-left: none;
            }
          }
        }
      }
      .tab-content {
        position: relative;
        .tab-pane {
          position: relative;
          display: flex;
          .custom-tags-input {
            max-width: calc(100% - 120px);
            border-top-left-radius: 0px;
            border-top-right-radius: 0px;
            border-bottom-right-radius: 0px;
            border-right: none;
            position: relative;
          }
          .name-input {
            height: 40px;
            color: black;
            font-weight: 300;
          }
          .input-group-append {
            width: 40px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-left: 0px;
            background-color: $primary;
            cursor: pointer;
            color: white;
            border-top-right-radius: 4px;
            border-bottom-right-radius: 4px;
            border: 1px solid $primary;
            &:hover {
              background-color: white;
              color: $primary;
            }
          }
        }
      }
    }
    .search-btn-wrapper {
      .filters-btn {
        display: none;
      }
      .search-btn {
        height: 40px;
        margin-top: 42px;
      }
    }

    .measure-input {
      position: absolute;
      top: 0;
      word-break: break-word;
      visibility: hidden;
      left: 0;
    }

    .clear-btn {
      position: absolute;
      top: 6px;
      right: 132px;
      z-index: 1;
      line-height: 40px;
      color: #007bff;
      &:hover {
        color: #0056b3;
        text-decoration: underline;
      }
    }
  }
}

@media screen and (max-height: 700px) {
  .autocomplete-dropdown {
    max-height: 400px !important;
  }
}
@media screen and (max-width: 1024px) {
  .person-search-wrapper {
    .nav-item {
      a {
        text-align: center;
      }
    }
    .tab-pane {
      &:first-child {
        flex-direction: column;
        .custom-tags-input {
          margin-bottom: 0px !important;
          max-width: 100% !important;
          border-radius: 0px;
          border-right: 1px solid #ced4da !important;
          border-bottom: none;
        }
        .clear-btn {
          right: 10px;
        }

        .tax-dropdown {
          width: 100%;
          .dropdown-toggle {
            margin-top: 0px;
            height: 36px;
            border-top: none;
            border-bottom-left-radius: 4px;
            border-top-right-radius: 0px;
            background-color: white !important;
          }
          ul {
            transform: none;
            width: 100%;
          }
        }
      }
    }
    .search-inputs {
      .search-btn-wrapper {
        display: flex;
        margin-top: 10px;
        margin-bottom: 2px;
        .filters-btn {
          width: 30%;
          height: 40px;
          display: block !important;
        }
        .search-btn {
          margin-left: 10px;
          margin-top: 0px;
          width: 70%;
        }
      }
    }
    .nav-tabs {
      li {
        flex: 1;
      }
    }
  }
}
</style>
