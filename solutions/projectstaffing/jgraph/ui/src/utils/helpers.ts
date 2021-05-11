/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

import { Employee } from '@/types/Employee';
import store from '../store';

export function validateState(this: any, name: string, formSubmited?: boolean) {
  if (this.$v.form[name] && formSubmited !== undefined ? formSubmited : true) {
    const $dirty = this.$v.form[name]?.$dirty;
    const $error = this.$v.form[name]?.$error;
    return $dirty ? !$error : null;
  }
}

export function getRandomColor(index: number) {
  const colors = ['#7EE8B4', '#FFD294', '#FFB1A7', '#DEABFB', '#A9CBFD'];
  return colors[index % colors.length];
}
