import { Injectable } from '@angular/core';
import 'rxjs/add/operator/toPromise';
import { DataService } from './core/services/data.service';

@Injectable()
export class AppLoadService {

  constructor(public dataService: DataService) { }

  initializeApp(): Promise<any> {

    return new Promise((resolve, reject) => {
      console.log(`initializeApp:: inside promise`);
      this.dataService.getProfile().subscribe(profile => {
        if (profile && profile.aziende) {
          sessionStorage.setItem('access_token', profile.token)
          var ids = [];
          for (var k in profile.aziende) {
            ids.push(k);
          }
          this.dataService.setAziendaId(ids[0]);
          resolve();
        } else {
          // alert("Errore nel caricamento dell'applicazione. Prova ad attendere qualche minuto e ricaricare la pagina; se l'errore persiste contatta il supporto");
          reject();
        }

      }, err => {
        // alert("Errore nel caricamento dell'applicazione. Prova ad attendere qualche minuto e ricaricare la pagina; se l'errore persiste contatta il supporto");
        reject();
      });
    });

  }

}