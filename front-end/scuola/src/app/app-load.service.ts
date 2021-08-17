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
                resolve(this.initialize());
              }
            });
          } else {
            this.authService.startAuthentication();
          }
        } else {
          resolve(this.initialize());
        }
      });
    });
  }

  initialize() {
    new Promise((resolve, reject) => {
      this.dataService.getProfile().subscribe(profile => {
        if (profile && profile.istituti) {
          var ids = [];
          for (var k in profile.istituti) {
            ids.push(k);
          }
          this.dataService.setIstitutoId(ids[0]);
          this.dataService.getSchoolYear(ids[0], null)
            .subscribe((res) => {
              this.dataService.setSchoolYear(res.schoolYear);
              resolve(true);
            },
              (err: any) => console.log(err),
              () => console.log('get school year'));
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

