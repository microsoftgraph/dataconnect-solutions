<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <div class="default-view-wrapper settings-wrapper">
    <div class="mobile-header-wrapper border-bottom">
      <div v-if="showMenu" class="backdrop" @click="toggleSettingsMenu"></div>
      <span @click="toggleSettingsMenu" class="menu-icon">
        <svg
          width="100%"
          height="100%"
          viewBox="0 0 16 16"
          version="1.1"
          xmlns="http://www.w3.org/2000/svg"
          xmlns:xlink="http://www.w3.org/1999/xlink"
          xml:space="preserve"
          xmlns:serif="http://www.serif.com/"
          style="fill-rule:evenodd;clip-rule:evenodd;stroke-linejoin:round;stroke-miterlimit:2;"
        >
          <rect
            id="ico-16-menu-active"
            x="0"
            y="0"
            width="16"
            height="16"
            style="fill:none;"
          />
          <path
            d="M2.5,11.25c0,-0.206 0.169,-0.375 0.375,-0.375l10.25,0c0.206,0 0.375,0.169 0.375,0.375c0,0.206 -0.169,0.375 -0.375,0.375l-10.25,0c-0.206,0 -0.375,-0.169 -0.375,-0.375Zm0,-6.5c0,-0.206 0.169,-0.375 0.375,-0.375l10.25,0c0.206,0 0.375,0.169 0.375,0.375c0,0.206 -0.169,0.375 -0.375,0.375l-10.25,0c-0.206,0 -0.375,-0.169 -0.375,-0.375Z"
          />
        </svg>
      </span>
      <span class="title">Settings</span>
      <div class="actions">
        <b-button
          v-for="action in mobileSettingsActions"
          :key="`${action.text}_header_action`"
          :variant="action.variant"
          :class="action.class"
          @click="emit(action.event)"
          >{{ action.text }}</b-button
        >
      </div>
    </div>
    <b-row class="settings-content" no-gutters>
      <b-tabs v-model="tabIndex" id="settings-tabs" pills card vertical>
        <b-tab active>
          <template v-slot:title>
            <span @click="toggleSettingsMenu">
              <b-icon-grid></b-icon-grid> Data Sources
            </span>
          </template>
          <DataSources
            ref="dataSources"
            :configurations="configurations"
            @resetDataSourceSettings="resetDataSourceSettings"
            @resetSearchResultsFilters="resetSearchResultsFilters"
          />
        </b-tab>
        <b-tab>
          <template v-slot:title>
            <span @click="toggleSettingsMenu">
              <b-icon-search></b-icon-search> Search Criteria
            </span>
          </template>
          <SearchCriteria
            ref="searchCriteria"
            :configurations="configurations"
            @refreshList="refreshList"
            @resetSearchCriteria="resetSearchCriteria"
            @resetEmailContent="resetEmailContent"
          />
        </b-tab>
        <b-tab>
          <template v-slot:title>
            <span @click="toggleSettingsMenu">
              <b-icon-bar-chart-line></b-icon-bar-chart-line> Emails Search
            </span>
          </template>
          <SearchRanking
            ref="searchRanking"
            :configurations="configurations"
            @resetIntValues="resetIntValues"
            @resetDomains="resetDomains"
          />
        </b-tab>
        <b-tab v-if="isAdmin">
          <template v-slot:title>
            <span @click="toggleSettingsMenu">
              <b-icon-shuffle></b-icon-shuffle> Ingestion Mode
            </span>
          </template>
          <Ingestion />
        </b-tab>
        <b-tab v-if="isAdmin">
          <template v-slot:title>
            <span @click="toggleSettingsMenu">
              <b-icon-box></b-icon-box> Upload HR Data
            </span>
          </template>
          <HRData />
        </b-tab>
      </b-tabs>
    </b-row>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { SearchSettings } from '@/types/Settings';
import moment from 'moment';
import SearchCriteria from '@/components/settings/SearchCriteria.vue';
import SearchRanking from '@/components/settings/SearchRanking.vue';
import Ingestion from '@/components/settings/Ingestion.vue';
import HRData from '@/components/settings/HRData.vue';
import DataSources from '@/components/settings/DataSources.vue';

@Component({
  components: {
    SearchCriteria,
    SearchRanking,
    Ingestion,
    DataSources,
    HRData
  }
})
export default class Settings extends Vue {
  configurations: SearchSettings = {};
  showMenu = false;

  get tabIndex() {
    return this.$store.state.settings.tabIndex;
  }

  set tabIndex(value) {
    this.$store.commit('SET_SETTINGS_TAB_INDEX', value);
  }

