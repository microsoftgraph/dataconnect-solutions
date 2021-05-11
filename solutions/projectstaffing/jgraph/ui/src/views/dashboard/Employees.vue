<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <div v-if="false"></div>
</template>
<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class Employees extends Vue {
  mounted() {
    this.verifyEmployeeModal();
  }

  verifyEmployeeModal() {
    if (this.$route.name === 'Employees' && this.$route.params.id) {
      this.$store.commit('SET_SELECTED_EMPLOYEE_ID', this.$route.params.id);
      this.$bvModal.show('user-profile-modal');
    }
  }
}
</script>

<style lang="scss"></style>
