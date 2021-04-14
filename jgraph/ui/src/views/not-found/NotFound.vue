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
