import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Rx';
import { forkJoin } from 'rxjs';  // RxJS 6 syntax
import { catchError, map, } from 'rxjs/operators';
import { Attivita } from '../../shared/classes/Attivita.class';
import { ReportClasse } from '../../shared/classes/ReportClasse.class';
import { ReportStudente } from '../../shared/classes/ReportStudente.class';
import { Competenza } from '../../shared/classes/Competenza.class';
// import { PianoAlternanza, AttivitaPiano } from '../../shared/classes/PianoAlternanza.class';
import { IApiResponse } from '../../shared/classes/IApiResponse.class';
import { IPagedIstituto } from '../../shared/classes/IPagedIstituto.class';
import { GrowlerService, GrowlerMessageType } from '../growler/growler.service';
import { CorsoDiStudio } from '../../shared/classes/CorsoDiStudio.class';
import { serverAPIConfig } from '../serverAPIConfig'
import { Azienda, IPagedES, IPagedAA, EsperienzaSvolta, Valutazione, IPagedOffers, IOffer } from '../../shared/interfaces';
import { AttivitaAlternanza } from '../../shared/classes/AttivitaAlternanza.class';
import { IPagedAzienda } from '../../shared/classes/Azienda.class';
import { DomSanitizer } from '@angular/platform-browser';
import { environment } from '../../../environments/environment';

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
  private corsiStudio = this.host + '/corsiDiStudio/';
  private reportStudentiByPiano = this.host + "/programmazione";
  private reportClasse = this.host + "/report";
  private attivitaGiornalieraListaEndpoint = this.host + '/attivitaGiornaliera';
  private attivitaGiornalieraStudentiStatusEndpoint = this.host + '/attivitaGiornaliera/esperienze/report'
  private attivitaAlternanzaEndpoint = this.host + '/attivitaAlternanza';
  private studentiProfiles = this.host + '/studenti/profiles';
  private classiRegistration = this.host + '/registration/classi';
  private solveEccezioni = this.host + '/eccezioni/attivita';
  private esperienzaEndPoint = this.host + '/esperienzaSvolta/details';
  corsoDiStudioAPIUrl: string = '/corsi';
  esperienzaSvoltaAPIUrl: string = '/esperienzaSvolta';
  attiitaAlternanzaAPIUrl: string = '/attivitaAlternanza'
  diarioDiBordoAPIUrl: string = "/diarioDiBordo"
  opportunitaAPIUrl: string = '/opportunita';
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

  getProfile(): Observable<any> {
    let url = this.host + '/profile';
    return this.http.get<any>(url, {
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

  setAziendaPosition(coord) {
    this.coorindateAzienda = coord;
  }

  getAziendaPosition() {
    return this.coorindateAzienda;
  }


  // GET /azienda/{id}
  getAziendaInfoRiferente(aziendaId: any) {
    let url = this.host + "/azienda/" + aziendaId;

    return this.http.get<Azienda>(url,
      {
        observe: 'response'
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          let attivita = res.body as Azienda;
          return attivita;
        }),
        catchError(this.handleError)
      );
  }

/** ATTIVITA ALTERNANZA **/  
  updateAttivitaAlternanza(attivita): Observable<AttivitaAlternanza> {

    let url = this.host + "/attivita/ente";
    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);

    return this.http.post<AttivitaAlternanza>(url, attivita,
      {
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res;
        },
          catchError(this.handleError)
        )
      );
  }

  getAttivitaTipologie(): Observable<object[]> {
    let url = this.host + '/tipologieTipologiaAttivita';

    return this.http.get<object[]>(url)
      .timeout(this.timeout)
      .pipe(
        map(tipologie => {
          return tipologie;
        },
          catchError(this.handleError)
        )
      );
  }

  getAttivitaAlternanzaForEnteAPI(filter, page: any, pageSize: any): Observable<IPagedAA> {

    let url = this.host + "/attivita/ente/search/";
    let headers = new HttpHeaders();
    let params = new HttpParams();

    if (filter.titolo)
      params = params.append('text', filter.titolo);
    if (filter.stato != null)
      params = params.append('stato', filter.stato);

    params = params.append('enteId', this.aziendaId);
    params = params.append('page', page);
    params = params.append('size', pageSize);

    return this.http.get<IPagedAA>(
      url,
      {
        headers: headers,
        params: params,
        observe: 'response'
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return (res.body as IPagedAA);

        }),
        catchError(this.handleError)
      );
  }

  getAttivita(id): Observable<any> {
    let url = this.host + "/attivita/" + id + '/ente';
    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);

    return this.http.get<any>(
      url,
      {
        observe: 'response',
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return (res.body);

        }),
        catchError(this.handleError)
      );

  }

  attivitaAlternanzaEsperienzeReport(id): Observable<any> {
    let url = this.host + "/attivita/" + id + '/report/esperienze/ente';
    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);

    return this.http.get<any>(
      url,
      {
        observe: 'response',
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return (res.body);

        }),
        catchError(this.handleError)
      );
  }

  getAttivitaPresenzeIndividualeReport(id: any): Observable<any> {
    let url = this.host + "/attivita/" + id + '/presenze/individuale/report/ente';
    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);

    return this.http.get<any>(
      url,
      {
        observe: 'response',
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return (res.body);

        }),
        catchError(this.handleError)
      );

  }
 
  getAttivitaReportStudenti(id): Observable<any> {
    let url = this.host + "/attivita/" + id + '/report/studenti/ente';
    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);

    return this.http.get<any>(
      url,
      {
        observe: 'response',
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return (res.body);

        }),
        catchError(this.handleError)
      );

  }

  getAttivitaPresenzeIndividualeListaGiorni(id: any, dataInizio, dataFine): Observable<any> {
    let url = this.host + "/attivita/" + id + '/presenze/individuale/ente';
    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);
    params = params.append('dateFrom', dataInizio);
    params = params.append('dateTo', dataFine);

    return this.http.get<any>(
      url,
      {
        observe: 'response',
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return (res.body);

        }),
        catchError(this.handleError)
      );

  }

  validaPresenzeAttivitaIndividuale(id, presenze): Observable<any> {
    let url = this.host + "/attivita/" + id + "/presenze/individuale/ente";
    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);

    return this.http.post(url, presenze, { params: params })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res
        },
          catchError(this.handleError)
        )
      );
  }

   /** DOCUMENTI **/
  // uploadDocumentToRisorsa(file: File, uuid: string): Observable<any> {
  //   let url = this.host + '/upload/document/risorsa/' + uuid + '/istituto/' + this.istitutoId;
  //   let formData: FormData = new FormData();
  //   formData.append('data', file, file.name);
  //   let headers = new Headers();

  //   return this.http.post<Valutazione>(url, formData)
  //     .timeout(this.timeout)
  //     .pipe(
  //       map(res => {
  //         return res;
  //       }),
  //       catchError(this.handleError)
  //     )
  // }

  // downloadAttivitaDocumenti(id: any): Observable<any> {
  //   let url = this.host + '/download/document/risorsa/' + id + '/istituto/' + this.istitutoId + '/attivita';

  //   return this.http.get<any>(url,
  //     {
  //       observe: 'response'
  //     })
  //     .timeout(this.timeout)
  //     .pipe(
  //       map(res => {
  //         return res.body;
  //       }),
  //       catchError(this.handleError)
  //     );
  // }

  // deleteDocument(id: any): Promise<any> {
  //   let body = {}
  //   let url = this.host + '/remove/document/' + id + '/istituto/' + this.istitutoId;

  //   return this.http.delete(url)
  //     .timeout(this.timeout)
  //     .toPromise().then(response => {
  //       return response;
  //     }
  //     ).catch(response => this.handleError);
  // }

  // downloadDocumentBlob(doc): Observable<any> {
  //   let url = this.host + '/download/document/' + doc.uuid + '/istituto/' + this.istitutoId;
  //   return this.http.get(url,
  //     {
  //       responseType: 'arraybuffer'
  //     })
  //     .timeout(this.timeout)
  //     .map(data => {
  //       const blob = new Blob([data], { type: doc.formatoDocumento });
  //       const url = URL.createObjectURL(blob);
  //       return url;
  //       //doc.url = this.sanitizer.bypassSecurityTrustResourceUrl(window.URL.createObjectURL(blob));
  //     },
  //       catchError(this.handleError)
  //     );
  // }

  // downLoadFile(data: any, type: string) {
  //   let blob = new Blob([data], { type: type });
  //   let url = window.URL.createObjectURL(blob);
  //   let pwa = window.open(url);

  //   if (!pwa || pwa.closed || typeof pwa.closed == 'undefined') {
  //     alert('Please disable your Pop-up blocker and try again.');
  //   }
  // }

  getRisorsaCompetenze(uuid): Observable<any> {
    let url = this.host + '/risorsa/' + uuid + '/competenze/ente/' + this.aziendaId
    // let params = new HttpParams();
    // params = params.append('enteId', this.aziendaId);

    return this.http.get<any>(url,
      // {
      //   params: params
      // }
    ).timeout(this.timeout)
      .pipe(
        map(competenze => {
          return competenze;
        },
          catchError(this.handleError)
        )
      );
  }

 
  addConsent(): Observable<any> {
    let url = this.host + '/consent/add';

    return this.http.put(
      url,
      {
        observe: 'response',
      }
    )
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res;
        }),
        catchError(this.handleError))
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