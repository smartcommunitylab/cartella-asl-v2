import { Injectable } from '@angular/core';
import 'rxjs/add/operator/toPromise';
import { DataService } from './core/services/data.service';
import { AuthenticationService } from './core/services/authentication.service';

@Injectable()
export class AppLoadService {

  constructor(public dataService: DataService, public authService: AuthenticationService) { }

  initializeApp(): Promise<any> {

    return new Promise((resolve, reject) => {
      console.log(`initializeApp:: inside promise`);
      resolve();
    });

  }

}