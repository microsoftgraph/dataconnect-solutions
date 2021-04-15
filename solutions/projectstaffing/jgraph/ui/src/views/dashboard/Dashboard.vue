<template>
  <div class="dashboard-wrapper">
    <Navbar></Navbar>
    <div class="content-wrapper">
      <Header />
      <router-view></router-view>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Navbar from '@/components/Navbar.vue';
// eslint-disable-next-line no-unused-vars
import { ConfirmDialog } from '@/types/Shared';
import Header from '@/components/Header.vue';

@Component({
  components: {
    Navbar,
    Header
  }
})
export default class Dashboard extends Vue {
  mounted() {
    // const params: ConfirmDialog = {
    //   modal: this.$bvModal,
    //   callback: this.deleteCallback.bind(this),
    //   title: 'Confirm delete',
    //   text: `Are you sure you want to delete ... ?`
    // };
    // this.$store.dispatch('showConfirmDialog', params);
  }

  deleteCallback() {}
}
</script>

<style lang="scss">
.dashboard-wrapper {
  border-radius: $main-border-radius;
  box-shadow: 0 0 20px 0px #000000;
  margin: 24px;
  background-color: white;
  overflow: hidden;
}

@media screen and (max-width: 1024px) {
  .dashboard-wrapper {
    margin: 0px;
    box-shadow: none;
    overflow: visible;
  }
}
</style>
