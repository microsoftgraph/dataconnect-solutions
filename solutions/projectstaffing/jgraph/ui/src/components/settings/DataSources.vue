<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <div class="data-sources">
    <h5 class="text-sb mb-3">Data Sources</h5>
    <b-row no-gutters class="columns-holder">
      <b-col class="cont-wrapper pr-3" cols="6">
        <h6 class="text-sb">Data Sources Priority</h6>
        <b-row no-gutters class="text-secondary mt-2"
          >Choose order in which data sources are used while building the
          employee profile (for common fields that are empty in the primary data
          source, the value is retrieved from the fallback source, e.g. if the
          City, State and Country are empty in M365, then we use the value of
          Location form the HR Data). The order also determines which fields are
          available by default for search results filtering.</b-row
        >
        <b-row
          no-gutters
          class="drag-drop-wrapper"
          v-if="configurations.dataSourceSettings"
        >
          <draggable
            tag="ul"
            v-model="configurations.dataSourceSettings.dataSourcesPriority"
            v-bind="dragOptionsSources"
            @start="drag = true"
            @end="drag = false"
            handle=".handle"
          >
            <transition-group
              type="transition"
              :name="!drag ? 'flip-list' : null"
            >
              <li
                v-for="element in configurations.dataSourceSettings
                  .dataSourcesPriority"
                :key="element"
              >
                {{
                  constants.settings.facets[element]
                    ? constants.settings.facets[element]
                    : element
                }}
                <b-icon-list class="handle"></b-icon-list>
              </li>
            </transition-group>
          </draggable>
        </b-row>

        <b-row no-gutters v-if="configurations.dataSourceSettings">
          <b-col class="d-flex flex-column">
            <b-form-checkbox
              v-model="configurations.dataSourceSettings.isHRDataMandatory"
              class="custom-checkbox hr-checkbox"
            >
              <span>
                Is HR Data Mandatory
              </span>
              <span class="text-secondary d-block">
                If HR Data is considered mandatory, employees that only appear
                in M365 will be ignored
              </span>
            </b-form-checkbox>
          </b-col>
        </b-row>
        <b-button
          class="default-size mt-3"
          variant="outline-secondary"
          @click="resetDataSourceSettings"
          >Reset</b-button
        >
      </b-col>
      <b-col class="cont-wrapper pl-3" cols="6">
        <h6 class="text-sb">Search Results Filtering</h6>
        <b-row no-gutters class="text-secondary mt-2"
          >Choose which employee profile fields are exposed as filters in the
          search menu.
          <br />
          Selected fields with no values will be omitted.
        </b-row>
        <b-row
          no-gutters
          class="drag-drop-wrapper"
          v-for="sourceFilters in configurations.searchResultsFilters"
          :key="sourceFilters.dataSource"
        >
          <span class="mt-3 text-sb text-secondary">
            {{
              constants.settings.facets[sourceFilters.dataSource]
                ? constants.settings.facets[sourceFilters.dataSource]
                : sourceFilters.dataSource
            }}</span
          >
          <draggable
            tag="ul"
            v-model="sourceFilters.filters"
            v-bind="dragOptionsFilters"
            @start="drag = true"
            @end="drag = false"
            handle=".handle"
          >
            <transition-group
              type="transition"
              :name="!drag ? 'flip-list' : null"
            >
              <li
                v-for="element in sourceFilters.filters"
                :key="`${sourceFilters.dataSource}_${element.filterType}`"
              >
                <b-form-checkbox
                  v-model="element.isActive"
                  class="custom-checkbox"
                >
                  {{
                    constants.settings.facets[element.filterType]
                      ? constants.settings.facets[element.filterType]
                          .settingsLabel
                        ? constants.settings.facets[element.filterType]
                            .settingsLabel
                        : constants.settings.facets[element.filterType]
                      : element.filterType
                  }}
                </b-form-checkbox>
                <b-icon-list class="handle"></b-icon-list>
              </li>
            </transition-group>
          </draggable>
        </b-row>
        <b-button
          class="default-size mt-3"
          variant="outline-secondary"
          @click="resetSearchResultsFilters"
          >Reset</b-button
        >
      </b-col>
    </b-row>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Emit } from 'vue-property-decorator';
import draggable from 'vuedraggable';
import { constants } from '@/helpers/constants';

@Component({
  components: { draggable },
  data() {
    return {
      constants
    };
  }
})
export default class DataSources extends Vue {
  drag = false;
  dragOptionsSources = {
    name:'dragSources',
    animation: 200,
    disabled: false,
    ghostClass: 'ghost'
  };

  dragOptionsFilters = {
    name:'dragFilters',
    animation: 200,
    disabled: false,
    ghostClass: 'ghost'
  };


  @Prop({
    default: () => {
      return {
        dataSourceSettings: [
          {
            dataSourcesPriority: [],
            isHRDataMandatory: false
          }
        ]
      };
    }
  })
  configurations: any;

  @Emit('resetDataSourceSettings')
  resetDataSourceSettings() {}

  @Emit('resetSearchResultsFilters')
  resetSearchResultsFilters() {}
}
</script>

<style lang="scss">
.data-sources {
  .drag-drop-wrapper {
    ul {
      margin-top: 10px;
    }
  }
  .hr-checkbox {
    .custom-control-label {
      &::before,
      &::after {
        margin-top: 7px;
      }
      display: flex;
      flex-direction: column;
    }
  }
}
@media screen and (max-width: 1024px) {
  .data-sources {
    .columns-holder {
      flex-direction: column;
      .cont-wrapper {
        padding: var(--main-padding) !important;
      }
    }
  }
}
</style>
