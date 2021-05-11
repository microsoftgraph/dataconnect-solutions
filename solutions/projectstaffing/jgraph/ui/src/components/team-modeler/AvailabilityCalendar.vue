<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <div>
    <span class="text-sb">Available At</span>
    <b-form-datepicker
      id="availabilityCalendarToggler"
      :min="minDate"
      :date-format-options="{
        year: 'numeric',
        month: 'numeric',
        day: 'numeric'
      }"
      v-model="date"
      class="mt-2 availability-calendar"
    >
      <template v-slot:button-content>
        <b-icon-calendar3></b-icon-calendar3>
      </template>
    </b-form-datepicker>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class AvailabilityCalendar extends Vue {
  minDate = new Date();
  
  get date() {
    return this.$store.state.search.date;
  }

  set date(value: string) {
    this.$store.commit('SET_DATE', value);
  }

  mounted() {}
}
</script>

<style lang="scss">
.availability-calendar {
  position: relative;
  &:hover {
    border-color: $primary;
  }
  &:active,
  &:focus-within {
    border-color: $primary;
    outline: 0;
    box-shadow: 0 0 0 0.2rem #007bff40;
  }
  .dropdown-menu {
    position: fixed !important;
    z-index: 1;
    margin-top: 46px;
    transform: none !important;
    top: auto !important;
    margin-left: 48px;
    background-color: white;
    left: 0;
    border: 1px solid $main-border-color;
    padding: 12px;
    border-radius: 4px;
  }
}

@media screen and (max-width: 1024px) {
  .availability-calendar {
    .dropdown-menu {
      margin-left: 12px;
    }
  }
}
</style>
