<template>
  <div class="search-criteria">
    <h5 class="text-sb mb-3">Search Criteria</h5>

    <b-col class="cont-wrapper" cols="12">
      <b-row no-gutters class="text-secondary mt-2"
        >Select which criteria are used to search for relevant employees. Drag
        to determine the order in which they are considered.</b-row
      >
      <b-row no-gutters class="drag-drop-wrapper">
        <draggable
          tag="ul"
          v-model="localConfigurations.searchCriteria"
          v-bind="dragOptions"
          @start="drag = true"
          @end="
            drag = false;
            refreshList();
          "
          handle=".handle"
        >
          <transition-group
            type="transition"
            :name="!drag ? 'flip-list' : null"
          >
            <li
              v-for="element in localConfigurations.searchCriteria"
              :key="element.searchCriterionType"
            >
              <b-form-checkbox
                v-model="element.isActive"
                class="custom-checkbox"
              >
                {{
                  $store.getters.getSearchSettingsLegend[
                    element.searchCriterionType
                  ]
                }}
              </b-form-checkbox>
              <b-icon-list class="handle"></b-icon-list>
            </li>
          </transition-group>
        </draggable>
      </b-row>

      <b-button
        class="default-size"
        variant="outline-secondary"
        @click="resetSearchCriteria"
        >Reset</b-button
      >
    </b-col>
    <b-col class="cont-wrapper" cols="12">
      <h6 class="text-sb mt-4 mb-3">Search Settings</h6>
      <b-row no-gutters class="field-row include-emails">
        <b-col class="d-flex flex-column">
          <b-form-checkbox
            v-if="useReceivedEmailsContentEnabled"
            v-model="localConfigurations.useReceivedEmailsContent"
            class="custom-checkbox"
          >
            <span>
              Include Received Emails Content
            </span>
            <span class="text-secondary">
              Also consider received emails as part of the “Emails Content” search criterion
            </span>
          </b-form-checkbox>
          <b-form-checkbox v-else class="custom-checkbox" disabled>
            <span>
              Include Received Emails Content
            </span>
            <span class="text-secondary">
              Unavailable because "Emails Content" search criterion is disabled
            </span>
          </b-form-checkbox>
        </b-col>
      </b-row>

      <b-button
        class="default-size"
        variant="outline-secondary"
        :disabled="!useReceivedEmailsContentEnabled"
        @click="resetEmailContent"
        >Reset</b-button
      >
    </b-col>
  </div>
</template>

<script lang="ts">
import { Component, Emit, Prop, Vue, Watch } from 'vue-property-decorator';
import draggable from 'vuedraggable';
@Component({
  components: {
    draggable
  }
})
export default class SearchCriteria extends Vue {
  drag = false;
  dragOptions = {
    animation: 200,
    group: 'description',
    disabled: false,
    ghostClass: 'ghost'
  };
  // Use local configurations because of useReceivedEmailsContent to record last value even if disabled
  localConfigurations = {
    useReceivedEmailsContent: true,
    searchCriteria: []
  };
  isResetSearchCriteria = false;
  isResetEmailContent = false;
  init = false;

  get useReceivedEmailsContentEnabled() {
    const ds: any = this.localConfigurations.searchCriteria.find(
      (x: any) => x.searchCriterionType === 'EMAIL_CONTENT'
    );
    return ds ? ds.isActive : false;
  }

  @Prop({
    default: () => {
      return {
        useReceivedEmailsContent: true,
        searchCriteria: []
      };
    }
  })
  configurations: any;

  @Emit('resetSearchCriteria')
  resetSearchCriteria() {
    this.isResetSearchCriteria = true;
  }

  @Emit('resetEmailContent')
  resetEmailContent() {
    this.isResetEmailContent = true;
  }

  @Emit('refreshList')
  refreshList() {
    this.isResetSearchCriteria = true;
  }

  @Watch('configurations', { deep: true })
  onConfigurationsChange() {
    if (!this.init) this.initConfigurations();
    else {
      if (this.isResetSearchCriteria) this.initDataSources();
      if (this.isResetEmailContent) this.initEmailContent();
    }
  }

  mounted() {}

  initConfigurations() {
    this.localConfigurations = JSON.parse(JSON.stringify(this.configurations));
    this.init = true;
  }

  initDataSources() {
    this.localConfigurations.searchCriteria = JSON.parse(
      JSON.stringify(this.configurations.searchCriteria)
    );
    this.isResetSearchCriteria = false;
  }

  initEmailContent() {
    this.localConfigurations.useReceivedEmailsContent = this.configurations.useReceivedEmailsContent;
    this.isResetEmailContent = false;
  }
}
</script>

<style lang="scss">
.search-criteria {
  .include-emails {
    .custom-control-label {
      display: flex;
      flex-direction: column;
      &::before,
      &::after {
        margin-top: 7px;
      }
    }
  }
}
</style>
