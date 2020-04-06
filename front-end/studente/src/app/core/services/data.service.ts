import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { forkJoin } from 'rxjs';
import { catchError, map, } from 'rxjs/operators';
import { IPagedCompetenze } from '../../shared/classes/IPagedCompetenze.class';
import { GrowlerService, GrowlerMessageType } from '../growler/growler.service';
import { Studente } from '../../shared/interfaces';
import { serverAPIConfig } from '../serverAPIConfig'

@Injectable()
export class DataService {

  host: string = serverAPIConfig.host;

  private attivitaStudenteEndpoint = this.host + '/studente/attivita';
  private attivitaNoteEndpoint = this.host + '/attivita/noteStudente';
  private attivitaTipologieEndpoint = this.host + '/tipologieTipologiaAttivita';
  private getRegistrationClassi = this.host + '/registration/classi';
  private attivitaGiornalieraListaEndpoint = this.host + '/attivitaGiornaliera';
  private studenteEndpoint = this.host + '/studente';
  /** competenze. */
  competenzeAPIUrl: string = '/competenze'

  static growler;

  studenteId = '';
  listStudentiIds = [];

  timeout: number = 120000;

  constructor(
    private http: HttpClient,
    private growler: GrowlerService) {
    DataService.growler = growler;
  }


  setStudenteId(id) {
    if (id) {
      this.studenteId = id;
    }
  }

  setListId(list) {
    if (list) {
      this.listStudentiIds = list;
    }

  }

  getListId() {
    if (this.listStudentiIds)
      return this.listStudentiIds;
  }

