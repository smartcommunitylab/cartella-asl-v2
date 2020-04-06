import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { config } from '../../config';

@Injectable()
export class AuthenticationService {
    aacClientId;
    redirectUrl;
    scope;
    aacUrl;
  
    constructor(
        private http: HttpClient) {
        this.aacClientId = config.aacClientId;
        this.redirectUrl = config.redirectUrl;
        this.scope = config.scope;
        this.aacUrl = config.aacUrl;

    }

    /**
     * Check status of the login. Return true if the user is already logged or the token present in storage is valid
     */
    checkLoginStatus(): Promise<boolean> {
        const token = sessionStorage.getItem('access_token');
        const expiresIn = sessionStorage.getItem('access_token_expires_in') || 0;
        return Promise.resolve(!!token && expiresIn > new Date().getTime());
    }

    redirectAuth() {
        window.location.href = `${this.aacUrl}/eauth/authorize?response_type=token&client_id=${this.aacClientId}&scope=${this.scope}&redirect_uri=${this.redirectUrl}`;
    }

    logout() {
        sessionStorage.clear();
        const redirect = `${this.aacUrl}/logout?target=${window.location.href}`;
        window.location.href = redirect;
    }
    getToken() {
        return sessionStorage.getItem('access_token');
    }

    getProfile(): Observable<any> {
        return this.http.get(`${this.aacUrl}/basicprofile/me`);
    }
}