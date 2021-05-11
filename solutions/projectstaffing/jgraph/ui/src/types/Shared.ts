/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

import { BvModal } from 'bootstrap-vue';

export interface ConfirmDialog {
  modal: BvModal;
  callback: Function;
  title: string;
  text: string;
}
