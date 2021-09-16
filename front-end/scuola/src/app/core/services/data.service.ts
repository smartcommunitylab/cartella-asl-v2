import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Rx';
import { of,forkJoin } from 'rxjs';  // RxJS 6 syntax
import { catchError, map, } from 'rxjs/operators';
import { Competenza } from '../../shared/classes/Competenza.class';
import { PianoAlternanza } from '../../shared/classes/PianoAlternanza.class';
import { IApiResponse } from '../../shared/classes/IApiResponse.class';
import { IPagedCompetenze } from '../../shared/classes/IPagedCompetenze.class';
import { GrowlerService, GrowlerMessageType } from '../growler/growler.service';
import { serverAPIConfig } from '../serverAPIConfig'
import { Azienda, IPagedAA } from '../../shared/interfaces';
import { AttivitaAlternanza } from '../../shared/classes/AttivitaAlternanza.class';
import { IPagedAzienda } from '../../shared/classes/Azienda.class';
import { DomSanitizer } from '@angular/platform-browser';
import { environment } from '../../../environments/environment';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable()
export class DataService {
  istitutoId: string = "";
  schoolYear: string = "";
  listIstituteIds = [];
  istituto: string = "Centro Formazione Professionale Agrario - S. Michele all'Adige'";
  host: string = serverAPIConfig.host;
  corsiStudio = this.host + '/corsiDiStudio/';
  corsoDiStudioAPIUrl: string = '/corsi';
  esperienzaSvoltaAPIUrl: string = '/esperienzaSvolta';
  attiitaAlternanzaAPIUrl: string = '/attivitaAlternanza'
  diarioDiBordoAPIUrl: string = "/diarioDiBordo"
  opportunitaAPIUrl: string = '/opportunita';
  static growler;
  timeout: number = 120000;
  coorindateIstituto;
  roles;

  constructor(
    private http: HttpClient,
    private growler: GrowlerService,
    private sanitizer: DomSanitizer) {
    DataService.growler = growler;
  }

  setIstitutoId(id) {
    if (id) {
      this.istitutoId = id;
    }
  }

  setSchoolYear(year) {
    if (year) {
      this.schoolYear = year;
    }
  }

  setIstitutoName(name) {
    if (name) {
      this.istituto = name;
    }
  }

  getIstitutoName(): string {
    return this.istituto;
  }

  setListId(list) {
    if (list) {
      this.listIstituteIds = list;
    }
  }

  setIstitutoPosition(coord) {
    this.coorindateIstituto = coord;
  }

  getIstitutoPosition() {
    return this.coorindateIstituto;
  }

  getListId() {
    if (this.listIstituteIds)
      return this.listIstituteIds;
  }

  setRoles(roles: any) {
    this.roles = roles;
  }

