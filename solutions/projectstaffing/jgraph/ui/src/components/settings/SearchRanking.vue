<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <div class="search-ranking">
    <h5 class="text-sb mb-3">Emails Search</h5>
    <b-col class=" cont-wrapper" cols="12">
      <h6 class="text-sb">Employee Ranking</h6>
      <b-row no-gutters class="text-secondary mt-2"
        >Define how the attributes of sent (and received ) emails influence
        ranking of employees in search results.</b-row
      >
      <b-row
        class="mt-4 border-top border-bottom text-sb text-secondary pt-2 pb-2"
        no-gutters
      >
        <b-col cols="6"> Attribute</b-col>
        <b-col cols="6"> Weight</b-col>
      </b-row>
      <b-row
        class=" border-bottom field-row"
        no-gutters
        v-for="intVal in intValues"
        :key="intVal.key"
      >
        <b-col cols="6" class="d-flex flex-column">
          <b-form-checkbox
            v-model="configurations[intVal.key + 'Enabled']"
            class="custom-checkbox long-text"
          >
            <span>
              {{ intVal.label }}
            </span>
            <span class="text-secondary">
              {{ intVal.info }}
            </span>
          </b-form-checkbox>
        </b-col>
        <b-col cols="6">
          <b-form-group>
            <b-form-input
              type="number"
              v-model="$v.configurations[intVal.key].$model"
              :state="
                validateState(
                  intVal.key,
                  configurations[intVal.key + 'Enabled']
                )
              "
              :disabled="!configurations[intVal.key + 'Enabled']"
            ></b-form-input>
            <b-form-invalid-feedback
              v-if="
                !$v.configurations[intVal.key].required &&
                  configurations[intVal.key + 'Enabled']
              "
              >{{ intVal.label }} must not be empty.</b-form-invalid-feedback
            >
            <b-form-invalid-feedback
              v-if="
                (!$v.configurations[intVal.key].minValue ||
                  !$v.configurations[intVal.key].maxValue) &&
                  configurations[intVal.key + 'Enabled'] &&
                  $v.configurations[intVal.key].required
              "
              >{{ intVal.label }} must be between 0 and
              100.</b-form-invalid-feedback
            >
          </b-form-group>
        </b-col>
      </b-row>
      <b-button
        class="default-size mt-3"
        variant="outline-secondary"
        @click="resetIntValues"
        >Reset</b-button
      >

      <h6 class="mt-4 text-sb">Emails Filters</h6>
      <b-row no-gutters class="text-secondary mt-2"
        >Configure which emails get used for employee search</b-row
      >
      <b-row
        class="mt-4 border-top border-bottom field-row align-items-center d-flex flex-row"
        no-gutters
      >
        <b-col cols="6" class="">
          <b-form-checkbox
            v-model="configurations.freshnessBeginDateEnabled"
            class="custom-checkbox"
          >
            <span>
              Process emails newer than
            </span>
          </b-form-checkbox>
        </b-col>
        <b-col cols="6">
          <b-form-datepicker
            reset-button
            :date-format-options="{
              year: 'numeric',
              month: 'numeric',
              day: 'numeric'
            }"
            :disabled="!configurations.freshnessBeginDateEnabled"
            v-model="configurations.freshnessBeginDate"
          >
            <template v-slot:button-content>
              <b-icon-calendar3></b-icon-calendar3>
            </template>
          </b-form-datepicker>
        </b-col>
      </b-row>
      <b-row
        class=" border-bottom field-row h-auto align-items-center d-flex flex-row"
        no-gutters
      >
        <b-col cols="6" class="">
          <b-form-checkbox
            v-model="configurations.includedEmailDomainsEnabled"
            class="custom-checkbox"
          >
            <span>
              Include emails from domains
            </span>
          </b-form-checkbox>
        </b-col>
        <b-col cols="6">
          <b-form-group>
            <b-form-tags
              input-id="includedDomains"
              class="custom-tags-input default-size-form"
              ref="includedDomains"
              tag-class="custom-tags"
              add-button-variant="outline-primary"
              add-button-text="+"
              v-model="configurations.includedEmailDomains"
              :disabled="!configurations.includedEmailDomainsEnabled"
              placeholder="Enter domains"
              :input-attrs="{ autocomplete: 'off' }"
              remove-on-delete
              :tag-validator="tagValidatorIncluded"
              :invalid-tag-text="includedInvalidText"
            >
            </b-form-tags>
          </b-form-group>
          <b-icon-info-circle
            v-if="configurations.includedEmailDomainsEnabled"
            :title="validDomains"
            v-b-tooltip.hover
            class="info-icon"
          ></b-icon-info-circle>
        </b-col>
      </b-row>
      <b-row
        class="border-bottom field-row h-auto align-items-center d-flex flex-row"
        no-gutters
      >
        <b-col cols="6" class="">
          <b-form-checkbox
            v-model="configurations.excludedEmailDomainsEnabled"
            class="custom-checkbox"
          >
            <span>
              Exclude emails from domains
            </span>
          </b-form-checkbox>
        </b-col>
        <b-col cols="6">
          <b-form-group>
            <b-form-tags
              input-id="excludedDomains"
              class="custom-tags-input default-size-form"
              ref="excludedDomains"
              tag-class="custom-tags"
              add-button-variant="outline-primary"
              add-button-text="+"
              placeholder="Enter domains"
              :disabled="!configurations.excludedEmailDomainsEnabled"
              v-model="configurations.excludedEmailDomains"
              remove-on-delete
              :input-attrs="{ autocomplete: 'off' }"
              :tag-validator="tagValidatorExcluded"
              :invalid-tag-text="excludedInvalidText"
            >
            </b-form-tags>
          </b-form-group>
          <b-icon-info-circle
            v-if="configurations.excludedEmailDomainsEnabled"
            :title="validDomains"
            v-b-tooltip.hover
            class="info-icon"
          ></b-icon-info-circle>
        </b-col>
      </b-row>
      <b-button
        class="default-size mt-3"
        variant="outline-secondary"
        @click="resetDomains"
        >Reset</b-button
      >
    </b-col>
  </div>
