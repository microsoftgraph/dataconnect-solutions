<template>
  <div class="hr-data">
    <h5 class="text-sb mb-3">Upload HR Data</h5>
    <b-alert v-if="fileUploadOngoing" show variant="primary">
      <b-icon-info-circle-fill
        variant="primary"
        class="mr-2"
      ></b-icon-info-circle-fill>
      <span
        >This functionality is disabled while the data from the previous file upload is processed.
      </span></b-alert
    >
    <b-row no-gutters class="text-secondary mt-2">Upload HR Data csv...</b-row>
    <div class="gdc-custom-file-input mt-2">
      <div class="label">
        <span class="text">{{
          file ? file.name : 'Choose a file or drop it here...'
        }}</span>
        <span class="browse-btn">Browse</span>
      </div>
      <input
        :class="{ 'not-allowed': fileUploadOngoing }"
        ref="file"
        type="file"
        accept=".csv"
        :disabled="fileUploadOngoing"
        @change="fileChanged"
      />
    </div>
    <b-button
      class="default-size mt-3"
      variant="primary"
      @click="upload"
      :disabled="file === null"
      >Upload</b-button
    >
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class HRData extends Vue {
  file: any = null;

  get fileUploadOngoing() {
    return !['employee_pipeline_run_finished', 'error'].includes(
      this.$store.state.fileUpload.fileUploadState
    );
  }

  fileChanged() {
    const file = (this.$refs.file as any).files[0];
    if (file)
      if (file.type === 'text/csv') {
        this.file = file;
      } else {
        (this.$refs.file as any).value = null;
        this.file = null;
        this.$store.commit('SHOW_MESSAGE', {
          type: 1,
          message: `File type must be .csv`
        });
      }
  }

  async upload() {
    let response = await this.$store.dispatch('uploadHRDataCSV', this.file);
    if (response) {
      this.file = null;
      (this.$refs.file as any).value = null;
    }
  }
}
</script>

<style lang="scss">
.hr-data {
}
</style>
