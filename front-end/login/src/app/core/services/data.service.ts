import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams, HttpHeaders, HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { catchError, map, } from 'rxjs/operators';
import { Attivita } from '../../shared/classes/Attivita.class';
import { Richiesta } from '../../shared/classes/Richiesta.class';
import { AbilitaCompetenza } from '../../shared/classes/AbilitaCompetenza.class';
import { ReportClasse } from '../../shared/classes/ReportClasse.class';
import { ReportStudente } from '../../shared/classes/ReportStudente.class';
import { ConoscenzaCompetenza } from '../../shared/classes/ConoscenzaCompetenza.class';
import { ProfiloCompetenza } from '../../shared/classes/ProfiloCompetenza.class';
import { Competenza } from '../../shared/classes/Competenza.class';
import { IPagedCorsoInterno } from '../../shared/classes/IPagedCorsoInterno.class';
import { IApiResponse } from '../../shared/classes/IApiResponse.class';
import { IPagedCompetenze } from '../../shared/classes/IPagedCompetenze.class';
import { GrowlerService, GrowlerMessageType } from '../growler/growler.service';
import { debug } from 'util';
import { BaseRequestOptions, RequestOptions } from '@angular/http';


@Injectable()
export class DataService {



  host: string = 'https://dev.smartcommunitylab.it/cartella-as/api';
  //host: string = 'http://localhost:4040/cartella-asl/api';
  //host = 'http://192.168.43.7:4040/cartella-asl';

  private loginEndpoint = this.host + '/login';
  
  static growler;

  constructor(
    private http: HttpClient,
    private growler: GrowlerService) {
    DataService.growler = growler;
  }


  login(username, password) {
    return this.http.get<any>(this.loginEndpoint)
      .pipe(
      map(attivita => {
        return attivita
      },
        catchError(this.handleError)
      )
      );
  }


  private handleError(error: HttpErrorResponse) {
    let errMsg = "Errore del server! Prova a ricaricare la pagina.";

    if (error.error.ex) {
      errMsg = error.error.ex;
      // Use the following instead if using lite-server
      //return Observable.throw(err.text() || 'backend server error');
    } else {
      console.error('server error:', errMsg);

    }

    DataService.growler.growl(errMsg, GrowlerMessageType.Danger, 5000);
    console.error('server error:', errMsg);
    return Observable.throw(errMsg);

  }

}