  //ATTIVITA
  getAttivitaStudenteList(page, pageSize, studenteId, filters) {
    let params = new HttpParams();
    params = params.append('page', page);
    params = params.append('size', pageSize);
    params = params.append('studenteId', studenteId);

    if (filters) {
      if (filters.filterText)
        params = params.append('filterText', filters.filterText);
      if (filters.dataInizio)
        params = params.append('dataInizio', filters.dataInizio);
      if (filters.dataFine)
        params = params.append('dataFine', filters.dataFine);
      if (filters.stato)
        params = params.append('stato', filters.stato);
      if (filters.Tipologia)
        params = params.append('Tipologia', filters.Tipologia);
    }

    return this.http.get<any>(
      this.attivitaStudenteEndpoint,
      {
        params: params,
        observe: 'response'
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return (res.body);
        }),
        catchError(this.handleError)
      );
  }

  getAttivitaStudenteById(esperienzaId) {
    let url = this.host + '/studente/' + this.studenteId + '/esperienza/' + esperienzaId;
    return this.http.get(url,
      {
        observe: 'response'
      })
      .timeout(this.timeout)
      .pipe(
        map(resp => {
          return resp.body;
        },
          catchError(this.handleError)
        )
      );
  }

  getIstitutoById(id: any): Observable<any> {
    let url = this.host + '/istituto/' + id;

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

  getListaStudentiByIds(ids: any): any {
    let singleObservables = ids.map((singleIds: string, urlIndex: number) => {
      return this.getStudedente(singleIds)
        .timeout(this.timeout)
        .map(single => single as Studente)
        .catch((error: any) => {
          console.error('Error loading Single, singleUrl: ' + singleIds, 'Error: ', error);
          return Observable.of(null);
        });
    });

    return forkJoin(singleObservables);
  }

  getStudedente(singleId: string): Observable<Studente> {
    return this.http.get<Studente>(
      `${this.studenteEndpoint}/${singleId}`,
      {
        observe: 'response'
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          let competenza = res.body as Studente;
          return competenza
        }));
  };

  getAttivitaCompetenze(uuid: any) {
    let url = this.host + '/risorsa/' + uuid + '/competenze/studente/' + this.studenteId;
    return this.http.get(url)
      .timeout(this.timeout)
      .pipe(
        map(resp => {
          return resp;
        },
          catchError(this.handleError)
        )
      );
  }


  //NOTE
  saveNoteStudente(esperienzaId, nota) {
    let params = new HttpParams();
    params = params.append('note', nota);
    return this.http.post(this.attivitaNoteEndpoint + "/" + esperienzaId, {},
      {
        params: params,
        observe: 'response'
      })
      .timeout(this.timeout)
      .pipe(
        map(attivitaStudente => {
          return attivitaStudente;
        },
          catchError(this.handleError)
        )
      );
  }

  //DOCUMENTI
  getDocumentoById(documentoId) {
    return this.http.get(this.host + "/download/esperienzaSvolta/documento/" + documentoId, { responseType: 'text' })
      .timeout(this.timeout)
      .pipe(
        map(
          url => url,
          catchError(this.handleError)
        )
      );
  }

  createDocumento(esperienzaId, document: File, name: string) {
    let formData: FormData = new FormData();
    if (document) {
      formData.append('data', document, document.name);
    }

    let params = new HttpParams();
    params = params.append('nome', name);

    return this.http.post(this.host + "/attivita/esperienzaSvolta/" + esperienzaId + "/documento", formData,
      {
        params: params,
        observe: 'response'
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

  //GENERAL API
  getCompetenzeAPI(filterText: string, page: any, pageSize: any) {
    let url = this.host + this.competenzeAPIUrl;

    let params = new HttpParams();
    params = params.append('page', page);
    params = params.append('size', pageSize);

    if (filterText) {
      params = params.append('filterText', filterText);
    }

    return this.http.get<IPagedCompetenze>(
      url,
      {
        params: params,
        observe: 'response'
      })
      .timeout(this.timeout)
      .pipe(map(res => {
        let competencies = res.body as IPagedCompetenze;
        return competencies;
      }), catchError(this.handleError));

  }

  getAttivitaTipologie(): Observable<object[]> {
    return this.http.get<object[]>(this.attivitaTipologieEndpoint)
      .timeout(this.timeout)
      .pipe(
        map(tipologie => {
          return tipologie;
        },
          catchError(this.handleError)
        )
      );
  }

  getAttivitaGiornaliereReportStudenteById(id) {
    return this.http.get(this.attivitaGiornalieraListaEndpoint + '/esperienze/report/' + id)
      .timeout(this.timeout)
      .pipe(
        map(report => {
          return report
        },
          catchError(this.handleError)));
  }

  saveAttivitaGiornaliereStudentiPresenze(presenzeObject, esperienzaSvoltaId) {
    let url = this.host + '/studente/' + this.studenteId + '/esperienza/' + esperienzaSvoltaId + '/presenze';
    return this.http.post(url, presenzeObject)
      .timeout(this.timeout)
      .pipe(
        map(studenti => {
          return studenti
        },
          catchError(this.handleError))
      );
  }

  getStudenteAttivitaGiornalieraCalendario(idEsperienza, studenteId, dataInizio, dataFine) {
    let url = this.host + '/studente/' + studenteId + '/esperienza/' + idEsperienza + '/presenze';
    let params = new HttpParams();
    params = params.append('dateFrom', dataInizio);
    params = params.append('dateTo', dataFine);


    return this.http.get<any>(url,
      {
        observe: 'response',
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(resp => {
          return resp.body
        },
          catchError(this.handleError)
        )
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
          return res.body;
        }),
        catchError(this.handleError)
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
        catchError(this.handleError));
  }

  getAttivitaDocumenti(uuid): Observable<any> {
    let url = this.host + '/download/document/risorsa/' + uuid + '/studente/' + this.studenteId;

    return this.http.get(
      url,
      {
        observe: 'response',
      }
    )
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res.body;
        }),
        catchError(this.handleError));
  }

  uploadDocumentToRisorsa(file: File, uuid: string): Observable<any> {
    let url = this.host + '/upload/document/risorsa/' + uuid + '/studente/' + this.studenteId;
    let formData: FormData = new FormData();
    formData.append('data', file, file.name);
    let headers = new Headers();

    return this.http.post<any>(url, formData)
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res;
        }),
        catchError(this.handleError)
      )
  }

  downloadRisorsaDocumenti(id: any): Observable<any> {

    let url = this.host + '/download/document/risorsa/' + id + '/studente/' + this.studenteId;

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

  openDocument(doc) {
    let url = this.host + '/download/document/' + doc.uuid + '/studente/' + this.studenteId;

    this.http.get(url, {
      responseType: 'arraybuffer'
    }
    ).subscribe(response => this.downLoadFile(response, doc.formatoDocumento));

  }

  downLoadFile(data: any, type: string) {
    let blob = new Blob([data], { type: type });
    let url = window.URL.createObjectURL(blob);
    let pwa = window.open(url);

    if (!pwa || pwa.closed || typeof pwa.closed == 'undefined') {
      alert('Please disable your Pop-up blocker and try again.');
    }
  }

  deleteDocument(id: any): Observable<any> {
    let url = this.host + '/remove/document/' + id + '/studente/' + this.studenteId;

    return this.http.delete(url,
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