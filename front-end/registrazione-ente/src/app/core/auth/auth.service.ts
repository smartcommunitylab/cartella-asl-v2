import { Injectable, Injector } from '@angular/core';
import { Router } from '@angular/router';

import { UserManager, UserManagerSettings, User } from 'oidc-client';
import { BASE_URL } from '../../../app/app.constants';
import { Account } from '../../core/user/account.model';
import { StateStorageService } from './state-storage.service';
import { config } from '../../config'

export function getClientSettings(): UserManagerSettings {
  const url = BASE_URL || window.location.protocol + '//' + window.location.host + '/';
  return {
      authority: config.aacUrl,
      client_id: config.aacClientId,
      redirect_uri: config.redirectUrl,
      post_logout_redirect_uri: url,
      response_type: 'code',
      scope: config.scope,
      filterProtocolClaims: true,
      loadUserInfo: false
  };
}

@Injectable(
  // { providedIn: 'root' }
  )
export class AuthService {

  private manager = new UserManager(getClientSettings());
  private user: User | null = null;
  private account: Account | null = null;

  constructor(private _injector: Injector)
  {
    console.log('here');
  }

  private get router() { return this._injector.get(Router); }
  private get stateStorageService() { return this._injector.get(StateStorageService); }

  init(): Promise<User | null> {
    return this.manager.getUser().then(user => {
      this.user = user;
      return user;
    });
  }

  checkLoggedIn(): Promise<boolean> {
    return this.user != null ? Promise.resolve(this.isLoggedIn()) : this.init().then(() => this.isLoggedIn());
  }

  isLoggedIn(): boolean {
    return this.user != null && !this.user.expired;
  }
  getClaims(): any {
    return this.user?this.user.profile:null;
  }
  getAccount(): Account | null {
    if (this.account === null && this.user !== null) {
      this.account = new Account(
        true,
        [],
        this.user?this.user.profile.email:null || this.user?this.user.profile.username:null || this.user?this.user.profile.preferred_username: null || '',
        this.user.profile.given_name || '',
        'it',
        this.user?this.user.profile.family_name:null || '');

    }
    return this.account;
  }

  getAuthorizationHeaderValue(): string {
    return `${this.user?this.user.token_type:null} ${this.user?this.user.access_token:null}`;
  }

  startAuthentication(): Promise<void> {
    return this.manager.signinRedirect();
  }

  completeAuthentication(): Promise<void> {
    return this.manager.signinRedirectCallback().then(user => {
        this.user = user;
        this.navigateToStoredUrl();
    });
  }

  login(): void {
    this.startAuthentication();
  }

  logout(): void{
    var getUrl = window.location;
    var baseUrl = getUrl.protocol + "//" + getUrl.host; // + "/" + getUrl.pathname.split('/')[1]
    var logoutUrl = `${config.aacUrl}/logout?target=${baseUrl}/asl-login/`;        
    this.user = null;
    this.account = null;
    sessionStorage.clear();
    this.manager.signoutRedirect().then(()=> {
      window.location.href = logoutUrl;
    });    
  }

  subscribeSignedIn(): Promise<void> {
    return new Promise((resolve) => {
      this.manager.events.addUserLoaded(() => {
        this.checkLoggedIn().then(() => {
          resolve();
        });
      });
    });
  }

  private navigateToStoredUrl(): void {
    // previousState can be set in the authExpiredInterceptor and in the userRouteAccessService
    // if login is successful, go to stored previousState and clear previousState
    const previousUrl = this.stateStorageService.getUrl() || '/';
    if (previousUrl) {
      this.stateStorageService.clearUrl();
      this.router.navigateByUrl(previousUrl);
    }
  }
}
