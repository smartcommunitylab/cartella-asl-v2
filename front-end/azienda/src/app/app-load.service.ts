import { Injectable } from '@angular/core';
import 'rxjs/add/operator/toPromise';
import { DataService } from './core/services/data.service';
import { AuthService } from './core/auth/auth.service';

@Injectable()
export class AppLoadService {

  constructor(public dataService: DataService, public authService: AuthService) { }

  initializeApp(): Promise<any> {
    return new Promise((resolve, reject) => {
      console.log(`initializeApp:: inside promise`);
      this.authService.init().then(() => {
        let isAuthenticated = this.authService.isLoggedIn();
        if (!isAuthenticated) {
          if (!!this.getQueryStringValue('code')) {
            this.authService.completeAuthentication().then(() => {
              if (this.authService.isLoggedIn()) {
                this.initialize().then (()=> {
                  resolve(true);
                });
              }
            });
          } else {
            this.authService.startAuthentication();
          }
        } else {
          this.initialize().then (()=> {
            resolve(true);
          }); 
        }
      });
    });
  }

  initialize() {
    return new Promise((resolve, reject) => {
      this.dataService.getProfile().subscribe(profile => {
        if (profile && profile.aziende) {
          var ids = [];
          for (var k in profile.aziende) {
            ids.push(k);
          }
          this.dataService.setAziendaId(ids[0]);
          resolve(true);
        } else {
          reject();
        }
      }, err => {
        reject();
      });
    });
  }

  getQueryStringValue(key) {
    return decodeURIComponent(window.location.search.replace(new RegExp("^(?:.*[&\\?]" + encodeURIComponent(key).replace(/[\.\+\*]/g, "\\$&") + "(?:\\=([^&]*))?)?.*$", "i"), "$1"));
  }

}