  validateRoles(rolesToCheck:any) {
    let valid = false;
    if (!!rolesToCheck && !!this.roles) {
      rolesToCheck.forEach(role=> {
        if (this.roles.findIndex(x => x.role == role) > -1) {
          valid = true;
          return valid;
        }
      })
    }    
    return valid;
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

  getSchoolYear(istitutoId, dataInizio): Observable<any> {
    let url = this.host + '/schoolYear/' + istitutoId;
    let params = new HttpParams();
    if (dataInizio)
      params = params.append('dateFrom', dataInizio);

    return this.http.get<any>(url, {
      params: params,
      observe: 'response'
    })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res.body;
        },
          catchError(this.handleError)
        )
      );
  }

  /** PIANO ALTERNANZA **/
  createPiano(piano): Observable<PianoAlternanza> {
    piano.istitutoId = this.istitutoId;
    piano.istituto = this.istituto;
    if (!piano.annoScolasticoAttivazione) {
      piano.annoScolasticoAttivazione = this.schoolYear;
    }
    let url = this.host + '/pianoAlternanza'
    return this.http.post<PianoAlternanza>(url, piano)
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res;
        },
          catchError(this.handleError)
        )
      );
  }

  updatePianoDetails(piano): Observable<PianoAlternanza> {
    let url = this.host + '/pianoAlternanza'
    return this.http.put<PianoAlternanza>(url, piano)
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res;
        },
          catchError(this.handleError)
        )
      );
  }

  getPianiPage(page: number, pageSize: number, filtro): Observable<any> {

    var params = new HttpParams();

    if (filtro.corsoStudioId != null)
      params = params.append("corsoDiStudioId", filtro.corsoStudioId + "");
    if (filtro.titolo != null)
      params = params.append('titolo', filtro.titolo);
    if (filtro.stato != null) {
      params = params.append('stato', filtro.stato);
    }

    params = params.append('annoScolastico', this.schoolYear);
    params = params.append('page', page + '');
    params = params.append('size', pageSize + '');
    let url = this.host + '/pianiAlternanza/' + this.istitutoId;

    return this.http.get<PianoAlternanza[]>(url,
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

  getPianoById(pianoId): Observable<PianoAlternanza> {
    let url = this.host + '/pianoAlternanza/' + pianoId;
    return this.http.get<PianoAlternanza>(url)
      .timeout(this.timeout)
      .pipe(
        map(piano => {
          return piano;
        },
          catchError(
            this.handleError
          )
        )
      );
  }

  uploadDocumentToRisorsa(option, uuid: string): Observable<any> {
    let url = this.host + '/upload/document/risorsa/' + uuid + '/istituto/' + this.istitutoId;
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
    let url = this.host + '/download/document/risorsa/' + id + '/istituto/' + this.istitutoId + '/attivita';

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

  downloadPianoDocumenti(id: any): Observable<any> {
    let url = this.host + '/download/document/risorsa/' + id + '/istituto/' + this.istitutoId + '/piano';

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

  deleteDocument(id: any): Promise<any> {
    let url = this.host + '/remove/document/' + id + '/istituto/' + this.istitutoId;

    return this.http.delete(url)
      .timeout(this.timeout)
      .toPromise().then(response => {
        return response;
      }
      ).catch(response => this.handleError);
  }

  downloadDocumentBlob(doc): Observable<any> {
    let url = this.host + '/download/document/' + doc.uuid + '/istituto/' + this.istitutoId;
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

  // openDocument(doc) {
  //   let url = this.host + '/download/document/' + doc.uuid + '/istituto/' + this.istitutoId;

  //   this.http.get(url, {
  //     responseType: 'arraybuffer'
  //   }
  //   ).subscribe(response => this.downLoadFile(response, doc.formatoDocumento));

  // }

  downLoadFile(data: any, type: string) {
    let blob = new Blob([data], { type: type });
    let url = window.URL.createObjectURL(blob);
    let pwa = window.open(url);

    if (!pwa || pwa.closed || typeof pwa.closed == 'undefined') {
      alert('Please disable your Pop-up blocker and try again.');
    }
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

  getPianoTipologie(id): Observable<any> {
    let url = this.host + '/pianoAlternanza/' + id + '/tipologie';

    return this.http.get<any>(url)
      .timeout(this.timeout)
      .pipe(
        map(tipologie => {
          return tipologie;
        },
          catchError(this.handleError)
        )
      );
  }

  getRisorsaCompetenze(id): Observable<any> {
    let url = this.host + '/risorsa/' + id + '/competenze/istituto/' + this.istitutoId;

    return this.http.get<any>(url)
      .timeout(this.timeout)
      .pipe(
        map(competenze => {
          return competenze;
        },
          catchError(this.handleError)
        )
      );
  }

  assignTipologieToPiano(tipologieToSave, pianoId): Observable<PianoAlternanza> {
    let url = this.host + '/pianoAlternanza/' + pianoId + '/tipologie';

    return this.http.put<PianoAlternanza>(url, tipologieToSave)
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res;
        },
          catchError(this.handleError)
        )
      );
  }

  assignCompetenzeToRisorsa(uuid, competenzeToSave) {
    let url = this.host + '/risorsa/' + uuid + '/competenze/istituto/' + this.istitutoId;
    let idCompetenze = [];
    competenzeToSave.forEach(element => {
      idCompetenze.push(element.id);
    });
    console.log(idCompetenze);

    return this.http.put<Competenza[]>(url, idCompetenze)
      .timeout(this.timeout)
      .pipe(
        map(competenze => {
          return competenze
        },
          catchError(
            this.handleError
          )
        )
      );
  }

  duplicaPiano(duplicaPiano) {
    let url = this.host + '/pianoAlternanza/duplica';

    return this.http.post<PianoAlternanza>(url, duplicaPiano)
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res;
        },
          catchError(this.handleError)
        )
      );
  }

  getDulicaPiano(id) {
    let url = this.host + '/pianoAlternanza/duplica/' + id;

    return this.http.get<PianoAlternanza>(url)
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res;
        },
          catchError(this.handleError)
        )
      );
  }

  activatePiano(id): Observable<PianoAlternanza> {
    let url = this.host + '/pianoAlternanza/activate/' + id;

    return this.http.put<PianoAlternanza>(url, {
      observe: 'response',
      responseType: 'text'
    })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res;
        }),
        catchError(this.handleError)
      );
  }

  deactivatePiano(id): Observable<PianoAlternanza> {
    let url = this.host + '/pianoAlternanza/deactivate/' + id;

    return this.http.put<PianoAlternanza>(url, null)
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res;
        }),
        catchError(this.handleError)
      );
  }

  deletePiano(id: string): Observable<boolean> {
    let url = this.host + '/pianoAlternanza/' + id;

    return this.http.delete<boolean>(url)
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res;
        },
          catchError(this.handleError)
        )
      );
  }

  getCorsiStudio() {
    return this.http.get(this.corsiStudio + this.istitutoId,
      {
        params: {
          annoScolastico: this.schoolYear
        }
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

  /** ATTIVITA ALTERNANZA **/
  createAttivitaAlternanza(attivita): Observable<AttivitaAlternanza> {
    let url = this.host + "/attivita";
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);

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

  associaOffertaToAttivita(offertaId, rendicontazioneCorpo): Observable<AttivitaAlternanza> {
    let url = this.host + "/attivita/offerta/" + offertaId + '/associa';
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);
    params = params.append('rendicontazioneCorpo', rendicontazioneCorpo);

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

  getAttivitaAlternanzaForIstitutoAPI(filter, page: any, pageSize: any): Observable<IPagedAA> {
    let url = this.host + "/attivita/search/";
    let headers = new HttpHeaders();
    let params = new HttpParams();

    if (filter.tipologia)
      params = params.append('tipologia', filter.tipologia);
    if (filter.titolo)
      params = params.append('text', filter.titolo);
    if (filter.stato != null)
      params = params.append('stato', filter.stato);

    params = params.append('istitutoId', this.istitutoId);
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
    let url = this.host + "/attivita/" + id;
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);

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
    let url = this.host + "/attivita/" + id + '/report/esperienze';
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);

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
    let url = this.host + "/attivita/" + id + '/presenze/individuale/report';
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);

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
    let url = this.host + "/attivita/" + id + '/presenze/gruppo/report';
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);

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
    let url = this.host + "/attivita/" + id + '/presenze/individuale';
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);
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
    let url = this.host + "/attivita/" + id + '/presenze/gruppo';
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);
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
    let url = this.host + "/attivita/" + id + "/presenze/individuale";
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);

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
    params = params.append('istitutoId', this.istitutoId);

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

  getAttivitaPresenzeCorpo(id: any) {
    let url = this.host + "/attivita/" + id + '/presenze/corpo';
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);

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

  validaPresenzeAttivitaCorpo(id: any, presenze: any[]) : Observable<any> {
    let url = this.host + "/attivita/" + id + "/presenze/corpo";
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);

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

  deleteAttivita(id: number): Observable<any> {
    let url = this.host + "/attivita/" + id;
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);

    return this.http.delete<any>(url,
      {
        observe: 'response',
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

  getAttivitaReportStudenti(id): Observable<any> {
    let url = this.host + "/attivita/" + id + '/report/studenti';
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);

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
    params = params.append('istitutoId', this.istitutoId);
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
    params = params.append('istitutoId', this.istitutoId);

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

  archiviaAttivita(id, esperienze) {
    let url = this.host + "/attivita/" + id + "/archivia";
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);

    return this.http.post(url, esperienze, { params: params })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res
        },
          catchError(this.handleError)
        )
      );
  }

  duplicaAA(aa): Observable<any> {
    let url = this.host + '/attivita/duplica';
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);
    params = params.append('attivitaId', aa.id);
    return this.http.post(url, null, { params: params })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res;
        },
          catchError(this.handleError)
        )
      );
  }

  /** OFFERTE */
  getOffeteForIstitutoAPI(filter, page: any, pageSize: any): Observable<IPagedAA> {
    let url = this.host + "/offerta/search/";
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

    params = params.append('istitutoId', this.istitutoId);
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
    let url = this.host + "/offerta";
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);

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
    let url = this.host + "/offerta/" + id;
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);

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
    let url = this.host + "/offerta/" + id;
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);

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
  getStudenteForIstituto(filtro: any, page, pageSize) {
    let url = this.host + "/studente/ricerca/" + this.istitutoId;
    let params = new HttpParams();
    params = params.append('annoScolastico', this.schoolYear);
    params = params.append('page', page);
    params = params.append('size', pageSize);

    if (filtro.text)
      params = params.append('text', filtro.text);
    if (filtro.corsoId)
      params = params.append('corsoId', filtro.corsoId);

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
    let url = this.host + "/studente/" + studenteId + '/istituto/report/details';
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);

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

  getRegistrazioneDocenteDetail(id) {
    let url = this.host + '/registrazione-docente/detail';
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);
    params = params.append('registrazioneId', id);

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

  /** COMPETENZA. **/
  getCompetenzeAPI(filterText: string, page: any, pageSize: any) {
    let url = this.host + '/competenze';
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

  getPagedCompetenzeOrderByIstitutoId(filter, page: any, pageSize: any) {
    let url = this.host + '/competenze/orderBy/istituto/' + this.istitutoId;
    let params = new HttpParams();
    params = params.append('page', page);
    params = params.append('size', pageSize);
    params = params.append('ownerIds', filter.owner);

    if (filter.filterText) {
      params = params.append('filterText', filter.filterText);
    }
    if (filter.stato != null) {
      params = params.append('stato', filter.stato);
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

  getIstiutoCompetenzaDetail(competenzaId: any): Observable<any> {
    let url = this.host + "/competenza/" + competenzaId;

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

  deleteCompetenzaIstituto(competenzaId: any): Observable<any> {
    let url = this.host + '/competenza/' + competenzaId;

    return this.http.delete<IApiResponse>(
      url,
      { observe: 'response', })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          if (res.ok)
            return true;
          else
            return res;
        }),
        catchError(this.handleError)
      );
  }

  addIstitutoCompetenza(competenza: Competenza): Observable<any> {
    let url = this.host + "/competenza/istituto/" + this.istitutoId;

    return this.http.post<Competenza>(
      url,
      competenza,
      { observe: 'response', })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          if (res.ok) {
            return (res.body as Competenza);
          } else
            return res;
        }
        ),
        catchError(this.handleError))
  }

  updateIstitutoCompetenza(competenza: Competenza): Observable<any> {
    let url = this.host + "/competenza/istituto/" + this.istitutoId;

    return this.http.put<Competenza>(
      url,
      competenza,
      { observe: 'response', })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          if (res.ok) {
            return (res.body as Competenza);
          } else
            return res;
        }
        ),
        catchError(this.handleError))
  }

  /** AZIENDE **/
  getListaAziende(term, page: any, pageSize: any) {
    let url = this.host + "/azienda/search";
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);
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

  searchEnte(term: string) {
    // if (term === '') {
    //   return of([]);
    // }

    let url = this.host + "/azienda/search";
    let params = new HttpParams();
    params = params.append('page', '0');
    params = params.append('size', '20');

    if (term) {
      params = params.append('text', term);
    }

    // return this.http
    //   .get(WIKI_URL, {params: PARAMS.set('search', term)}).pipe(
    //     map(response => response[1])
    //   );

    environment.globalSpinner = false;
    return this.http.get<any>(url,
      {
        observe: 'response',
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          environment.globalSpinner = true;
          return (res.body.content)
        }),
        catchError((err) => {
          environment.globalSpinner = true;
          return this.handleError(err);
        })
      );
  }

  getAzienda(aziendaId: any) {
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

  addAzienda(az): Observable<any> {
    let url = this.host + "/azienda";

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

  deleteAzienda(id: number): Observable<any> {
    let url = this.host + "/azienda/" + id;
    return this.http.delete<IApiResponse>(
      url,
      { observe: 'response', })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          if (res.ok)
            return true;
          else
            return res;
        }),
        catchError(this.handleError)
      );
  }

  creaRichiestaRegistrazione(az): Observable<any> {
    let url = this.host + "/registrazione-ente/richiesta";
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);
    params = params.append('enteId', az.id);
    params = params.append('email', az.email);
    return this.http.post<any>(
      url,
      null,
      { 
        params: params,
        observe: 'response',
       })
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

  annullaRichiestaRegistrazione(az): Observable<any> {
    let url = this.host + "/registrazione-ente/richiesta";
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);
    params = params.append('registrazioneId', az.registrazioneEnte.id);
    
    return this.http.delete<any>(url,
      {
        observe: 'response',
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

  /** ISTITUTO */
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

  updateIstituto(ist:any) {
    let url = this.host + '/istituto/';
    return this.http.put<any>(url, ist)
      .timeout(this.timeout)
      .pipe(
        map(res => {
          return res;
        },
          catchError(this.handleError)
        )
      );
  }

  getListaIstitutiByIds(ids: any): any {
    let singleObservables = ids.map((singleIds: string, urlIndex: number) => {
      return this.getIstitutoById(singleIds)
        .timeout(this.timeout)
        .map(single => single)
        .catch((error: any) => {
          console.error('Error loading Single, singleUrl: ' + singleIds, 'Error: ', error);
          return Observable.of(null);
        });
    });
    return forkJoin(singleObservables);
  }

  getDashboardIstitutoClasse(): Observable<String[]> {
    let url = this.host + '/dashboard-ist/classi';
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);
    params = params.append('annoScolastico', this.schoolYear);
    
    return this.http.get<String[]>(url,
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

  getDashboardIstitutoClasseReport(classe): Observable<any> {
    let url = this.host + '/dashboard-ist/rep-classe';
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);
    params = params.append('annoScolastico', this.schoolYear);
    params = params.append('classe', classe);

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

  getDashboardIstitutoSistemaReport(): Observable<any> {
    let url = this.host + '/dashboard-ist/rep-sistema';
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);
    params = params.append('annoScolastico', this.schoolYear);
   
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

  apiFormat(D) {
    var yyyy = D.getFullYear().toString();
    var mm = (D.getMonth() + 1).toString(); // getMonth() is zero-based         
    var dd = D.getDate().toString();

    return yyyy + '-' + (mm[1] ? mm : "0" + mm[0]) + '-' + (dd[1] ? dd : "0" + dd[0]);
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

  generateArray(obj) {
    return Object.keys(obj).map((key) => { return obj[key] });
  }

  getRiferente(term: string): Observable<any> {
    let url = this.host + "/registrazione-docente/search";
    let params = new HttpParams();
    params = params.append('page', '0');
    params = params.append('size', '20');
    params = params.append('istitutoId', this.istitutoId);
    
    if (term) {
      params = params.append('text', term);
    }

    environment.globalSpinner = false;
    return this.http.get<any>(url,
      {
        observe: 'response',
        params: params
      })
      .timeout(this.timeout)
      .pipe(
        map(res => {
          environment.globalSpinner = true;
          return (res.body.content)
        }),
        catchError((err) => {
          environment.globalSpinner = true;
          return this.handleError(err);
        })
      );
  }

  getRegistrazioneDocente() : Observable<any> {
    let url = this.host + "/registrazione-docente";
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);
    
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

  aggiungiDocentiAccount(ids: any) : Observable<AttivitaAlternanza> {
    let url = this.host + "/registrazione-docente";
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);
    
    return this.http.post<AttivitaAlternanza>(url, ids,
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
  
  getRegistrazioneDocenteClassiDaAssociare(page, pageSize, id: any, filterText) : Observable<any> {
    let url = this.host + "/registrazione-docente/reg/classi";
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);
    params = params.append('registrazioneId', id);
    params = params.append('annoScolastico', this.schoolYear);
    params = params.append('page', page + '');
    params = params.append('size', pageSize + '');

    if (filterText) {
      params = params.append('text', filterText);
    }
    
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

  updateAssociazioneDocentiClassi(id, ids: any) : Observable<AttivitaAlternanza> {
    let url = this.host + "/registrazione-docente/reg/classi";
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);
    params = params.append('registrazioneId', id);
    params = params.append('annoScolastico', this.schoolYear);
   
    return this.http.post<AttivitaAlternanza>(url, ids,
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

  listPerAggiungiAccount(page: number, pageSize: number, filtro) : Observable<any> {
    let url = this.host + "/registrazione-docente/docenti-classi";
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);
    params = params.append('annoScolastico', this.schoolYear);
    params = params.append('page', page + '');
    params = params.append('size', pageSize + '');
    if (filtro)
    params = params.append('text', filtro);
    
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

  deleteRegistrazioneDocente(id): Observable<any> {
    let url = this.host + "/registrazione-docente";
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);
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
        },
          catchError(this.handleError)
        )
      );
  }  

  getEnteConvenzione(enteId): Observable<any> {
    let url = this.host + "/convenzione/ente/" + enteId;
    let params = new HttpParams();
    params = params.append('istitutoId', this.istitutoId);

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