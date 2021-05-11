/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

import { Module } from 'vuex';
import { Employee, TeamMember } from '@/types/Employee';
import axios from 'axios';

export enum SearchTypes {
  SKILLS = 'SKILLS',
  NAME = 'NAME'
}

const team: Module<any, any> = {
  state: {
    team: {
      name: '',
      description: ''
    },
    teamMembers: [],
    teamMembersMap: {},
    resultsEmployees: {
      SKILLS: [],
      NAME: []
    },
    selectedSearchType: SearchTypes.SKILLS,
    SKILLS: {
      sort: 'relevance',
      pagination: {
        reachedEndOfResults: true,
        size: 50,
        offset: 0
      },
      searchBody: {
        searchTerms: [],
        sortBy: 'relevance',
        date: '',
        searchCriteria: SearchTypes.SKILLS,
        requiredAvailability: {
          minPercentage: 0,
          availableAtTheLatestOn: ''
        }
      }
    },
    NAME: {
      sort: 'relevance',
      pagination: {
        reachedEndOfResults: true,
        size: 50,
        offset: 0
      },
      searchBody: {
        searchTerms: [],
        sortBy: 'relevance',
        date: '',
        searchCriteria: SearchTypes.NAME,
        requiredAvailability: {
          minPercentage: 0,
          availableAtTheLatestOn: ''
        }
      }
    },
    taxonomies: {
      software: 'Software Engineering',
      data_science: 'Data Science',
      facilities: 'Facilities',
      finance: 'Finance',
      human_relations: 'HR',
      legal: 'Legal',
      sales_marketing: 'Sales & Marketing',
      oilgas: 'Oil & Gas',
      healthcare: 'Healthcare'
    },
    selectedTaxonomies: ['software'],
    autocomplete: [],
    selectedEmployee: {},
    selectedEmployeeSkills: [],
    virtualSrollingRef: null
  },
  getters: {
    currentPagination: state => {
      return state[state.selectedSearchType].pagination;
    },
    employeesToDisplay: state => {
      return state.resultsEmployees[state.selectedSearchType];
    },
    isTeamCreated: state => {
      return state.team.name && state.team.name !== '';
    },
    totalNumberOfEmployees: state => {
      return state[state.selectedSearchType].pagination.total;
    },
    isEmployeeSelected: state => (employee: Employee) => {
      return (
        state.teamMembers.find(
          (member: TeamMember) => member.employeeId === employee.id
        ) !== undefined
      );
    },
    getTeamMemberIdForEmployeeId: state => (employeeId: string) => {
      return state.teamMembers.find(
        (member: TeamMember) => member.employeeId === employeeId
      )?.teamMemberId;
    }
  },
  mutations: {
    SET_SORT(state, value) {
      state[state.selectedSearchType].sort = value;
    },
    SET_SCROLLING_REF(state, value) {
      state.virtualSrollingRef = value;
    },
    SET_SELECTED_SEARCH_TYPE(state, value) {
      state.selectedSearchType = Object.keys(SearchTypes)[value];
    },
    SET_SELECTED_TAXONOMIES(state, values) {
      localStorage.setItem('selectedTaxonomies', JSON.stringify(values));
      state.selectedTaxonomies = values;
    },
    SET_SELECTED_EMPLOYEE_ID(state, id: string) {
      state.selectedEmployee = { id };
    },
    SET_SELECTED_EMPLOYEE_SKILLS(state, skills: []) {
      state.selectedEmployeeSkills = skills;
    },
    SET_SELECTED_EMPLOYEE(state, employee: Employee) {
      state.selectedEmployee = employee;
    },
    UPDATE_EMPLOYEE_IN_TEAM(state, value: boolean) {
      const skillsEmp: Employee = state.resultsEmployees.SKILLS.find(
        (x: Employee) => x.id === state.selectedEmployee.id
      );
      if (skillsEmp) skillsEmp.includedInCurrentTeam = value;
      const nameEmp: Employee = state.resultsEmployees.NAME.find(
        (x: Employee) => x.id === state.selectedEmployee.id
      );
      if (nameEmp) nameEmp.includedInCurrentTeam = value;
    },
    SET_SEARCH_PARAMS(state, body) {
      state[body.searchCriteria].searchBody = body;
    },
    SET_TEAM(state, team) {
      state.team = {
        name: team.teamName,
        description: team.teamDescription
      };
    },
    SET_SEARCH_EMPLOYEES(state, data) {
      state.resultsEmployees[state.selectedSearchType] = data.map(
        (employee: any) => {
          return new Employee(employee);
        }
      );
    },

    CLEAR_AUTOCOMPLETE(state) {
      state.autocomplete = [];
    },
    SET_MORE_SEARCH_EMPLOYEES(state, data) {
      state.resultsEmployees[state.selectedSearchType] = state.resultsEmployees[
        state.selectedSearchType
      ].concat(data.map((employee: any) => new Employee(employee)));
    },
    SET_SEARCHED_EMPLOYEES_EOR(state, value) {
      state[state.selectedSearchType].pagination.reachedEndOfResults = value;
    },
    SET_SEARCHED_EMPLOYEES_OFFSET(state, offset) {
      state[state.selectedSearchType].pagination.offset = offset;
    },
    INCREASE_OFFSET(state) {
      state[state.selectedSearchType].pagination.offset++;
    },
    SET_TEAM_MEMBERS(state, data) {
      state.teamMembers = data.map((member: any) => new TeamMember(member));
      // Update employee in teams
      state.teamMembersMap = {};
      state.teamMembers.forEach((member: TeamMember) => {
        state.teamMembersMap[member.email!] = member;
      });
      state.resultsEmployees.SKILLS.map(
        (emp: Employee) =>
          (emp.includedInCurrentTeam = state.teamMembersMap[emp.mail!]
            ? true
            : false)
      );
      state.resultsEmployees.NAME.map(
        (emp: Employee) =>
          (emp.includedInCurrentTeam = state.teamMembersMap[emp.mail!]
            ? true
            : false)
      );
    },
    SET_AUTOCOMPLETE_VALUES(state, data) {
      state.autocomplete = data.map((x: any) => {
        return {
          relatedTerms: x.relatedTerms.filter((term: string) => term),
          suggestedSkill: x.suggestedSkill
        };
      });
    }
  },
  actions: {
    getDownloadTeam() {
      return axios.get(`/gdc/team/download`);
    },
    getDownloadEmployees(context, total) {
      return axios.post(
        `/gdc/employee-search/search?size=${total}&offset=0`,
        context.state[context.state.selectedSearchType].searchBody
      );
    },
    getAutocomplete(context, beginsWith) {
      return axios
        .get(
          `/gdc/employee-search/skill-suggestions?beginsWith=${encodeURIComponent(
            beginsWith
          )}&taxonomiesList=${context.state.selectedTaxonomies}`,
          {
            headers: { noLoader: true }
          }
        )
        .then(response => {
          context.commit('SET_AUTOCOMPLETE_VALUES', response.data);
        });
    },

    searchEmployees(context, values) {
      context.commit('SET_SEARCH_EMPLOYEES', []);
      context.commit('SET_SEARCHED_EMPLOYEES_OFFSET', 0);
      context.commit('SET_SEARCH_PARAMS', values.body);
      return axios
        .post(
          `/gdc/employee-search/search?size=${
            context.state[context.state.selectedSearchType].pagination.size
          }&offset=${
            context.state[context.state.selectedSearchType].pagination.offset
          }`,
          values.body
        )
        .then(response => {
          context.commit('SET_SEARCH_EMPLOYEES', response.data.employees);
          // if full length list and end reachedEndOfResults === true then wait for one more page
          context.commit(
            'SET_SEARCHED_EMPLOYEES_EOR',
            response.data.employees.length ===
              context.state[context.state.selectedSearchType].pagination.size &&
              response.data.reachedEndOfResults === true
              ? false
              : response.data.reachedEndOfResults
          );
          context.commit('INCREASE_OFFSET');
          return true;
        })
        .catch(err => {
          return false;
        });
    },
    loadMoreEmployees(context) {
      const offset =
        context.state[context.state.selectedSearchType].pagination.offset *
        context.state[context.state.selectedSearchType].pagination.size;
      return axios
        .post(
          `/gdc/employee-search/search?size=${
            context.state[context.state.selectedSearchType].pagination.size
          }&offset=${offset}`,
          context.state[context.state.selectedSearchType].searchBody
        )
        .then(response => {
          context.commit('SET_MORE_SEARCH_EMPLOYEES', response.data.employees);
          // if full length list and end reachedEndOfResults === true then wait for one more page
          context.commit(
            'SET_SEARCHED_EMPLOYEES_EOR',
            response.data.employees.length ===
              context.state[context.state.selectedSearchType].pagination.size &&
              response.data.reachedEndOfResults === true
              ? false
              : response.data.reachedEndOfResults
          );
          context.commit('INCREASE_OFFSET');
        });
    },
    clearAutocomplete(context) {
      context.commit('CLEAR_AUTOCOMPLETE');
    },
    async getTeamMembers(context) {
      let response = await axios.get(`/gdc/team/structure/members`);
      if (response.status === 200)
        context.commit('SET_TEAM_MEMBERS', response.data);
    },
    async addEmployeeToTeam(context, employee: Employee) {
      if (
        context.state.teamMembers.find(
          (member: TeamMember) => member.employeeId === employee.id
        ) === undefined &&
        context.getters.isTeamCreated
      ) {
        try {
          let response = await axios.post(`/gdc/team/structure/members`, {
            employeeId: employee.id,
            email: employee.mail,
            name: employee.name,
            skills: employee.relevantSkills!.map(x => x.value)
          });
          if (response.status === 200) {
            context.commit('SHOW_MESSAGE', {
              type: 3,
              message: `${employee.name} was added to your team!`
            });
            return true;
          }
        } finally {
          context.dispatch('getTeamMembers');
        }
      }
    },
    async removeEmployeeFromTeam(context, teamMember) {
      try {
        let response = await axios.delete(
          `/gdc/team/structure/members/${teamMember.id}`
        );
        if (response.status === 200) {
          context.commit('SHOW_MESSAGE', {
            type: 3,
            message: `${teamMember.name} was removed from your team!`
          });
          return true;
        }
      } finally {
        context.dispatch('getTeamMembers');
      }
    },
    async getCurrentEmployee(context) {
      let response = await axios.get(
        `/gdc/employee/${context.state.selectedEmployee.id}`
      );
      if (context.state.selectedEmployeeSkills)
        response.data.relevantSkills = context.state.selectedEmployeeSkills;
      let employee = new Employee(response.data);
      context.commit('SET_SELECTED_EMPLOYEE', employee);
    },
    async getTeamInfo(context) {
      let response = await axios.get(`/gdc/team/info`);
      context.commit('SET_TEAM', response.data);
    },
    async updateTeam(context, team) {
      const body = {
        teamName: team.name,
        teamDescription: team.description
      };
      let response = await axios.put(`/gdc/team/info`, body);
      if (response.status === 200) {
        context.commit('SET_TEAM', body);
        context.commit('SHOW_MESSAGE', {
          type: 3,
          message: `Team was saved successfully!`
        });
      }
      return response;
    }
  }
};

export default team;
