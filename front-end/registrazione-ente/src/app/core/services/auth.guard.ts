import {CanActivate, CanActivateChild, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { Injectable } from "@angular/core";
import { AuthService } from "../auth/auth.service";


@Injectable()
export class AuthGuard implements CanActivate, CanActivateChild {

  constructor(private login: AuthService, private router: Router) {}

  /**
   * Can navigate to internal pages only if the user is authenticated
   * @param route
   * @param state
   */
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    console.log('AuthGuard#canActivate called');
    return this.login.checkLoggedIn().then(valid => {
      if (!valid) {
        console.log('come here for not valid');
        this.login.startAuthentication();
      }
      return valid;
    });
  }

  canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.canActivate(route, state);
  }
}