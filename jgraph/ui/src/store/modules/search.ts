import { Module } from 'vuex';
import axios from 'axios';
import moment from 'moment';

const search: Module<any, any> = {
  state: {
    date: moment(new Date()).format('YYYY-MM-DD'),
    sortOptions: {
      relevance: 'Relevance',
      availability: 'Availability'
    },
    facets: []
  },
  getters: {
    mapFacetsSelectedValues: state => {
      let selectedFilters: any = {};
      state.facets.forEach((filter: any) => {
        if (
          filter.selected.length !== 0 &&
          filter.selected.length !== filter.values.length
        )
          selectedFilters[filter.filterType] = filter.selected;
      });
      return selectedFilters;
    },
    isFiltersSelected: state => {
      let value = false;
      state.facets.forEach((filter: any) => {
        if (filter.selected.length !== 0) value = true;
      });
      return value;
    }
  },
  mutations: {
    SET_DATE(state, value) {
      state.date = value;
      localStorage.setItem('availabilityDate', value);
    },
    CLEAR_SEARCH_FILTER(state, filterType) {
      const searchFilter = state.facets.find(
        (filter: any) => filter.filterType === filterType
      );
      if (searchFilter) searchFilter.selected = [];
    },
    SELECT_SEARCH_FILTER(state, filterType) {
      const searchFilter = state.facets.find(
        (filter: any) => filter.filterType === filterType
      );
      if (searchFilter) searchFilter.selected = searchFilter.values;
    },
    CLEAR_SEARCH_FILTERS(state) {
      state.facets.map((filter: any) => {
        filter.selected = [];
      });
    },
    SET_SEARCH_FACETS(state, values) {
      let cachedSearchFilters: any;
      if (localStorage.getItem('searchFilters'))
        cachedSearchFilters = JSON.parse(
          localStorage.getItem('searchFilters')!
        );
      let filters: any = [];
      values.forEach((filter: any) => {
        let sameValues = false;
        let ft;
        if (cachedSearchFilters) {
          ft = cachedSearchFilters.find(
            (x: any) => x.filterType === filter.filterType
          );
          if (ft)
            // if there are different options then ignore selected values
            sameValues =
              JSON.stringify(ft.values.sort()) ===
              JSON.stringify(filter.values.sort());
        }
        if (filter.values.length !== 0)
          filters.push({
            filterType: filter.filterType,
            values: filter.values.sort(),
            selected: cachedSearchFilters && ft && sameValues ? ft.selected : []
          });
      });
      state.facets = filters;
    }
  },
  actions: {
    setCachedAvailabilityDate(context) {
      const date = localStorage.getItem('availabilityDate');
      if (date)
        if (!moment(date).isBefore(moment())) {
          context.commit('SET_DATE', moment(date).format('YYYY-MM-DD'));
        }
    },
    persistSearchFilters(context) {
      localStorage.setItem(
        'searchFilters',
        JSON.stringify(context.state.facets)
      );
    },
    getSearchFacets(context) {
      return axios
        .get(`/gdc/employee-search/search-filters`)
        .then(response => {
          context.commit('SET_SEARCH_FACETS', response.data);
          return true;
        })
        .catch(err => {
          return false;
        });
    }
  }
};

export default search;
