import { Injectable } from '@angular/core';
import 'rxjs/add/operator/toPromise';
import { AuthService } from './core/auth/auth.service';

@Injectable()
export class AppLoadService {

  constructor(private authService: AuthService) { }

  initializeApp(): Promise<any> {

    return new Promise((resolve, reject) => {
      console.log(`initializeApp:: inside promise`);
      if (!!this.getQueryStringValue('code')) {
        this.authService.completeAuthentication().then(() => {
          if (this.authService.isLoggedIn()) {
            window.location.hash = '/registrazione';
          }
        });
      }
      resolve(true);
    });

  }

  getQueryStringValue(key) {
    return decodeURIComponent(window.location.search.replace(new RegExp("^(?:.*[&\\?]" + encodeURIComponent(key).replace(/[\.\+\*]/g, "\\$&") + "(?:\\=([^&]*))?)?.*$", "i"), "$1"));
  }

}
