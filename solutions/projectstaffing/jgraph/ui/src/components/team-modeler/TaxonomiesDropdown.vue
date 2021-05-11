<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <b-dropdown
    v-if="$store.state.team.selectedSearchType === 'SKILLS'"
    class="custom-dropdown tax-dropdown default-size"
    right
    :text="
      `${selectedTaxonomies.length} ${
        selectedTaxonomies.length === 1 ? 'Domain' : 'Domains'
      }`
    "
    variant="outline-secondary"
  >
    <div class="clear-select-group">
      <b-button-group>
        <b-button variant="outline-secondary" @click="selectAll"
          >Select All</b-button
        >
        <b-button variant="outline-secondary" @click="clearAll"
          >Clear All</b-button
        >
      </b-button-group>
    </div>
    <div class="options">
      <div
        class="custom-row"
        v-for="(tax, key) in taxonomies"
        :key="`taxonomies_${tax}`"
      >
        <b-form-checkbox
          v-model="selectedTaxonomies"
          name="flavour"
          :value="key"
          >{{ tax }}</b-form-checkbox
        >
      </div>
    </div>
  </b-dropdown>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class TaxonomiesDropdown extends Vue {
  get taxonomies() {
    return this.$store.state.team.taxonomies;
  }

  get selectedTaxonomies() {
    return this.$store.state.team.selectedTaxonomies;
  }

  set selectedTaxonomies(value: any) {
    this.$store.commit('SET_SELECTED_TAXONOMIES', value);
  }

  selectAll() {
    this.$store.commit(
      'SET_SELECTED_TAXONOMIES',
      Object.keys(this.taxonomies).map((x: any) => x)
    );
  }
  clearAll() {
    this.$store.commit('SET_SELECTED_TAXONOMIES', []);
  }
}
</script>

<style lang="scss">
.tax-dropdown {
  .dropdown-toggle {
    border-top-left-radius: 0px;
    border-bottom-left-radius: 0px;
    margin-top: 7px;
    height: calc(100% - 14px);
  }
  ul {
    padding: 0px;
    .clear-select-group {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 16px 10px;
      margin-bottom: 8px;
      border-bottom: 1px solid $main-border-color;
      .btn-group {
        flex: 1;
        button {
          text-align: center;
          padding: 0px;
          width: 100px;
          min-width: 100px;
        }
      }
    }
    .options {
      max-height: 240px;
      overflow-x: auto;
    }
  }
}
</style>
