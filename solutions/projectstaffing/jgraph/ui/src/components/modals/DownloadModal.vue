<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <b-modal
    @hidden="resetForm"
    @show="resetForm"
    id="export-modal"
    dialog-class="default-modal"
    centered
    scrollable
  >
    <template v-slot:modal-header="{ close }">
      <h5 class="text-sb">
        Export {{ isTypeTeam ? 'Team' : 'Search Results' }}
      </h5>
      <b-icon-x @click="close"> </b-icon-x>
    </template>
    <b-row no-gutters class="w-100">
      <b-col class="flex-4">
        <b-form-group label="File Name*">
          <b-form-input
            autofocus
            :state="validateState('name')"
            v-model="$v.form.name.$model"
          ></b-form-input>
          <b-form-invalid-feedback v-if="!$v.form.name.required"
            >Name must not be empty.</b-form-invalid-feedback
          >
        </b-form-group>
      </b-col>
      <b-col class="ml-4 flex-2">
        <b-form-group label="Format*">
          <b-form-select
            :state="validateState('format')"
            v-model="$v.form.format.$model"
            :options="formats"
          ></b-form-select>
          <b-form-invalid-feedback v-if="!$v.form.format.required"
            >Format must not be empty.</b-form-invalid-feedback
          >
        </b-form-group>
      </b-col>
    </b-row>
    <b-row v-if="!isTypeTeam" no-gutters class="w-100">
      <b-form-group
        description="You can specify a smaller number if you want to export a results subset. This can be helpful if you have a large results list, since exporting can take time."
        class="flex-1"
        label="Results to Export"
      >
        <b-form-input
          :state="validateState('no')"
          v-model="$v.form.no.$model"
          type="number"
        ></b-form-input>
        <b-form-invalid-feedback v-if="!$v.form.no.required"
          >Number must not be empty.</b-form-invalid-feedback
        >
        <b-form-invalid-feedback
          v-if="!$v.form.no.minValue || !$v.form.no.maxValue"
          >Number must be between 1 and
          {{ resultsEmployees.length }}.</b-form-invalid-feedback
        >
      </b-form-group>
    </b-row>
    <template v-slot:modal-footer>
      <b-button variant="outline-secondary" class="default-size" @click="close">
        Cancel
      </b-button>
      <b-button variant="primary" class="default-size" @click="submit">
        Export
      </b-button>
    </template>
  </b-modal>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import XLSX from 'xlsx';
import { required, minValue, maxValue } from 'vuelidate/lib/validators';
import { validateState } from '@/utils/helpers';
// eslint-disable-next-line no-unused-vars
import { Employee } from '@/types/Employee';

Component.registerHooks(['validations']);

@Component({
  components: {},
  methods: { validateState }
})
export default class DownloadModal extends Vue {
  id = 'export-modal';
  formats = [
    { value: 'xlsx', text: 'Excel' },
    { value: 'csv', text: 'CSV' }
  ];
  form = {
    name: '',
    format: 'xlsx',
    no: 0
  };

  get isTypeTeam() {
    return this.$store.state.shared.downloadModalType === 'team';
  }

  get team() {
    return this.$store.state.team.team;
  }

  get resultsEmployees() {
    return this.$store.getters.employeesToDisplay;
  }

  @Watch('resultsEmployees')
  onPropertyChanged() {
    this.resetForm();
  }

  mounted() {}

  validations() {
    if (this.isTypeTeam)
      return {
        form: {
          name: {
            required
          },
          format: {
            required
          }
        }
      };
    else
      return {
        form: {
          name: {
            required
          },
          format: {
            required
          },
          no: {
            required,
            minValue: minValue(1),
            maxValue: maxValue(this.resultsEmployees.length)
          }
        }
      };
  }

  close() {
    this.$bvModal.hide(this.id);
  }

  resetForm() {
    this.form = {
      name: this.isTypeTeam ? this.team.name : 'search_results',
      format: 'xlsx',
      no: this.resultsEmployees ? this.resultsEmployees.length : 1
    };
    this.$v.$reset();
  }

  submit() {
    this.$v.form.$touch();
    if (!this.$v.form.$invalid) {
      this.downloadData();
    }
  }

  async downloadData() {
    if (this.isTypeTeam) {
      let response = await this.$store.dispatch('getDownloadTeam');
      if (response.status === 200)
        this.generateTeamXLSX(response.data.members, 'Team');
    } else {
      this.generateSearchXLSX(
        this.resultsEmployees.slice(0, this.form.no).map((emp: Employee) => {
          return {
            ...emp,
            relevantSkills: emp.relevantSkills!.map((sk: any) => sk.value),
            declaredSkills: emp.declaredSkills!.map((sk: any) => sk.value),
            inferredSkills: emp.inferredSkills!.map((sk: any) => sk.value)
          };
        }),
        'Search Results'
      );
    }
    this.close();
  }

  generateTeamXLSX(arr: any[], type: string) {
    const data = XLSX.utils.json_to_sheet(
      arr.map((x: Employee) => {
        return {
          Name: x.name,
          Mail: x.mail,
          Role: x.role,
          'Current Engagement': x.currentEngagement,
          'Reports To': x.reportsTo,
          'Available Since': x.availableSince ? x.availableSince : 'Now',
          About: x.about,
          Skills: x.declaredSkills ? x.declaredSkills.join(', ') : ''
        };
      })
    );
    const wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, data, type);
    XLSX.writeFile(wb, `${this.form.name}.${this.form.format}`);
  }

  async generateSearchXLSX(arr: any[], type: string) {
    const data = XLSX.utils.json_to_sheet(
      arr.map((x: Employee) => {
        return {
          Name: x.name,
          Mail: x.mail,
          Role: x.role,
          'Current Engagement': x.currentEngagement,
          'Reports To': x.reportsTo,
          'Available Since': x.availableSince ? x.availableSince : 'Now',
          About: x.about,
          'Profile Skills': x.relevantSkills ? x.relevantSkills.join(', ') : '',
          'Inferred Skills': x.inferredSkills ? x.inferredSkills.join(', ') : ''
        };
      })
    );
    const wb = XLSX.utils.book_new();
    if (this.form.format === 'xlsx') {
      await this.$store.dispatch('getSearchSettings');
      XLSX.utils.book_append_sheet(
        wb,
        XLSX.utils.json_to_sheet([
          {
            'Search Terms': this.$store.state.team[
              this.$store.state.team.selectedSearchType
            ].searchBody.searchTerms.join(', '),
            'Search Type': this.$store.state.team.selectedSearchType,
            'Available At': this.$store.state.team[
              this.$store.state.team.selectedSearchType
            ].searchBody.requiredAvailability.availableAtTheLatestOn,
            'Exported On': new Date().toString()
          }
        ]),
        'Search Criteria'
      );
      XLSX.utils.book_append_sheet(
        wb,
        XLSX.utils.json_to_sheet(this.$store.getters.mapSearchSettingsToArray),
        'Search Settings'
      );
    }

    XLSX.utils.book_append_sheet(wb, data, type);
    XLSX.writeFile(wb, `${this.form.name}.${this.form.format}`);
  }

  open() {
    this.$bvModal.show(this.id);
  }
}
</script>

<style lang="scss">
#export-modal {
  .modal-dialog {
    .modal-body {
      flex-direction: column;
      padding: 20px 24px;
      display: flex;
    }
  }
}
</style>
