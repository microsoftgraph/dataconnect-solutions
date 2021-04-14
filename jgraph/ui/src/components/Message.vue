<template>
  <div class="toasts-wrapper">
    <transition-group name="fade">
      <div
        v-for="(toast, index) in shownToasts"
        :key="`toast_${index}`"
        class="custom-toast"
      >
        <header class="toast-header custom-toast-header">
          <button
            type="button"
            class="close ml-auto mb-1"
            @click="removeToast(index)"
          >
            ×
          </button>
        </header>
        <div class="toast-body">
          <div class="content-holder" :class="classes[toast.type]">
            <b-icon
              :icon="icons[toast.type]"
              :style="{ fill: colors[toast.type] }"
            ></b-icon>
            <div class="text-container">
              <strong>{{ titles[toast.type] }}</strong>
              <span class="stack-trace"> {{ toast.message }}</span>
            </div>
          </div>
        </div>
      </div>
    </transition-group>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';

@Component({
  components: {}
})
export default class Message extends Vue {
  /* 0-info | 1-error | 2-warning | 3-success */
  type = 3;
  icons = [
    'b-icon-info-circle-fill',
    'b-icon-x-circle-fill',
    'b-icon-exclamation-circle-fill',
    'b-icon-check-circle-fill'
  ];
  colors = ['#1d72f3', '#FF526F', '#ffc007', '#00b768'];
  classes = ['toast--blue', 'toast--red', 'toast--yellow', 'toast--green'];
  titles = ['Info', 'Error', 'Warning', 'Success'];
  message = 'Internal server error. Bad request just occured.';
  errorDelay = 60000;
  shownToasts: any[] = [];
  @Watch('$store.state.shared.message')
  onPropertyChange(newval: any) {
    this.type = newval.type;
    this.message = newval.message ? newval.message : 'Unknown error';
    const uniqueId = Math.random() * 100000000;
    this.shownToasts.push({
      uniqueId: uniqueId,
      type: this.type,
      message: this.message
    });
    setTimeout(
      () => {
        this.shownToasts = this.shownToasts.filter(
          x => x.uniqueId !== uniqueId
        );
      },
      this.type === 1 ? this.errorDelay : newval.delay ? newval.delay : 3000
    );
  }
  removeToast(index: number) {
    this.shownToasts.splice(index, 1);
  }
}
</script>

<style lang="scss">
.toasts-wrapper {
  position: absolute;
  right: 36px;
  top: 36px;
  z-index: 9;
  .toast--green:before {
    background-color: $success;
  }

  .toast--blue:before {
    background-color: #1d72f3;
  }

  .toast--yellow:before {
    background-color: #ffc007;
  }

  .toast--red:before {
    background-color: #ff4444;
  }

  .fade-enter-active,
  .fade-leave-active {
    transition: opacity 0.3s;
  }
  .fade-enter, .fade-leave-to /* .fade-leave-active below version 2.1.8 */ {
    opacity: 0;
  }

  .custom-toast {
    box-shadow: rgba(0, 0, 0, 0.2) 0px 0px 20px 0px;
    border-radius: 4px;
    margin-bottom: 10px;
    width: 300px;
    transition: opacity 0.3s;
    .custom-toast-header {
      padding: 0px !important;
      background-color: transparent !important;
      border: none !important;
      width: 100%;
      box-shadow: none;
      position: absolute;
      button {
        margin-right: 6px;
        margin-top: 0px;
        font-size: 26px;
        opacity: 1;
        z-index: 9;
        padding: 5px !important;
        color: black !important;
        font-weight: 200;
      }
      button:focus {
        outline: none;
      }
    }

    .toast-body {
      background-color: white;
      border-radius: $main-border-radius;
      display: flex;
      min-width: 300px;
      padding: 0px;
      .content-holder {
        position: relative;
        padding: 12px 12px 12px 16px;
        width: 100%;
        display: flex;
        flex-direction: row;
        align-items: center;
        text-align: left;
        svg {
          width: 18px;
          height: 18px;
          margin-right: 10px;
          align-self: flex-start;
        }
        &:before {
          border-top-left-radius: $main-border-radius;
          border-bottom-left-radius: $main-border-radius;
          content: '';
          position: absolute;
          top: 0;
          left: 0;
          width: 4px;
          height: 100%;
        }
        .text-container {
          width: 100%;
          word-break: break-word;
          display: flex;
          align-self: flex-start;
          font-size: 14px;
          margin-top: -3px;
          flex-direction: column;
          .stack-trace {
            max-height: 200px;
            overflow-x: auto;
          }
          strong {
            font-weight: 600;
          }
        }
      }
    }
  }
}

@media screen and (max-width: 1024px) {
  .toasts-wrapper {
    position: fixed;
    right: 12px;
    top: 12px;
  }
}
</style>
