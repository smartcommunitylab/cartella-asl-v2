import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';

import { AuthService } from './auth.service';
import { StateStorageService } from './state-storage.service';

@Injectable(
  // { providedIn: 'root' }
  )
export class UserRouteAccessService implements CanActivate {
  constructor(
    private authService: AuthService,
    private stateStorageService: StateStorageService
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> | boolean {
    if (this.authService.isLoggedIn()) {
      return true;
    }

    return this.authService.checkLoggedIn().then((auth: boolean) => {
      if (auth) {
        return true;
      }
      this.stateStorageService.storeUrl(state.url);
      this.authService.startAuthentication();
      return false;
    });

  }
}
