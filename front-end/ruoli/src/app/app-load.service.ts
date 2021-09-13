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

      // this.authService.checkLoginStatus().then(valid => {
      //   if (!valid) {
      //     console.log('come here for not valid');
      //     this.authService.redirectAuth();
      //   }
      // });

      // if (sessionStorage.getItem('access_token')) {
      //   resolve();
      // }

      this.authService.init().then(() => {
        let isAuthenticated = this.authService.isLoggedIn();
        if (!isAuthenticated) {
          if (!!this.getQueryStringValue('code')) {
            this.authService.completeAuthentication().then(() => {
              if (this.authService.isLoggedIn()) {
                resolve(true);
              }
            });
          } else {
            this.authService.startAuthentication();
          }
        } else {
          resolve(true);
        }
      });
    });

  }

  getQueryStringValue(key) {
    return decodeURIComponent(window.location.search.replace(new RegExp("^(?:.*[&\\?]" + encodeURIComponent(key).replace(/[\.\+\*]/g, "\\$&") + "(?:\\=([^&]*))?)?.*$", "i"), "$1"));
  }

}