<template>
  <div class="dashboard-wrapper">
    <Navbar />
    <div class="router-wrapper">
      <router-view />
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Navbar from '@/components/Navbar.vue';

@Component({
  components: { Navbar }
})
export default class Dashboard extends Vue {}
</script>

<style lang="scss">
.dashboard-wrapper {
  display: flex;
  flex-direction: column;
  background-color: #f7fafc;
  height: 100vh;
  max-width: 100vw;
  width: 100%;
  overflow: hidden;
  .router-wrapper {
    flex: 1;
    min-height: 0;
    display: flex;
    width: 100%;
  }
}
@media screen and (max-width: 1024px) {
  .router-wrapper {
    margin-top: 56px;
    .right-content-wrapper {
      overflow: visible;
    }
  }
}
</style>
