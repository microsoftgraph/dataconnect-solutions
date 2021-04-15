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