  get isAdmin() {
    return this.$store.state.auth.currentUser.isAdmin();
  }

  toggleSettingsMenu() {
    let menu = document
      .getElementById('settings-tabs')!
      .querySelectorAll('.col-auto')[0];
    if (menu && menu.classList.contains('d-block')) {
      menu.classList.remove('d-block');
      this.showMenu = false;
    } else {
      this.showMenu = true;
      menu.classList.add('d-block');
    }
  }

  get mobileSettingsActions() {
    return this.$store.state.shared.headers.Settings.actions;
  }

  emit(ev: string) {
    this.$root.$emit(ev);
  }

  savePendingTags() {
    const included: any = (this.$refs['searchRanking'] as any).$refs[
      'includedDomains'
    ];
    included.addTag(included.$refs.input.value);

    const excluded: any = (this.$refs['searchRanking'] as any).$refs[
      'excludedDomains'
    ];
    excluded.addTag(excluded.$refs.input.value);
  }

  async mounted() {
    this.$root.$on('user-role-loaded', this.verifyTabPreset);
    this.$store.dispatch('getResetDataSourcesFilters');
    this.$store.dispatch('getResetSearchCriteria');
    this.$store.dispatch('getResetRankingValues');
    this.$store.dispatch('getResetDataSources');
    await this.$store.dispatch('getSearchSettings');
    this.initConfigs();
    this.$root.$on('saveSearchSettings', this.saveSearchSettings);
  }

  verifyTabPreset() {
    setTimeout(() => {
      let tab = this.$route.query['tab'];
      if (tab !== undefined) {
        this.tabIndex = +tab;
      }
    }, 100);
  }

  refreshList() {
    this.configurations.searchCriteria = JSON.parse(
      JSON.stringify(
        (this.$refs['searchCriteria'] as any).localConfigurations.searchCriteria
      )
    );
  }

  saveSearchSettings() {
    this.savePendingTags();
    if (!(this.$refs['searchRanking'] as any).isFormValid()) return;
    this.$store.dispatch('saveSearchSettings', this.mapConfigsForSave());
  }

  mapConfigsForSave() {
    this.configurations.searchCriteria = (this.$refs[
      'searchCriteria'
    ] as any).localConfigurations.searchCriteria;
    this.configurations.useReceivedEmailsContent = (this.$refs[
      'searchCriteria'
    ] as any).localConfigurations.useReceivedEmailsContent;
    let date: any = this.configurations.freshnessBeginDate
      ? moment(this.configurations.freshnessBeginDate!).format()
      : null;
    this.configurations.freshness = +this.configurations.freshness!;
    this.configurations.relevanceScore = +this.configurations.relevanceScore!;
    this.configurations.volume = +this.configurations.volume!;
    this.configurations.freshnessBeginDate = date;
    return this.configurations;
  }

  initConfigs() {
    this.configurations = new SearchSettings(
      JSON.parse(JSON.stringify(this.$store.state.settings.searchSettings))
    );
  }

  reinitEmailProperties() {
    this.configurations.freshness = this.$store.state.settings.defaultSearchSettings.freshness!;
    this.configurations.relevanceScore = this.$store.state.settings.defaultSearchSettings.relevanceScore!;
    this.configurations.volume = this.$store.state.settings.defaultSearchSettings.volume!;
    this.configurations.freshnessEnabled = this.$store.state.settings.defaultSearchSettings.freshnessEnabled!;
    this.configurations.relevanceScoreEnabled = this.$store.state.settings.defaultSearchSettings.relevanceScoreEnabled!;
    this.configurations.volumeEnabled = this.$store.state.settings.defaultSearchSettings.volumeEnabled!;
  }

  reinitDomains() {
    this.configurations.freshnessBeginDateEnabled = this.$store.state.settings.defaultSearchSettings.freshnessBeginDateEnabled!;
    this.configurations.freshnessBeginDate = this.$store.state.settings.defaultSearchSettings.freshnessBeginDate!;
    this.configurations.excludedEmailDomains = this.$store.state.settings.defaultSearchSettings.excludedEmailDomains!;
    this.configurations.excludedEmailDomainsEnabled = this.$store.state.settings.defaultSearchSettings.excludedEmailDomainsEnabled!;
    this.configurations.includedEmailDomains = this.$store.state.settings.defaultSearchSettings.includedEmailDomains!;
    this.configurations.includedEmailDomainsEnabled = this.$store.state.settings.defaultSearchSettings.includedEmailDomainsEnabled!;
  }

  reinitConfigsDS() {
    this.configurations.searchCriteria = JSON.parse(
      JSON.stringify(
        this.$store.state.settings.defaultSearchSettings.searchCriteria!
      )
    );
  }

