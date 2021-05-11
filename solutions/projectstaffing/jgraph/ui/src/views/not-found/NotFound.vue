<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <div>
    <span>Page not found! You will be redirected to home page.</span>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class NotFound extends Vue {
  mounted() {
    setTimeout(() => {
      this.$router.push('/');
    }, 5000);
  }
}
</script>

<style></style>
