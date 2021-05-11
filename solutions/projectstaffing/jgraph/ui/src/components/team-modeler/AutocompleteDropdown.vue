<!--
  - Copyright (c) Microsoft Corporation. All rights reserved.
  - Licensed under the MIT license. See LICENSE file in the project root for full license information.
  -->

<template>
  <div
    id="autocompleteDropdown"
    v-if="focused && autocomplete.length !== 0"
    class="autocomplete-dropdown"
  >
    <span
      v-for="(option, index) in autocomplete"
      :key="`${option.suggestedSkill}_${index}`"
      class="section"
      :id="`autocomplete_${getIndexForSkill(true, option)}`"
    >
      <span
        class="d-block skill-snack"
        :class="{
          highlighted: autocompleteIndex === getIndexForSkill(true, option)
        }"
        @click="addTagToSearch(option.suggestedSkill)"
        >{{ option.suggestedSkill }}</span
      >
      <span class="related-skill-section border-bottom">
        <span
          class="skill-snack"
          v-for="(related, relatedIndex) in option.relatedTerms"
          :key="`${related.suggestedSkill}_${relatedIndex}`"
          :class="{
            highlighted:
              autocompleteIndex === getIndexForSkill(false, option, related)
          }"
          :id="`autocomplete_${getIndexForSkill(false, option, related)}`"
          @click="addTagToSearch(related)"
        >
          {{ related }}
        </span>
      </span>
    </span>
  </div>
</template>

<script lang="ts">
import { Component, Emit, Prop, Vue } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class autocompleteDropdown extends Vue {
  focused = false;
  autocompleteIndex = -1;

  @Prop({
    default: () => {
      return [];
    }
  })
  autocomplete: [];

  @Prop({
    default: () => {
      return [];
    }
  })
  autocompleteArr: any[];

  mounted() {
    this.$root.$on('hideAutocompleteDropdown', () => {
      setTimeout(() => {
        if (document.activeElement !== document.getElementById('searchInput'))
          this.focused = false;
        this.$store.dispatch('clearAutocomplete');
      }, 0);
    });
    this.$root.$on('resetAutocompleteIndex', () => {
      this.autocompleteIndex = -1;
    });
    this.$root.$on('autocompleteIncreaseIndexUpDown', this.increaseIndexUpDown);
    this.$root.$on(
      'autocompleteIncreaseIndexLeftRight',
      this.increaseIndexLeftRight
    );
    window.addEventListener('click', (ev: any) => {
      if (ev.path) {
        // reset autocomplete index
        this.autocompleteIndex = -1;
        this.focused =
          ev.path.find((x: any) =>
            x.classList?.contains('custom-tags-input')
          ) !== undefined ||
          document.activeElement === document.getElementById('searchInput');
        return;
      }
      this.focused = false;
    });
  }

  @Emit('add-tag-to-search')
  // eslint-disable-next-line no-unused-vars
  addTagToSearch(value: any) {}

  @Emit('update-native-input')
  updateNativeInput() {}

  getIndexForSkill(isRoot: boolean, option: any, related?: string) {
    const skill = this.autocompleteArr.find((x: any) => {
      if (isRoot) {
        return x.isRoot === true && x.value == option.suggestedSkill;
      } else {
        return (
          x.isRoot === false &&
          x.parentValue === option.suggestedSkill &&
          x.value === related
        );
      }
    });
    return skill !== undefined ? this.autocompleteArr.indexOf(skill) : 0;
  }

  increaseIndexUpDown(downKey: boolean) {
    if (this.autocompleteArr.length !== 0) {
      if (downKey) {
        this.autocompleteIndex = this.getNextIndexDown();
      } else this.autocompleteIndex = this.getNextIndexUp();
      this.updateNativeInput();
    }
  }

  increaseIndexLeftRight(right: boolean) {
    if (this.autocompleteArr.length !== 0) {
      if (right)
        this.autocompleteIndex = Math.min(
          this.autocompleteIndex + 1,
          this.autocompleteArr.length - 1
        );
      else this.autocompleteIndex = Math.max(this.autocompleteIndex - 1, -1);
      this.updateNativeInput();
    }
  }

  getNextIndexDown() {
    // if autocomplete arrow navigation not started yet set by default on first skill
    if (this.autocompleteIndex === -1) return 0;
    else {
      if (this.autocompleteArr[this.autocompleteIndex].isRoot)
        // if highlight positioned on a root skill then change section
        return Math.min(
          this.autocompleteArr[this.autocompleteIndex].children.length +
            this.autocompleteIndex +
            1,
          this.autocompleteArr.length - 1
        );
      else {
        // if highlight positioned on a NO root skill then find parent and change section as before
        const parent = this.autocompleteArr.find(
          (x: any) =>
            x.value === this.autocompleteArr[this.autocompleteIndex].parentValue
        );
        return Math.min(
          this.autocompleteArr.indexOf(parent) + parent.children.length + 1,
          this.autocompleteArr.length - 1
        );
      }
    }
  }

  getNextIndexUp() {
    // if autocomplete arrow is no where leave it there
    if (this.autocompleteIndex === -1) return -1;
    // if autocomplete arrow navigation is at end do nothing
    else if (this.autocompleteIndex === this.autocompleteArr.length)
      return this.autocompleteArr.length;
    else {
      // if first element is selected then deselect
      if (this.autocompleteIndex === 0) return -1;
      else if (this.autocompleteArr[this.autocompleteIndex].isRoot) {
        // if highlight positioned on a root skill then change section by taking the parent of child sibling but first verify if maybe it has no children
        const parentOfPreviousSibling = this.autocompleteArr[
          this.autocompleteIndex - 1
        ].isRoot
          ? this.autocompleteArr[this.autocompleteIndex - 1]
          : this.autocompleteArr.find(
              (x: any) =>
                x.value ===
                this.autocompleteArr[this.autocompleteIndex - 1].parentValue
            );
        return Math.max(
          this.autocompleteArr.indexOf(parentOfPreviousSibling),
          -1
        );
      } else {
        // if highlight positioned on a NO root skill then find parent and change section as before
        const parentSiblingIndex = this.autocompleteArr.indexOf(
          this.autocompleteArr.find(
            (x: any) =>
              x.value ===
              this.autocompleteArr[this.autocompleteIndex].parentValue
          )
        );
        return Math.max(parentSiblingIndex, -1);
      }
    }
  }
}
</script>

<style lang="scss">
.autocomplete-dropdown {
  position: absolute;
  display: flex;
  width: 100%;
  flex-direction: column;
  background-color: white;
  border: 1px solid $main-border-color;
  margin-top: 4px;
  border-radius: $main-border-radius;
  z-index: 1;
  max-height: 500px;
  overflow: auto;
  .section {
    display: flex;
    flex-direction: column;
    border-top: 1px solid transparent;
    border-bottom: 1px solid transparent;
    padding: 12px;
    padding-bottom: 0px;
    z-index: 2;
    .skill-snack {
      cursor: pointer;
      width: fit-content;
      padding: 5px 15px;
      border-radius: 100px;
      margin: 3px;
      display: inline-block;
      border: 1px solid $main-border-color;
      &:hover,
      &.highlighted {
        border-color: $primary;
        background-color: rgba($primary, $alpha: 0.1);
      }
    }
    .related-skill-section {
      padding-bottom: 12px;
      padding-left: 24px;
    }
    &:last-child {
      .related-skill-section {
        border-bottom: none !important;
      }
    }
  }
  .offset {
    padding-left: 30px;
  }
}
</style>
