<template>
  <div class="ingestion-wrapper">
    <span v-if="modeDetails.modeSwitchPhase === 'error'">
      <b-alert show variant="danger">
        <b-icon-x-circle-fill
          variant="danger"
          class="mr-2"
        ></b-icon-x-circle-fill>
        <span>{{ constants.ingestion.settings.failed }}</span>
      </b-alert>
      <span class="d-flex mt-3">
        <b-button
          @click="restart"
          variant="outline-secondary"
          class="default-size mr-2"
          >Restart</b-button
        >
        <b-button
          @click="showDetails"
          variant="outline-secondary"
          class="default-size"
          >Show Details</b-button
        >
      </span>
    </span>
    <span
      v-if="
        modeDetails.modeSwitchPhase !== 'error' &&
          modeDetails.modeSwitchPaused &&
          modeDetails.modeSwitchPhase !== 'completed'
      "
    >
      <b-alert show variant="danger">
        <b-icon-x-circle-fill
          variant="danger"
          class="mr-2"
        ></b-icon-x-circle-fill>
        <span>{{ constants.ingestion.settings.paused }}</span>
      </b-alert>
      <span class="d-flex mt-3">
        <span>
          <b-button
            @click="resume"
            variant="outline-secondary"
            class="default-size mt-2"
            >Resume</b-button
          ></span
        >
      </span>
    </span>

    <b-alert
      show
      variant="primary"
      v-if="
        modeDetails.modeSwitchPhase !== 'completed' &&
          modeDetails.modeSwitchPhase !== 'error' &&
          !modeDetails.modeSwitchPaused
      "
    >
      <b-icon-info-circle-fill
        variant="primary"
        class="mr-2"
      ></b-icon-info-circle-fill>
      <span>{{ constants.ingestion.settings.ongoing }}</span></b-alert
    >
    <span class="input-wrapper mt-3 mb-3">
      <span
        >Ingestion Mode: <strong class="text-sb">{{ mode }}</strong></span
      >
    </span>
    <b-alert class="mb-3" show variant="warning">
      <b-icon-exclamation-triangle-fill
        variant="warning"
        class="mr-2"
      ></b-icon-exclamation-triangle-fill>
      <span>{{ constants.ingestion.settings.warning }} </span>
    </b-alert>
    <div
      class="default-size"
      v-b-tooltip.hover
      :title="
        ingestionModeOngoing
          ? 'The system is processing new data in the background, therefore this functionality is disabled for now.'
          : ''
      "
    >
      <b-button
        @click="openModal"
        variant="outline-secondary"
        class="default-size"
        :disabled="ingestionModeOngoing"
        >Switch</b-button
      >
    </div>
    <IngestionModal />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import IngestionModal from '@/components/modals/IngestionModal.vue';
import { constants } from '@/helpers/constants';

@Component({
  components: { IngestionModal },
  data() {
    return {
      constants
    };
  }
})
export default class Ingestion extends Vue {
  get modeDetails() {
    return this.$store.state.ingestion.ingestionModeDetails;
  }

  get mode() {
    return this.$store.state.ingestion.ingestionModesOptions[
      this.$store.state.ingestion.ingestionModeDetails.ingestionMode
    ];
  }

  get ingestionModeOngoing() {
    return !['completed', 'error'].includes(
      this.$store.state.ingestion.ingestionModeDetails.modeSwitchPhase
    );
  }

  mounted() {
    this.$store.dispatch('getIngestionModeDetails', { showModal: false });
  }

  openModal() {
    this.$bvModal.show('ingestion-modal');
  }

  resume() {
    this.$store.dispatch('resumeIngestionMode');
  }

  restart() {
    this.$store.dispatch('restartIngestionMode');
  }

  showDetails() {
    this.$bvModal.show('error-details-modal');
  }
}
</script>

<style lang="scss">
@media screen and (max-width: 1024px) {
  .ingestion-wrapper {
    padding: var(--main-padding);
  }
}
</style>
