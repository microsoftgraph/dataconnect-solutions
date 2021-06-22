<template>
  <b-modal id="about-modal" dialog-class="default-modal" centered scrollable>
    <template v-slot:modal-header="{ close }">
      <span class="title">About</span>
      <b-icon-x @click="close"> </b-icon-x>
    </template>

    <b-col class="p-0" cols="6">
      <img src="@/assets/logo.png" />
    </b-col>
    <b-col class="p-0" cols="6">
      <b-row
        v-for="field in fields"
        :key="`about_modal_${field.key}`"
        no-gutters
        class="mb-3 d-flex flex-column"
      >
        <div class="text-sb">{{ field.label }}</div>
        <div v-if="field.type !== 'date'">
          {{ about[field.key] || field.fallback }}
        </div>
        <div v-else>
          <span
            v-if="
              about[field.key] && new Date(about[field.key]).toJSON() !== null
            "
          >
            {{ about[field.key] | moment('MMMM DD, YYYY') }}
          </span>
          <span v-else> {{ field.fallback }}</span>
        </div>
      </b-row>
    </b-col>
    <template v-slot:modal-footer>
      <b-button variant="primary" class="default-size" @click="close">
        Close
      </b-button>
    </template>
  </b-modal>
</template>

<script lang="ts">
import { SharedStore } from '@/store/modules/shared.store';
import { Component, Vue } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class AboutModal extends Vue {
  id = 'about-modal';
  isNewVersionAvailable = false;
  fields = [
    {
      label: 'Version',
      key: 'version',
      fallback: '-'
    },
    {
      label: 'Build',
      key: 'dockerTag',
      fallback: '-'
    },
    {
      label: 'Release Date',
      key: 'builtAt',
      type: 'date',
      fallback: '-'
    }
  ];

  get about() {
    return SharedStore.about;
  }

  mounted() {
    SharedStore.getAbout();
  }

  close() {
    this.$bvModal.hide(this.id);
  }

  open() {
    this.$bvModal.show(this.id);
  }
}
</script>

<style lang="scss">
#about-modal {
  .modal-dialog {
    max-width: 360px;
    .modal-body {
      padding: 20px var(--main-padding);
      display: flex;
      img {
        max-width: 100%;
        object-fit: contain;
      }
    }
  }
}
</style>
