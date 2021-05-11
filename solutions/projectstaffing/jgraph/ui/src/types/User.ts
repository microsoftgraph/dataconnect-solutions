/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

export class User {
  public email?: string;
  public name?: string;
  public role?: string;

  public isAdmin() {
    return this.role === 'admin';
  }

  constructor(init?: Partial<User>) {
    Object.assign(this, init);
  }
}