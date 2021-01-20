import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Rx';
import { catchError, map } from 'rxjs/operators';
import { GrowlerService, GrowlerMessageType } from '../growler/growler.service';
import { serverAPIConfig } from '../serverAPIConfig'
import { DomSanitizer } from '@angular/platform-browser';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable()
export class DataService {

  istitutoId: string = "19a46a53-8e10-4cd0-a7d0-fb2da217d1be";
  schoolYear: string = "2019-20";
  listIstituteIds = [];
  istituto: string = "Centro Formazione Professionale Agrario - S. Michele all'Adige'";
  host: string = serverAPIConfig.host;
  static growler;
  timeout: number = 120000;
  coorindateIstituto;
  aziendaId: string = '';
  listAziendaIds = [];
  aziendaName: string = "";
  coorindateAzienda;

  constructor(
    private http: HttpClient,
    private growler: GrowlerService,
    private sanitizer: DomSanitizer) {
    DataService.growler = growler;
  }

  /** AZIENDA.  **/
  setAziendaId(id) {
    if (id) {
      this.aziendaId = id;
    }
  }

  setAziendaName(name) {
    if (name) {
      this.aziendaName = name;
    }
  }

  setListId(list) {
    if (list) {
      this.listIstituteIds = list;
    }
  }

  getRegistrazioneByToken(token): Observable<any> {
    let url = this.host.replace('/api', '/registrazione-ente');
    let params = new HttpParams();
    params = params.append('token', token);
    return this.http.get<any>(url, {
      params: params,
      observe: 'response'
    })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res.body;
        }),
        // catchError(this.handleError)
      );
  }

  confermaRichiestaRegistrazione(token): Observable<any> {
    let url = this.host.replace('/api', '/registrazione-ente');
    let params = new HttpParams();
    params = params.append('token', token);
    return this.http.post<any>(url, 
      null,
      {
      params: params,
      observe: 'response'
    })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res.body;
        }),
        catchError(this.handleError)
      );
  }

  getProfile(): Observable<any> {
    let headers = new HttpHeaders();
    const authHeader = `Bearer ${sessionStorage.getItem('access_token')}`;

    let url = this.host + '/profile';
    return this.http.get<any>(url, {
      headers: headers.set('Authorization', authHeader),
      observe: 'response'
    })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res.body;
        }),
        catchError(this.handleError)
      );
  }

  getListaAziendeByIds(ids: any): any {
    let singleObservables = ids.map((singleIds: string, urlIndex: number) => {
      return this.getAziendaInfoRiferente(singleIds)
        .timeout(this.timeout)
        .map(single => single)
        .catch((error: any) => {
          console.error('Error loading Single, singleUrl: ' + singleIds, 'Error: ', error);
          return Observable.of(null);
        });
    });

    return Observable.forkJoin(singleObservables);
  }

  getAziendaInfoRiferente(aziendaId: any) {
    let headers = new HttpHeaders();
    const authHeader = `Bearer ${sessionStorage.getItem('access_token')}`;
    let url = this.host + "/azienda/" + aziendaId;

    return this.http.get<any>(url,
      {
        headers: headers.set('Authorization', authHeader),
        observe: 'response'
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          let attivita = res.body;
          return attivita;
        }),
        catchError(this.handleError)
      );
  }

  // GET /azienda/{id}
  getAzienda() {
    let headers = new HttpHeaders();
    const authHeader = `Bearer ${sessionStorage.getItem('access_token')}`;
    let url = this.host + "/azienda/" + this.aziendaId + "/ente";

    return this.http.get<any>(url,
      {
        headers: headers.set('Authorization', authHeader),
        observe: 'response'
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          let attivita = res.body as any;
          return attivita;
        }),
        catchError(this.handleError)
      );

  }

  addAzienda(az): Observable<any> {
    let headers = new HttpHeaders();
    const authHeader = `Bearer ${sessionStorage.getItem('access_token')}`;
    let url = this.host + "/azienda/ente";

    return this.http.post<any>(
      url,
      az,
      {
        headers: headers.set('Authorization', authHeader),
        observe: 'response',
      }
    )
      .timeout(this.timeout)
      .pipe(
        map(res => {
          if (res.ok) {
            return (res.body as any);
          } else
            return res;
        }
        ),
        catchError(this.handleError))
  }

  aggiornaDatiOwnerAzienda(data): Observable<any> {
    let headers = new HttpHeaders();
    const authHeader = `Bearer ${sessionStorage.getItem('access_token')}`;
    let url = this.host + "/registrazione-ente/owner";

    let params = new HttpParams();
    params = params.append('enteId', data.enteId);
    params = params.append('nome', data.name);
    params = params.append('cognome', data.surname);
    params = params.append('cf', data.cf);
    params = params.append('ownerId', data.ownerId);
  
    return this.http.post<any>(
      url,
      null,
      {
        headers: headers.set('Authorization', authHeader),
        params: params,
        observe: 'response',
      }
    )
      .timeout(this.timeout)
      .pipe(
        map(res => {
          if (res.ok) {
            return (res.body as any);
          } else
            return res;
        }
        ),
        catchError(this.handleError))
  }
  

  getAteco(code: string): Observable<any> {
    let url = 'https://dss.coinnovationlab.it/services/ateco/ricerca/' + code;
    return this.http.get(url,
      {
        observe: 'response'
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res.body;
        }),
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse) {
    let errMsg = "Errore del server! Prova a ricaricare la pagina.";

    if (error.name === 'TimeoutError') {
      errMsg = error.message;
    }
    else if (error.error) {
      if (error.error.message) {
        errMsg = error.error.message;
      } else if (error.error.ex) {
        errMsg = error.error.ex;
      } else if (typeof error.error === "string") {
        try {
          let errore = JSON.parse(error.error);
          if (errore.ex) {
            errMsg = errore.ex;
          }
        }
        catch (e) {
          console.error('server error:', errMsg);
        }
      }
    }

    console.error('server error:', errMsg);

    let displayGrowl: boolean = true;
    // to avoid display growl tip inccase of 401 | 403
    if ((error.status == 401) || (error.status == 403)) {
      displayGrowl = false;
    }

    if (DataService.growler.growl && displayGrowl) {
      DataService.growler.growl(errMsg, GrowlerMessageType.Danger, 5000);
    }

    return Observable.throw(errMsg);

  }

}