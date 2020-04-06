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

      this.authService.checkLoginStatus().then(valid => {
        if (!valid) {
          console.log('come here for not valid');
          this.authService.redirectAuth();
        }
      });

      if (sessionStorage.getItem('access_token')) {
        this.dataService.getProfile().subscribe(profile => {
          if (profile) {
            if (profile && profile.studenti) {
              var ids = [];
              for (var k in profile.studenti) {
                ids.push(k);
              }
              this.dataService.setStudenteId(ids[0]);
              resolve();
            } else {
              // alert("Errore nel caricamento dell'applicazione. Prova ad attendere qualche minuto e ricaricare la pagina; se l'errore persiste contatta il supporto");
              reject();
            }
          }
        }, err => {
          // alert("Errore nel caricamento dell'applicazione. Prova ad attendere qualche minuto e ricaricare la pagina; se l'errore persiste contatta il supporto");
          reject();
        });
      }
      
    });

  }

}