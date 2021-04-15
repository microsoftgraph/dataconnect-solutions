<template>
  <b-modal :id="id" dialog-class="default-modal" centered scrollable>
    <template v-slot:modal-header="{ close }">
      <h5 class="text-sb">Search Filters</h5>
      <b-icon-x @click="close"> </b-icon-x>
    </template>
    <SearchFilters />
    <template v-slot:modal-footer>
      <b-button variant="primary" class="w-100" @click="close">
        Close
      </b-button>
    </template>
  </b-modal>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import SearchFilters from '@/components/team-modeler/SearchFilters.vue';

@Component({
  components: { SearchFilters }
})
export default class SearchFiltersModal extends Vue {
  id = 'search-filters-modal';

  mounted() {
    window.addEventListener('resize', this.resized);
  }

  resized() {
    if (window.innerWidth > 1024) this.close();
  }

  close() {
    this.$bvModal.hide(this.id);
  }

  open() {
    this.$bvModal.show(this.id);
  }

  beforeDestroy() {
    window.removeEventListener('resive', this.resized);
  }
}
</script>

<style lang="scss">
#search-filters-modal {
  // .modal-dialog {
  //   max-width: 360px;
  //   .modal-body {
  //     padding: 20px 24px;
  //     display: flex;
  //   }
  // }
}
</style>
