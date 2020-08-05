import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { catchError, map, } from 'rxjs/operators';
import { GrowlerService, GrowlerMessageType } from '../growler/growler.service';
import { serverAPIConfig } from '../serverAPIConfig'
import { PermissionService } from './permission.service';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable()
export class DataService {


  host: string = serverAPIConfig.host;

  static growler;

  userId = '';
  listStudentiIds = [];

  timeout: number = 30000;

  constructor(
    private http: HttpClient,
    private growler: GrowlerService,
    private permissionService: PermissionService) {
    DataService.growler = growler;
  }


  setUserId(id) {
    if (id) {
      this.userId = id;
    }
  }

  getUsersList(page, pageSize, filters) {
    let params = new HttpParams();
    params = params.append('page', page);
    params = params.append('size', pageSize);

    if (filters) {
      if (filters.role)
        params = params.append('role', filters.role);
      if (filters.text)
        params = params.append('text', filters.text);
    }

    return this.http.get<any>(
      this.host + "/users",
      {
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(users => {
          return (users);
        }),
        catchError(this.handleError)
      );
  }

  getUser(userId) {
    return this.http.get(this.host + "/user/" + userId)
      .timeout(this.timeout)
      .pipe(
        map(user => {
          return user;
        },
          catchError(this.handleError)
        )
      );
  }

  deleteUser(userId) {
    return this.http.delete(this.host + "/user/" + userId)
      .timeout(this.timeout)
      .pipe(
        map(user => {
          return user;
        },
          catchError(this.handleError)
        )
      );
  }

  createUser(user) {
    return this.http.post(this.host + "/user", user)
      .timeout(this.timeout)
      .pipe(
        map(user => {
          return user;
        },
          catchError(this.handleError)
        )
      );
  }

  updateUser(user) {
    return this.http.put(this.host + "/user", user)
      .timeout(this.timeout)
      .pipe(
        map(user => {
          return user;
        },
          catchError(this.handleError)
        )
      );
  }

  updateRole(userId, role, id) {
    let params = new HttpParams();
    params = params.append('role', role);
    params = params.append('newId', id);
    return this.http.post(this.host + "/user/" + userId + "/role", "",
      {
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(user => {
          return user;
        },
          catchError(this.handleError)
        )
      );
  }

  deleteRole(userId, role, oldId) {
    let params = new HttpParams();
    params = params.append('role', role);
    if (oldId) {
      params = params.append('oldId', oldId);
    }

    return this.http.delete(this.host + "/user/" + userId + "/role",
      {
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(user => {
          return user;
        },
          catchError(this.handleError)
        )
      );
  }

  getAziende(page, pageSize, filters) {
    let params = new HttpParams();
    params = params.append('page', page);
    params = params.append('size', pageSize);

    if (filters) {
      if (filters.pIva)
        params = params.append('pIva', filters.pIva);
      if (filters.text)
        params = params.append('text', filters.text);
      if (filters.coordinate)
        params = params.append('coordinate', filters.coordinate);
      if (filters.raggio)
        params = params.append('raggio', filters.raggio);
    }

    return this.http.get<any>(
      this.host + "/list/aziende",
      {
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(users => {
          return (users);
        }),
        catchError(this.handleError)
      );
  }

  getIstituti(page, pageSize, filters) {
    let params = new HttpParams();
    params = params.append('page', page);
    params = params.append('size', pageSize);

    if (filters) {
      if (filters.text)
        params = params.append('text', filters.text);
      if (filters.coordinate)
        params = params.append('coordinate', filters.coordinate);
      if (filters.raggio)
        params = params.append('raggio', filters.raggio);
    }

    return this.http.get<any>(
      this.host + "/list/istituti",
      {
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(users => {
          return (users);
        }),
        catchError(this.handleError)
      );
  }

  getStudenti(page, pageSize, filters) {
    let params = new HttpParams();
    params = params.append('page', page);
    params = params.append('size', pageSize);

    if (filters) {
      if (filters.text)
        params = params.append('text', filters.text);
      if (filters.cf)
        params = params.append('cf', filters.cf);
    }

    return this.http.get<any>(
      this.host + "/list/studenti",
      {
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(users => {
          return (users);
        }),
        catchError(this.handleError)
      );
  }

  getIstitutoById(id: any): Observable<any> {
    let url = this.host + '/profile/istituto/' + id;

    return this.http.get<any>(url,
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

  getProfile(): Observable<any> {
    let url = this.host + '/profile';
    return this.http.get<any>(url, {
      observe: 'response'
    })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          this.permissionService.setProfileForPermissions(res.body);
          return res.body;
        }),
        catchError(this.handleError)
      );
  }

  getUtilizzoSistema(istitutoId, annoScolastico):  Observable<any> {
    let params = new HttpParams();
    params = params.append('istitutoId', istitutoId);
    params = params.append('annoScolastico', annoScolastico);
    return this.http.get<any>(
      this.host + '/dashboard/sistema',
      {
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res;
        }),
        catchError(this.handleError)
      );    
  }

  getReportAttivita(istitutoId, annoScolastico):  Observable<any> {
    let params = new HttpParams();
    params = params.append('istitutoId', istitutoId);
    params = params.append('annoScolastico', annoScolastico);
    return this.http.get<any>(
      this.host + '/dashboard/attivita',
      {
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res;
        }),
        catchError(this.handleError)
      );    
  }

  getReportRegistrazioni(istitutoId, cf):  Observable<any> {
    let params = new HttpParams();
    params = params.append('istitutoId', istitutoId);
    params = params.append('cf', cf);
    return this.http.get<any>(
      this.host + '/dashboard/registrazioni',
      {
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res;
        }),
        catchError(this.handleError)
      );    
  }

  getReportEsperienze(istitutoId, annoScolastico, text):  Observable<any> {
    let params = new HttpParams();
    params = params.append('istitutoId', istitutoId);
    params = params.append('annoScolastico', annoScolastico);
    if(text) {
      params = params.append('text', text);
    }
    return this.http.get<any>(
      this.host + '/dashboard/esperienze',
      {
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res;
        }),
        catchError(this.handleError)
      );    
  }

  getEsperienzeCsv(istitutoId, annoScolastico, text): Observable<any> {
    let url = this.host + '/export/csv/studente';
    let params = new HttpParams();
    params = params.append('istitutoId', istitutoId);
    params = params.append('annoScolastico', annoScolastico);
    if(text) {
      params = params.append('text', text);
    }
    return this.http.get(
      this.host + '/export/csv/dashboard/esperienze',
      {
        observe: 'response',
        params: params,
        responseType: 'arraybuffer'
      })
      .timeout(this.timeout)
      .map((response) => {
        const blob = new Blob([response.body],{type: response.headers.get('Content-Type')!});
        const url = URL.createObjectURL(blob); 
        const disposition = response.headers.get('Content-Disposition')!;
        const filename = disposition.substring(disposition.indexOf('=')+1).replace(/\\\"/g, '');
        let doc = {
          'url': url,
          'filename': filename
        };
        return doc;
      },
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse) {
    let errMsg = "Errore del server! Prova a ricaricare la pagina.";

    if (error.name === 'TimeoutError') {
      errMsg = error.message;
    }
    else if (error.error) {
      if (error.error.ex) {
        errMsg = error.error.ex;
        // Use the following instead if using lite-server
        //return Observable.throw(err.text() || 'backend server error');
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
    if (DataService.growler.growl) {
      DataService.growler.growl(errMsg, GrowlerMessageType.Danger, 5000);
    }

    return Observable.throw(errMsg);

  }

}