  beforeDestroy() {
    this.$root.$off('saveSearchSettings');
  }

  showInfoMessage() {
    this.$store.commit('SHOW_MESSAGE', {
      type: 0,
      message: `Make sure to click save in order to persist the settings!`
    });
  }

  resetEmailContent() {
    this.showInfoMessage();
    this.configurations.useReceivedEmailsContent = undefined;
    this.configurations.useReceivedEmailsContent = this.$store.state.settings.defaultSearchSettings.useReceivedEmailsContent;
  }

  resetSearchCriteria() {
    this.showInfoMessage();
    this.reinitConfigsDS();
  }

  resetIntValues() {
    this.showInfoMessage();
    this.reinitEmailProperties();
  }

  resetDomains() {
    this.showInfoMessage();
    this.reinitDomains();
  }

  resetDataSourceSettings() {
    this.configurations.dataSourceSettings!.isHRDataMandatory = this.$store.state.settings.defaultSearchSettings.dataSourceSettings?.isHRDataMandatory;
    this.configurations.dataSourceSettings!.dataSourcesPriority = this.$store.state.settings.defaultSearchSettings.dataSourceSettings?.dataSourcesPriority;
    this.showInfoMessage();
  }

  async resetSearchResultsFilters() {
    await this.$store.dispatch(
      'getResetDataSourcesFilters',
      this.configurations.dataSourceSettings!.dataSourcesPriority
    );
    this.configurations.searchResultsFilters!.forEach(
      x =>
        (x.filters = JSON.parse(
          JSON.stringify(
            this.$store.state.settings.defaultSearchSettings.searchResultsFilters.find(
              (y: any) => x.dataSource === y.dataSource
            ).filters
          )
        ))
    );
    this.showInfoMessage();
  }
}
</script>

<style lang="scss">
.settings-wrapper {
  .settings-content {
    height: 100%;
    overflow: hidden;
    .tabs {
      height: 100%;
      width: 100%;
      .nav-pills {
        min-width: 220px;
        border-right: 1px solid $main-border-color;
        padding: 0px;
        .nav-item {
          a {
            color: black;
            background-color: transparent;
            border-left: 4px solid transparent;
            border-radius: 0px;
            height: 46px;
            line-height: 46px;
            padding: 0px 12px;
            svg {
              margin: 0px 8px;
            }
            &.active {
              background-color: #eeeeee;
              border-left: 4px solid $primary;
            }
            &:focus {
              outline: none !important;
            }
            &:hover {
              background-color: #e2e2e2;
            }
          }
        }
      }
      .tab-content {
        height: 100%;
        overflow-y: auto;
        .tab-pane {
          > div {
            width: 100%;
          }
        }
        .cont-wrapper {
          padding: 0px;
          fieldset {
            .form-text {
              padding-left: 26px;
              &:focus {
                outline: none;
              }
            }
          }
          .field-row {
            padding: 10px 0px;
            height: 74px;
            .col-6 {
              display: flex;
              flex-direction: column;
              justify-content: center;
              fieldset {
                margin-bottom: 0px;
              }
            }
            .custom-tags-input {
              ul {
                margin-top: -2px;
                width: calc(100% - 22px);
              }
            }
            .info-icon {
              position: absolute;
              top: 10px;
              right: 10px;
              color: $primary;
            }
            .custom-checkbox {
              &.long-text {
                label {
                  display: flex;
                  flex-direction: column;
                  &::before,
                  &::after {
                    margin-top: 9px;
                  }
                }
              }
            }
            .b-form-datepicker {
              .dropdown-menu {
                top: -386px !important;
                transform: none !important;
                left: auto !important;
                right: 0px !important;
              }
            }
          }
        }
      }
    }
  }
}

@media screen and (max-width: 1024px) {
  .settings-wrapper {
    .settings-content {
      .tabs {
        flex-direction: column;
        .col-auto {
          position: absolute;
          display: none;
          width: 100%;
          z-index: 1;
          border-bottom: 1px solid $main-border-color;
          .nav-pills {
            border-right: none;
            a {
              background-color: white;
              &.active {
                background-color: white;
                border-color: transparent;
              }
            }
          }
        }
        .tab-content {
          overflow: visible;
          .tab-pane {
            padding: 0px;
            h5 {
              height: 56px;
              background-color: #fcfbfa;
              line-height: 56px;
              text-align: center;
              border-bottom: 1px solid $main-border-color;
            }
            .cont-wrapper {
              max-width: 100%;
              padding: var(--main-padding);
            }
          }
        }
      }
    }
    .backdrop {
      left: 0;
      top: 132px;
    }
  }
}
</style>
