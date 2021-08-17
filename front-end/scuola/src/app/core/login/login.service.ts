import { Injectable } from '@angular/core';

import { AuthService } from '../auth/auth.service';

@Injectable({ providedIn: 'root' })
export class LoginService {
  constructor(private authService: AuthService) {}

  login(): void{
    return this.authService.login();
  }

  logout(): void {
    this.authService.logout();
  }
}
