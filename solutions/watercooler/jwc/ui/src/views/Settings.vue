<template>
  <div class="settings-wrapper">
    <div class="header border-bottom">
      <span>Settings</span>
      <b-button variant="primary" class="ml-auto default-size" @click="save"
        >Save</b-button
      >
    </div>
    <div class="content">
      <b-card class="mb-4">
        <span class="title mb-3">Groups Creation Criteria</span>
        <RangeInput
          :val="settings.clustering.role_similarity"
          :label="'Role Similarity'"
          @update:val="settings.clustering.role_similarity = $event"
        />
        <RangeInput
          :val="settings.clustering.skill_match"
          :label="'Skills Match'"
          @update:val="settings.clustering.skill_match = $event"
        />
        <RangeInput
          :val="settings.clustering.location_match"
          :label="'Location Match'"
          @update:val="settings.clustering.location_match = $event"
        />
        <RangeInput
          :val="settings.clustering.randomness"
          :label="'Randomness'"
          @update:val="settings.clustering.randomness = $event"
        />
        <span class="title mb-3 mt-4">Core Hours</span>
        <b-form-select
          v-model="settings.core_hours"
          :options="optionsCore"
        ></b-form-select>
        <span class="title mb-3 mt-4">Generate Groups Interval</span>
        <b-form-select
          v-model="settings.groups_time_range"
          :options="optionsGroupsInterval"
        ></b-form-select>
      </b-card>
      <b-card>
        <span class="title mb-3">Import GDC Calendar</span>
        <b-button
          class="default-size"
          variant="primary"
          @click="verifyPermissions"
          v-b-tooltip.hover
          title="Update Atendee Status"
          >Update</b-button
        >
      </b-card>
    </div>
    <RequestPermissionModal :link="permissionConsentLink" />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RangeInput from '@/components/RangeInput.vue';
import { SharedStore } from '@/store/modules/shared.store';
import { AuthStore } from '@/store/modules/auth.store';
import { SettingsStore } from '@/store/modules/settings.store';
import RequestPermissionModal from '@/components/modals/RequestPermissionModal.vue';

@Component({
  components: { RangeInput, RequestPermissionModal }
})
export default class Settings extends Vue {
  permissionConsentLink = '';
  optionsGroupsInterval = [
    { value: 1, text: '1 Day' },
    { value: 2, text: '2 Days' },
    { value: 3, text: '3 Days' },
    { value: 4, text: '4 Days' },
    { value: 5, text: '5 Days' },
    { value: 6, text: '6 Days' },
    { value: 7, text: '1 Week' },
    { value: 14, text: '2 Weeks' },
    { value: 21, text: '3 Weeks' },
    { value: 28, text: '4 Weeks' }
  ];
  optionsCore = [
    { value: '8,16', text: '8AM - 16PM' },
    { value: '9,17', text: '9AM - 17PM' }
  ];

  mounted() {
    console.log(this.settings);
    SettingsStore.getSettings();
    setTimeout(() => {
      console.log(this.settings);
    }, 1000);
  }

  get settings() {
    return SettingsStore.settings;
  }

  save() {
    SettingsStore.saveSettings(this.settings);
  }

  verifyPermissions() {
    AuthStore.verifyPermissions().catch(error => {
      if (
        error.response &&
        error.response.status === 400 &&
        error.response.data &&
        error.response.data.permissionConsentLink
      ) {
        this.permissionConsentLink = `${error.response.data.permissionConsentLink}&state=redir%3D%2Fsettings`;
        this.$bvModal.show('permission-modal');
      } else {
        if (error.response && error.response.status === 409) {
          SharedStore.setMessage({
            type: 1,
            message: 'The update process is already running',
            error: error
          });
        } else
          SharedStore.setMessage({
            type: 1,
            message: 'Update failed',
            error: error
          });
      }
    });
  }
}
</script>

<style lang="scss">
.settings-wrapper {
  flex-direction: column;
  display: flex;
  width: 100%;
  .header {
    font-size: 28px;
    height: 80px;
    background-color: white;
    width: 100%;
    display: flex;
    align-items: center;
    padding: var(--main-padding);
    button {
      min-width: 120px;
    }
  }
  .content {
    padding: var(--main-padding);
    overflow: auto;

    .card {
      border-radius: 8px;
      .card-body {
        flex-direction: column;
        display: flex;
        padding: 24px;
        .title {
          font-size: 18px;
          border-top: 1px solid #dfdfdf;
          padding-top: 12px;
          &:first-child {
            border-top: none;
            padding-top: 0px;
          }
        }
        .custom-select {
          max-width: 356px;
        }
      }
    }
  }
}
</style>
