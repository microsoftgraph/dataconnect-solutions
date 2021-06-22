<template>
  <div>
    <Message />
    <WarningModal />
    <router-view></router-view>
    <div class="spinner" :class="{ 'd-block': showLoader }">
      <Loader />
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import interceptorsSetup from '@/shared/httpInterceptors';
import Loader from '@/components/Loader.vue';
// eslint-disable-next-line no-unused-vars
import { AlertModalData, SharedStore } from './store/modules/shared.store';
import Message from '@/components/Message.vue';
import WarningModal from '@/components/modals/WarningModal.vue';
import moment from 'moment';
import { GroupsStore } from '@/store/modules/groups.store';
import { AuthStore } from './store/modules/auth.store';

interceptorsSetup();

@Component({
  components: { Loader, Message, WarningModal }
})
export default class App extends Vue {
  @Watch('modalData', { deep: true })
  onPropertyChanged(data: AlertModalData) {
    if (data.visible) this.$bvModal.show('warning-modal');
  }

  get modalData() {
    return SharedStore.warningModalData;
  }

  get showLoader() {
    return SharedStore.showLoader;
  }

  get selectedDate() {
    return GroupsStore.selectedDate;
  }

  get selectedWeek() {
    return GroupsStore.selectedWeek;
  }

  set selectedWeek(week: moment.Moment[]) {
    GroupsStore.setWeek(week);
  }

  initSelectedWeek() {
    const start = moment(this.selectedDate).startOf('week');
    const end = moment(this.selectedDate).endOf('week');
    let now = start.clone();
    let week: moment.Moment[] = [];
    // eslint-disable-next-line no-unused-vars
    let index = 0;
    while (now.isSameOrBefore(end)) {
      week.push(now.clone());
      now.add(1, 'days');
      index++;
    }
    this.selectedWeek = week;
  }

  created() {
    AuthStore.getCurrentUser();
    this.initSelectedWeek();
    this.verifyLogout();
    GroupsStore.getAvailableTimezones(this);
  }

  verifyLogout() {
    // Custom Logout logic for Azure App Services loading page from cache after logout
    if (localStorage.getItem('logout') && !location.href.includes('/.auth')) {
      location.reload();
      localStorage.removeItem('logout');
    }
  }
}
</script>
<style lang="scss">
.spinner {
  position: fixed;
  display: none;
  width: 100vw;
  height: 100vh;
  z-index: 9999999;
  background-color: #00000066;
  top: 0;
}
</style>
