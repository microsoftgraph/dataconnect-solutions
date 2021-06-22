<template>
  <div class="header-wrapper border-bottom">
    <span class="title">{{ title }}</span>
    <div class="actions ml-auto">
      <slot name="actions"></slot>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class Header extends Vue {
  @Prop({
    default: () => {
      return '';
    }
  })
  title!: string;

  mounted() {}

  emit(ev: string) {
    this.$root.$emit(ev);
  }
}
</script>

<style lang="scss">
.header-wrapper {
  z-index: 1;
  position: relative;
  background-color: white;
  min-height: 80px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0px 28px;
  width: 100%;
  .title {
    font-size: 24px;
    color: $gray-dark;
  }
  .actions {
    display: flex;
    align-items: center;
  }
}
@media screen and (max-width: 1024px) {
  .header-wrapper {
    display: none;
  }
}
</style>
