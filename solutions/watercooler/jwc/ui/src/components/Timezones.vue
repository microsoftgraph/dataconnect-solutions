<template>
  <b-dropdown class="timezones-wrapper default-dropdown light" right>
    <template v-slot:button-content>
      <div>Timezone: {{ selectedValue.offset | gmtOffset }}</div>
    </template>
    <b-dropdown-item
      class="dropdown-item-row"
      v-for="(timezone, index) in timezones"
      :key="`timezone_${index}`"
      @click="selectTimezone(timezone)"
    >
      <span class="icon-holder">
        <b-icon-check-circle
          v-if="selectedValue.offset === timezone.offset"
        ></b-icon-check-circle>
      </span>
      <!-- <span class="zone border rounded"> {{ timezone.zone }} </span> -->
      <!-- <span class="text-secondary title">{{ timezone.title }} </span> -->
      <span class="text-secondary offset border rounded">
        {{ timezone.offset | gmtOffset }}</span
      >
    </b-dropdown-item>
  </b-dropdown>
</template>

<script lang="ts">
// eslint-disable-next-line no-unused-vars
import { GroupsStore, timezones, Timezone } from '@/store/modules/groups.store';
import { Component, Vue } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class Timezones extends Vue {
  get timezones() {
    const tz = timezones;
    return tz;
  }

  get selectedValue() {
    return GroupsStore.selectedTimezone;
  }

  selectTimezone(timezone: Timezone) {
    GroupsStore.setTimezone(timezone);
    this.$root.$emit('timezone-changed');
  }

  mounted() {}
}
</script>

<style lang="scss">
.timezones-wrapper {
  padding: 0px 16px;
  ul {
    max-height: 300px;
    overflow-y: auto;
    margin-top: 9px;
    left: inherit !important;
    .dropdown-item-row {
      a {
        display: flex;
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
          }
          &.offset {
            min-width: 90px;
            text-align: center;
          }
        }
      }
    }
  }
}
</style>
