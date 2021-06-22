<template>
  <span class="current-timezone-wrapper">
    <span class="label">Groups Timezone</span>
    <b-dropdown
      right
      variant="outline-secondary"
      class="min-140 default-dropdown dark"
      :text="text"
    >
      <span
        class="dropdown-item-row"
        v-for="(option, index) in availableFilterTimezone"
        :key="`option_timezone_${index}`"
      >
        <span class="icon-holder">
          <b-form-checkbox
            v-model="selectedFilterTimezone"
            :value="option.value"
            class="custom-checkbox"
          >
          </b-form-checkbox>
        </span>
        <!-- <span class="zone border rounded"> {{ option.zone }} </span> -->
        <span class="text-secondary title text-capitalize">{{ option.name }} </span>
        <span class="text-secondary offset border rounded">
          {{ option.value | gmtOffset }}</span
        >
      </span>
    </b-dropdown>
  </span>
</template>

<script lang="ts">
import { GroupsStore, timezones } from '@/store/modules/groups.store';
import { Component, Vue, Watch } from 'vue-property-decorator';

@Component({
  components: {},
  data() {
    return {
      timezones
    };
  }
})
export default class CurrentTimezoneDropdown extends Vue {
  static loaded = false;

  @Watch('selectedFilterTimezone')
  selectedFilterTimezoneChanged() {
    if (CurrentTimezoneDropdown.loaded) this.$root.$emit('timezone-changed');
    else CurrentTimezoneDropdown.loaded = true;
  }

  get availableFilterTimezone() {
    return GroupsStore.availableTimezones;
  }

  get selectedFilterTimezone() {
    return GroupsStore.selectedFilterTimezone;
  }

  set selectedFilterTimezone(value: any) {
    GroupsStore.setSelectedFilterTimezone(value);
  }

  get text() {
    return `${this.selectedFilterTimezone.length} ${
      this.selectedFilterTimezone.length === 1 ? 'Timezone' : 'Timezones'
    }`;
  }

}
</script>

<style lang="scss">
.current-timezone-wrapper {
  display: flex;
  align-items: center;
  .label {
    font-size: 14px;
    margin-right: 5px;
  }
  .dropdown-menu {
    max-height: 300px;
    overflow: auto;
    .dropdown-item-row {
      min-width: 200px;
      padding: 8px 12px;
      font-weight: 300;
      cursor: initial;
      span {
        &.icon-holder {
          width: 21px;
          display: flex;
          align-items: center;
          justify-content: center;
          svg {
            fill: $success;
          }
        }
        &.zone {
          margin: 0px 10px;
          width: 40px;
          text-align: center;
        }
        &.title {
          width: 260px;
          font-size: 14px;
        }
        &.offset {
          min-width: 90px;
          text-align: center;
        }
      }
    }
  }
}
</style>