</template>

<script lang="ts">
import { Component, Emit, Prop, Vue } from 'vue-property-decorator';
import draggable from 'vuedraggable';
import { maxValue, minValue, required } from 'vuelidate/lib/validators';

Component.registerHooks(['validations']);

@Component({
  components: {
    draggable
  }
})
export default class SearchRanking extends Vue {
  validDomains = `Domain should match the following format: example.com and it may contain: uppercase letters, lowercase letters, spaces, numbers and these special characters ,.;- `;
  excludedInvalidText = '';
  includedInvalidText = '';
  intValues = [
    {
      label: 'Volume',
      info: 'Faceting',
      key: 'volume'
    },
    {
      label: 'Freshness',
      info: 'Recent communication',
      key: 'freshness'
    },
    {
      label: 'Relevance Score',
      info: 'TF/IDF',
      key: 'relevanceScore'
    }
  ];

  @Prop({
    default: () => {
      return {};
    }
  })
  configurations: any;

  validations() {
    return {
      configurations: {
        freshness: {
          required,
          minValue: minValue(0),
          maxValue: maxValue(100)
        },
        volume: {
          required,
          minValue: minValue(0),
          maxValue: maxValue(100)
        },
        relevanceScore: {
          required,
          minValue: minValue(0),
          maxValue: maxValue(100)
        }
      }
    };
  }

  validateState(name: string, enabled: boolean) {
    if (this.$v.configurations[name] && enabled) {
      const $dirty = this.$v.configurations[name]?.$dirty;
      const $error = this.$v.configurations[name]?.$error;
      return $dirty ? !$error : null;
    }
  }

  @Emit('resetDomains')
  resetDomains() {}

  @Emit('resetIntValues')
  resetIntValues() {}

  mounted() {}

  tagValidatorIncluded(tag: string) {
    if (
      this.configurations.includedEmailDomains!.find(
        (t: any) => t.toLowerCase() === tag.toLowerCase()
      ) !== undefined
    )
      this.includedInvalidText = 'Duplicate tag(s)';

    if (!/^[a-zA-Z0-9 ,.;-]*$/.test(tag))
      this.includedInvalidText = 'Invalid tag(s)';

    return (
      /^[a-zA-Z0-9 ,.;-]*$/.test(tag) &&
      this.configurations.includedEmailDomains!.find(
        (t: string) => t.toLowerCase() === tag.toLowerCase()
      ) === undefined
    );
  }

  tagValidatorExcluded(tag: string) {
    if (
      this.configurations.excludedEmailDomains!.find(
        (t: any) => t.toLowerCase() === tag.toLowerCase()
      ) !== undefined
    )
      this.excludedInvalidText = 'Duplicate tag(s)';

    if (!/^[a-zA-Z0-9 ,.;-]*$/.test(tag))
      this.excludedInvalidText = 'Invalid tag(s)';

    return (
      /^[a-zA-Z0-9 ,.;-]*$/.test(tag) &&
      this.configurations.excludedEmailDomains!.find(
        (t: string) => t.toLowerCase() === tag.toLowerCase()
      ) === undefined
    );
  }

  validateForm() {
    let errors = true;
    this.$v.configurations.$touch();
    if (this.configurations.freshnessEnabled)
      errors = errors && !this.$v.configurations.freshness!.$error;
    if (this.configurations.relevanceScoreEnabled)
      errors = errors && !this.$v.configurations.relevanceScore!.$error;
    if (this.configurations.volumeEnabled)
      errors = errors && !this.$v.configurations.volume!.$error;
    return errors;
  }

  isFormValid() {
    this.$v.configurations.$touch();
    return this.validateForm();
  }
}
</script>

<style lang="scss"></style>
