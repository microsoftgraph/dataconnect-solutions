<template>
  <div class="header-wrapper border-bottom">
    <span class="title">{{ currentRoute.title }}</span>
    <div class="actions">
      <b-button
        v-for="action in currentRoute.actions"
        :key="`${action.text}_header_action`"
        :variant="action.variant"
        :class="action.class"
        class="ml-2"
        @click="emit(action.event)"
        >{{ action.text }}</b-button
      >
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class Header extends Vue {
  mounted() {}

  get currentRoute() {
    return this.$store.state.shared.headers[this.$route.name!];
  }

  emit(ev: string) {
    this.$root.$emit(ev);
  }
}
</script>

<style lang="scss">
.header-wrapper {
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1;
  position: relative;
  background-color: #f5f2f0;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0px var(--main-padding);
  .title {
    font-size: 26px;
    color: $gray-dark;
  }
}
@media screen and (max-width: 1024px) {
  .header-wrapper {
    display: none;
  }
}
</style>
