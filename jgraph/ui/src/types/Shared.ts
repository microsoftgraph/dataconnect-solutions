import { BvModal } from 'bootstrap-vue';

export interface ConfirmDialog {
  modal: BvModal;
  callback: Function;
  title: string;
  text: string;
}
