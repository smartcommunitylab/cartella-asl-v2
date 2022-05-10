import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Rx';
import { catchError, map } from 'rxjs/operators';
import { IPagedIstituto } from '../../shared/classes/IPagedIstituto.class';
import { GrowlerService, GrowlerMessageType } from '../growler/growler.service';
import { Azienda, IPagedAA } from '../../shared/interfaces';
import { AttivitaAlternanza } from '../../shared/classes/AttivitaAlternanza.class';
import { IPagedAzienda } from '../../shared/classes/Azienda.class';
import { DomSanitizer } from '@angular/platform-browser';
import * as moment from 'moment';
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
  host: string;
  corsoDiStudioAPIUrl: string = '/corsi';
  esperienzaSvoltaAPIUrl: string = '/esperienzaSvolta';
  attiitaAlternanzaAPIUrl: string = '/attivitaAlternanza'
  diarioDiBordoAPIUrl: string = "/diarioDiBordo"
  opportunitaAPIUrl: string = '/opportunita';
  static growler;
  timeout: number = 120000;
  coorindateIstituto;
  aziendaId: string = '';
  ownerId;
  listAziendaIds = [];
  aziendaName: string = "";
  coorindateAzienda;
  atecoURL = 'assets/ateco/data.csv';
  atecoData: any[] = [];

  constructor(
    private http: HttpClient,
    private growler: GrowlerService,
    private sanitizer: DomSanitizer) {
    DataService.growler = growler;
    this.host = environment.serverAPIURL;
  }

  setAtecoData() {
    this.http.get(this.atecoURL, { responseType: 'text' }).subscribe(
      data => {
        const list = data.split('\n');
        list.forEach(e => {
          var item = e.split(',');
          let entry = {};
          entry['codice'] = item[0];
          entry['descrizione'] = item[1];
          this.atecoData.push(entry);
        });
      });
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

  setOwnerId(id) {
    if (id) {
      this.ownerId = id;
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

  getAziendaInfoRiferente(aziendaId: any) {
    let url = this.host + "/azienda/" + aziendaId + '/ente';
    
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

  getRuoliByEnte() {
    let url = this.host + "/registrazione-ente/ruoli";

    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);

    return this.http.get<any>(url,
      {
        params: params,
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
    if (filter.istitutoId != null)
      params = params.append('istitutoId', filter.istitutoId);

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

  getAttivitaPresenzeGruppoReport(id: any): Observable<any> {
    let url = this.host + "/attivita/" + id + '/presenze/gruppo/report/ente';
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

  getAttivitaPresenzeGruppoListaGiorni(id: any, dataInizio, dataFine): Observable<any> {
    let url = this.host + "/attivita/" + id + '/presenze/gruppo/ente';
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

  validaPresenzeAttivitaGruppo(id, presenze): Observable<any> {
    let url = this.host + "/attivita/" + id + "/presenze/gruppo";
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

  getAttivitaRegistrations(filterText, id: number, page: any, pageSize: any) {
    let url = this.host + "/attivita/" + id + '/registrazioni';
    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);
    params = params.append('page', page);
    params = params.append('size', pageSize);

    if (filterText) {
      params = params.append('text', filterText);
    }

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

  assignStudentiToAttivita(id: any, studenti): Observable<any> {
    let url = this.host + "/attivita/" + id + '/studenti';
    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);

    return this.http.post<any>(url, studenti,
      {
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res;
        }
        ),
        catchError(this.handleError)
      );
  }

  associaOffertaToAttivita(offertaId): Observable<AttivitaAlternanza> {

    let url = this.host + "/attivita/offerta/" + offertaId + '/associa';

    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);

    return this.http.post<AttivitaAlternanza>(url, null,
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

  /** DOCUMENTI **/
  uploadDocumentToRisorsa(option, uuid: string): Observable<any> {
    let url = this.host + '/upload/document/risorsa/' + uuid + '/ente/' + this.aziendaId;
    let formData: FormData = new FormData();
    formData.append('data', option.file, option.file.name);
    formData.append('tipo', option.type);
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

  downloadAttivitaDocumenti(id: any): Observable<any> {
    let url = this.host + '/download/document/risorsa/' + id + '/ente/' + this.aziendaId;
    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);
    return this.http.get<any>(url,
      {
        observe: 'response',
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res.body;
        }),
        catchError(this.handleError)
      );
  }

  deleteDocument(id: any): Promise<any> {
    let body = {}
    let url = this.host + '/remove/document/' + id + '/ente/' + this.aziendaId;

    return this.http.delete(url)
      .timeout(this.timeout)
      .toPromise().then(response => {
        return response;
      }
      ).catch(response => this.handleError);
  }

  downloadDocumentBlob(doc): Observable<any> {
    let url = this.host + '/download/document/' + doc.uuid + '/ente/' + this.aziendaId;
    return this.http.get(url,
      {
        responseType: 'arraybuffer'
      })
      .timeout(this.timeout)
      .map(data => {
        const blob = new Blob([data], { type: doc.formatoDocumento });
        const url = URL.createObjectURL(blob);
        return url;
        //doc.url = this.sanitizer.bypassSecurityTrustResourceUrl(window.URL.createObjectURL(blob));
      },
        catchError(this.handleError)
      );
  }

  downLoadFile(data: any, type: string) {
    let blob = new Blob([data], { type: type });
    let url = window.URL.createObjectURL(blob);
    let pwa = window.open(url);

    if (!pwa || pwa.closed || typeof pwa.closed == 'undefined') {
      alert('Please disable your Pop-up blocker and try again.');
    }
  }

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

  getValutazioneCompetenzeReport(esperienzaSvoltaId): Observable<any> {
    let url = this.host + '/valutazione/competenze/ente';
    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);
    params = params.append('esperienzaSvoltaId', esperienzaSvoltaId);

    return this.http.get<any>(url,
      {
         params: params
      }
    ).timeout(this.timeout)
      .pipe(
        map(report => {
          return report;
        },
          catchError(this.handleError)
        )
      );
  }

  saveValutazioneCompetenze(esperienzaSvoltaId, valutazioni): Observable<any> {
    let url = this.host + '/valutazione/competenze/ente';
    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);
    params = params.append('esperienzaSvoltaId', esperienzaSvoltaId);

    return this.http.post<any>(url, valutazioni,
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

  associaIstitutiToOfferta(id, istitutiToSave) {
    let url = this.host + '/offerta/' + id + '/istituti';
    let istitutoStub = [];
    istitutiToSave.forEach(element => {
      istitutoStub.push({ istitutoId: element.istitutoId, nomeIstituto: element.nomeIstituto });
    });
    console.log(istitutoStub);
    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);

    return this.http.post<any[]>(url, istitutoStub,
      {
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(istituti => {
          return istituti
        }),
        catchError(this.handleError)
      );
  }


  /** OFFERTE */
  getOffeteForIstitutoAPI(filter, page: any, pageSize: any): Observable<IPagedAA> {

    let url = this.host + "/offerta/search/ente";
    let headers = new HttpHeaders();
    let params = new HttpParams();

    if (filter.tipologia)
      params = params.append('tipologia', filter.tipologia);
    if (filter.titolo)
      params = params.append('text', filter.titolo);
    if (filter.stato)
      params = params.append('stato', filter.stato);
    if (filter.ownerIstituto != null) {
      params = params.append('ownerIstituto', filter.ownerIstituto);
    }

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

  createOfferta(offerta): Observable<AttivitaAlternanza> {

    let url = this.host + "/offerta/ente";
    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);

    return this.http.post<any>(url, offerta,
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
        ,
        catchError(this.handleError)
      );
  }

  getOfferta(id): Observable<any> {
    let url = this.host + "/offerta/" + id + '/ente';
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

  deleteOfferta(id: number): Observable<any> {
    let url = this.host + "/offerta/" + id + '/ente';
    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);

    return this.http.delete<any>(url,
      {
        observe: 'response',
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

  /** STUDENTI */
  getStudenteForEnte(filtro: any, page, pageSize) {
    let url = this.host + "/studente/ricerca/" + this.aziendaId + '/ente';
    let params = new HttpParams();
    params = params.append('page', page);
    params = params.append('size', pageSize);

    if (filtro.text)
      params = params.append('text', filtro.text);

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

  getStudenteDettaglio(studenteId: any): Observable<any> {
    let url = this.host + "/studente/dettaglio/" + this.aziendaId + '/ente';
    let params = new HttpParams();
    params = params.append('studenteId', studenteId);

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

  /** EXPORT DATI **/
  getEnteStudenteCsv(studenteId: string): Observable<any> {
    let url = this.host + '/export/csv/ente/studente';
    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);
    params = params.append('studenteId', studenteId);
    return this.http.get(
      url,
      {
        observe: 'response',
        params: params,
        responseType: 'arraybuffer'
      })
      .timeout(this.timeout)
      .map((response) => {
        const blob = new Blob([response.body], { type: response.headers.get('Content-Type')! });
        const url = URL.createObjectURL(blob);
        const disposition = response.headers.get('Content-Disposition')!;
        const filename = disposition.substring(disposition.indexOf('=') + 1).replace(/\\\"/g, '');
        let doc = {
          'url': url,
          'filename': filename
        };
        return doc;
      },
        catchError(this.handleError)
      );
  }

  getEsperienzeStudenteCsv(studente: any): Observable<any> {
    let url = this.host + '/export/csv/studente';
    let params = new HttpParams();
    params = params.append('istitutoId', studente.istitutoId);
    params = params.append('studenteId', studente.id);
    return this.http.get(
      url,
      {
        observe: 'response',
        params: params,
        responseType: 'arraybuffer'
      })
      .timeout(this.timeout)
      .map((response) => {
        const blob = new Blob([response.body], { type: response.headers.get('Content-Type')! });
        const url = URL.createObjectURL(blob);
        const disposition = response.headers.get('Content-Disposition')!;
        const filename = disposition.substring(disposition.indexOf('=') + 1).replace(/\\\"/g, '');
        let doc = {
          'url': url,
          'filename': filename
        };
        return doc;
      },
        catchError(this.handleError)
      );
  }

  getEsperienzeClasseCsv(studente: any): Observable<any> {
    let url = this.host + '/export/csv/classe';
    let params = new HttpParams();
    params = params.append('istitutoId', studente.istitutoId);
    params = params.append('annoScolastico', studente.annoScolastico);
    params = params.append('corsoId', studente.corsoDiStudio.courseId);
    params = params.append('corso', studente.corsoDiStudio.nome);
    params = params.append('classe', studente.classroom);
    return this.http.get(
      url,
      {
        observe: 'response',
        params: params,
        responseType: 'arraybuffer'
      })
      .timeout(this.timeout)
      .map((response) => {
        const blob = new Blob([response.body], { type: response.headers.get('Content-Type')! });
        const url = URL.createObjectURL(blob);
        const disposition = response.headers.get('Content-Disposition')!;
        const filename = disposition.substring(disposition.indexOf('=') + 1).replace(/\\\"/g, '');
        let doc = {
          'url': url,
          'filename': filename
        };
        return doc;
      },
        catchError(this.handleError)
      );
  }

  /** ISTITUTI. **/
  searchIstitutiAPI(dataInizio, dataFine, filterText: string, page: any, pageSize: any) {
    let url = this.host + '/istituto/offerta';
    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);
    params = params.append('dateFrom', dataInizio);
    params = params.append('dateTo', dataFine);
    params = params.append('page', page);
    params = params.append('size', pageSize);

    if (!filterText) {
      filterText = '';
    }
    params = params.append('text', filterText);


    return this.http.get<IPagedIstituto>(
      url,
      {
        params: params,
        observe: 'response'
      })
      .timeout(this.timeout)
      .pipe(map(res => {
        let istituti = res.body as IPagedIstituto;
        return istituti;

      }), catchError(this.handleError));

  }

  /** AZIENDE **/
  getListaAziende(term, page: any, pageSize: any) {
    let url = this.host + "/azienda/search";
    let params = new HttpParams();
    params = params.append('page', page);
    params = params.append('size', pageSize);

    if (term) {
      params = params.append('text', term);
    }

    console.log(url);
    return this.http.get<IPagedAzienda>(url,
      {
        observe: 'response',
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return (res.body as IPagedAzienda)
        }),
        catchError(this.handleError)
      )
  }

  // GET /azienda/{id}
  getAzienda() {
    let url = this.host + "/azienda/" + this.aziendaId + "/ente";

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

  addAzienda(az): Observable<any> {
    let url = this.host + "/azienda/ente";

    return this.http.post<Azienda>(
      url,
      az,
      { observe: 'response', }
    )
      .timeout(this.timeout)
      .pipe(
        map(res => {
          if (res.ok) {
            return (res.body as Azienda);
          } else
            return res;
        }
        ),
        catchError(this.handleError))
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

    return this.http.get(
      this.host + "/list/aziende/" + this.istitutoId,
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

  
  aggiungiRuoloReferenteAzienda(role): Observable<any> {
    let url = this.host + "/registrazione-ente/ref-azienda";
    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);
    params = params.append('nome', role.name);
    params = params.append('cognome', role.surname);
    params = params.append('email', role.email);
    params = params.append('cf', role.cf);
    params = params.append('ownerId', this.ownerId);
    return this.http.post<any>(
      url,
      null,
      { 
        params: params,
        observe: 'response',
      }
    )
      .timeout(this.timeout)
      .pipe(
        map(res => {
          if (res.ok) {
            return (res.body);
          } else
            return res;
        }
        ),
        catchError(this.handleError))
  }

  cancellaRuoloReferenteAzienda(id): Observable<any> {
    let url = this.host + '/registrazione-ente/ref-azienda';
    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);
    params = params.append('registrazioneId', id);
    
    return this.http.delete<any>(url,
      {
        observe: 'response',
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

  getAteco(code: string): Observable<any> {
    console.log(this.atecoData);
    let result: any[] = [];
    if (code.length > 1) {
      result = this.atecoData.filter(x => x.codice.startsWith(code));
    } 
    return Observable.of(result);
  }

  getAnnoScolstico(now) {
    var annoScolastico;
    var lastDay = moment().month(8).date(1);
    if (now.isBefore(lastDay)) {
      annoScolastico = moment().year(now.year() - 1).format('YYYY') + '-' + now.format('YY');
    } else {
      annoScolastico = now.format('YYYY') + '-' + moment().year(now.year() + 1).format('YY');
    }
    return annoScolastico;
  }

  formatTwoDigit(n) {
    n = parseInt(n); //ex. if already passed '05' it will be converted to number 5
    var ret = n > 9 ? "" + n : "0" + n;
    return ret;
  }

  /* istituti */
  getPagedIstitutiOrderByIstitutoId(filtro: any, page, pageSize) {
    let url = this.host + "/istituto/search/ente";
    let params = new HttpParams();
    params = params.append('enteId', this.aziendaId);
    params = params.append('page', page);
    params = params.append('size', pageSize);

    if (filtro.filterText)
      params = params.append('text', filtro.filterText);

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

  getistitutoDettaglio(id: any): Observable<any> {
    let url = this.host + "/istituto/" + id + '/ente';
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

  downloadDocumentConvenzioneBlob(doc): Observable<any> {
    let url = this.host + '/download/document/convenzione/' + doc.uuid + '/ente/' + this.aziendaId;
    return this.http.get(url,
      {
        responseType: 'arraybuffer'
      })
      .timeout(this.timeout)
      .map(data => {
        const blob = new Blob([data], { type: doc.formatoDocumento });
        const url = URL.createObjectURL(blob);
        return url;
        //doc.url = this.sanitizer.bypassSecurityTrustResourceUrl(window.URL.createObjectURL(blob));
      },
        catchError(this.handleError)
      );
  }

  getEsperienzeistitutoCsv(istitutoId: any): Observable<any> {
    let url = this.host + '/export/csv/ente/istituto';
    let params = new HttpParams();
    params = params.append('istitutoId', istitutoId);
    params = params.append('enteId', this.aziendaId);
    return this.http.get(
      url,
      {
        observe: 'response',
        params: params,
        responseType: 'arraybuffer'
      })
      .timeout(this.timeout)
      .map((response) => {
        const blob = new Blob([response.body], { type: response.headers.get('Content-Type')! });
        const url = URL.createObjectURL(blob);
        const disposition = response.headers.get('Content-Disposition')!;
        const filename = disposition.substring(disposition.indexOf('=') + 1).replace(/\\\"/g, '');
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