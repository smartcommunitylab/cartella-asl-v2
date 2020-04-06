import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams, HttpHeaders } from '@angular/common/http';

import { Observable } from 'rxjs/Observable';
import { map, catchError } from 'rxjs/operators';

import { IPagedCompetenze, IOrder, IState, IPagedResults, IApiResponse, IOffer, IPagedOffers, EsperienzaSvolta, Competenza, Giornate, Valutazione, IPagedES, Azienda, TipologiaAzienda, IPagedAA, AttivitaAlternanza } from '../../shared/interfaces';


import { GrowlerService, GrowlerMessageType } from '../growler/growler.service';
import { serverAPIConfig } from '../serverAPIConfig'

@Injectable()
export class DataService {

    host: string = serverAPIConfig.host;

    aziendaId: string = '';
    listAziendaIds = [];
    // istitudeId: string = 'fdef62ac-63bf-4da8-9385-f33d7ee645ec';
    aziendaName: string = "";
    customersBaseUrl: string = '/api/customers';
    ordersBaseUrl: string = '/api/orders';

    competenzeBaseUrl: string = '/api/competenze';
    competenzeAPIUrl: string = '/competenze'

    /** esperienzaSvolta API(s) - CRUD **/
    //test
    esperienzaSvoltaBaseUrl: string = '/api/esperienzaSvolta';
    //real.
    esperienzaSvoltaAPIUrl: string = '/esperienzaSvolta';
    // aa
    attiitaAlternanzaAPIUrl: string = '/attivitaAlternanza'

    /** diarioDiBordo API(s) - CRUD */
    diarioDiBordoAPIUrl: string = "/diarioDiBordo"

    //attivita giornaliera
    private attivitaGiornalieraListaEndpoint = this.host + '/attivitaGiornaliera';


    /** oppurtunita API(s) - CRUD **/
    //test
    offersBaseUrl: string = '/api/offers';
    //real.
    opportunitaAPIUrl: string = '/opportunita';



    orders: IOrder[];
    states: IState[];

    // CRUD /opportunita/{aziendaId}
    opportunitaBaseUrl: string = "/api/opportunita/";

    private competenzeAttivitaEndpoint = 'api/competenze/attivita'

    static growler;

    timeout: number = 120000;

    coorindateAzienda;

    constructor(
        private http: HttpClient,
        private growler: GrowlerService) {
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
            this.listAziendaIds = list;
        }

    }
    getListId() {
        if (this.listAziendaIds)
            return this.listAziendaIds;


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

    /** DATA. **/
    getData(type: any): Observable<any> {

        let url = this.host + "/data";

        let params = new HttpParams();
        params = params.append('type', type);

        return this.http.get<any>(url,
            {
                params: params,
                observe: 'response'
            })
            .timeout(this.timeout)
            .pipe(
                map(res => {

                    return this.generateArray(res.body);
                }),
                catchError(this.handleError)
            );


    }


    generateArray(obj) {
        return Object.keys(obj).map((key) => { return obj[key] });
    }

    /** ACTIVITES/ESPERIENZA SVOLTA API BLOCK. */

    //GET /esperienzaSvolta/
    getEsperienzaSvoltaAPI(aziendaId: any, istitutoId: any, dataInizio: any, dataFine: any, stato: any, tipologia: any, filterText: any, page: any, pageSize: any): Observable<IPagedES> {

        let url = this.host + this.esperienzaSvoltaAPIUrl;

        let headers = new HttpHeaders();
        // headers = headers.append('Access-Control-Allow-Origin', '*');

        let params = new HttpParams();

        if (dataInizio)
            params = params.append('dataInizio', dataInizio);
        if (dataFine)
            params = params.append('dataFine', dataFine);
        if (stato)
            params = params.append('stato', stato);
        if (tipologia)
            params = params.append('tipologia', tipologia);
        if (filterText)
            params = params.append('filterText', filterText);
        if (istitutoId)
            params = params.append('istitutoId', istitutoId);

        params = params.append('aziendaId', aziendaId);
        // force individuale to true (students only)
        params = params.append('individuale', 'true');
        params = params.append('page', page);
        params = params.append('size', pageSize);

        return this.http.get<IPagedES>(
            url,
            {
                headers: headers,
                params: params,
                observe: 'response'
            })
            .timeout(this.timeout)
            .pipe(
                map(res => {
                    return (res.body as IPagedES);

                }),
                catchError(this.handleError)
            );
    }

    getAttivitaAlternanzaForAziendaAPI(aziendaId: any, istitutoId: any, dataInizio: any, dataFine: any, tipologia: any, filterText: any, page: any, pageSize: any): Observable<IPagedAA> {

        let url = this.host + this.attiitaAlternanzaAPIUrl + '/azienda';

        let headers = new HttpHeaders();
        // headers = headers.append('Access-Control-Allow-Origin', '*');

        let params = new HttpParams();

        params = params.append('aziendaId', aziendaId);

        if (istitutoId)
            params = params.append('istitutoId', istitutoId);
        if (dataInizio)
            params = params.append('dataInizio', dataInizio);
        if (dataFine)
            params = params.append('dataFine', dataFine);
        if (tipologia)
            params = params.append('tipologia', tipologia);
        if (filterText)
            params = params.append('filterText', filterText);

        params = params.append('page', page);
        params = params.append('size', pageSize);
        params = params.append('individuale', 'false');

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

    //GET /esperienzaSvolta/details/{id}

    getEsperienzaSvoltaByIdAPI(id: any): Observable<EsperienzaSvolta> {

        let url = this.host + this.esperienzaSvoltaAPIUrl + '/details/' + id;

        return this.http.get<EsperienzaSvolta>(url,
            {
                observe: 'response'
            })
            .timeout(this.timeout)
            .pipe(
                map(res => {
                    let attivita = res.body as EsperienzaSvolta;
                    return attivita;
                }),
                catchError(this.handleError)
            );
    }

    //GET /attivitaAlternanza/{id}
    getAttivitaAlternanzaByIdAPI(id: any): Observable<AttivitaAlternanza> {

        let url = this.host + this.attiitaAlternanzaAPIUrl + '/' + id;

        return this.http.get<AttivitaAlternanza>(url,
            {
                observe: 'response'
            })
            .timeout(this.timeout)
            .pipe(
                map(res => {
                    let attivita = res.body as AttivitaAlternanza;
                    return attivita;
                }),
                catchError(this.handleError)
            );
    }

    // GET /download/schedaValutazioneAzienda/{es_id}
    downloadschedaValutazione(id: any): Observable<Valutazione> {

        let url = this.host + '/download/schedaValutazioneAzienda/' + id;

        return this.http.get<Valutazione>(url,
            {
                observe: 'response'
            })
            .timeout(this.timeout)
            .pipe(
                map(res => {
                    return res.body as Valutazione;
                }),
                catchError(this.handleError)
            );
    }

    // upload /upload/schedaValutazioneAzienda/{es_id}
    uploadDocument(file: File, id: string): Observable<Valutazione> {

        let url = this.host + '/upload/schedaValutazioneAzienda/' + id;

        let formData: FormData = new FormData();
        formData.append('data', file, file.name);
        let headers = new Headers();

        return this.http.post<Valutazione>(url, formData)
            .timeout(this.timeout)
            .pipe(
                map(res => {
                    return res as Valutazione;
                }),
                catchError(this.handleError)
            )
    }

    // remove /remove/schedaValutazioneAzienda/{es_id}
    deleteDocument(id: any): Promise<any> {
        let body = {}

        let url = this.host + '/remove/schedaValutazioneAzienda/' + id;;

        return this.http.delete(url)
            .timeout(this.timeout)
            .toPromise().then(response => {
                return response;
            }
            ).catch(response => this.handleError);
    }

    // POST /schedaValutazioneAzienda/details/{es_id}
    insertValutazioneESAPI(valutazione: Valutazione): Observable<Valutazione> {

        let url = this.host + '/schedaValutazioneAzienda/' + valutazione.id;

        return this.http.post<Valutazione>(url, valutazione,
            {
                observe: 'response'
            })
            .timeout(this.timeout)
            .pipe(
                map(res => {
                    let attivita = res.body as Valutazione;
                    return attivita;
                }),
                catchError(this.handleError)
            );
    }


    // GET /diarioDiBordo/{id}/giorno/{data}
    initDiarioDiBordo(id: any, requestDate: Date): Promise<any> {

        let requestDayFormat = this.apiFormat(requestDate);
        let httpClient = this.http;
        let url = this.host + this.diarioDiBordoAPIUrl + '/' + id + "/giorno/" + requestDayFormat;
        let addGiornoUrl = this.host + this.diarioDiBordoAPIUrl + '/' + id + "/giorno";
        let giornoNuovo: Giornate = { attivitaSvolta: "", data: requestDate, presenza: false, verificata: false, id: null, isModifiedState: true };

        return new Promise<any>((resolve, reject) => {

            console.log(url);
            return this.http.get<Giornate>(url,
                {
                    observe: 'response'
                })
                .timeout(this.timeout)
                .toPromise().then(function (res) {
                    //  
                    let giorno = res.body as Giornate;

                    if (giorno) {
                        resolve(giorno);
                    } else {
                        //    console.log(giornoNuovo);
                        // create giorno.
                        httpClient.post<IApiResponse>(
                            addGiornoUrl,
                            giornoNuovo,
                            { observe: 'response', })
                            .timeout(this.timeout)
                            .toPromise().then(function (res) {
                            if (res.ok)
                                resolve(giornoNuovo);
                            else
                                reject();
                        }, function (err) {
                            reject(err);
                        }).catch(error => { reject(error) });


                    }
                }, function (err) {
                    reject(err);
                }).catch(error => { reject(error) })

        });

    }


    apiFormat(D) {
        var yyyy = D.getFullYear().toString();
        var mm = (D.getMonth() + 1).toString(); // getMonth() is zero-based         
        var dd = D.getDate().toString();

        return yyyy + '-' + (mm[1] ? mm : "0" + mm[0]) + '-' + (dd[1] ? dd : "0" + dd[0]);
    }


    // POST /diarioDiBordo/{id}/giorno
    addGiorno(id: any, giorno: Giornate): Promise<any> {

        return new Promise<any>((resolve, reject) => {

            let url = this.host + this.diarioDiBordoAPIUrl + '/' + id + "/giorno";
            return this.http.post<IApiResponse>(
                url,
                giorno,
                { observe: 'response', })
                .timeout(this.timeout)
                .toPromise()
                .then(function (res) {
                if (res.ok)
                    resolve(true);
                else
                    resolve(false);
            }, function (err) {
                reject(err);
            })

        });


    }


    // POST /diarioDiBordo/{id}/giorno
    updateGiorno(id: any, giorno: Giornate) {
        let url = this.host + this.diarioDiBordoAPIUrl + '/' + id + "/giorno";
        return this.http.put<IApiResponse>(
            url,
            giorno,
            { observe: 'response', })
            .timeout(this.timeout)
            .pipe(
                map(res => {
                    if (res.ok)
                        return true;
                    else
                        return res;
                }),
                catchError(this.handleError))

    }

    // GET /opportunita/{aziendaId}
    getPagedOppurtunitaAPI(aziendaId: any, dataInizio: any, dataFine: any, tipologia: any, filterText: any, page: any, pageSize: any): Observable<IPagedOffers> {

        let url = this.host + this.opportunitaAPIUrl + '/' + aziendaId;

        let params = new HttpParams();
        if (dataInizio)
            params = params.append('dataInizio', dataInizio);
        if (dataFine)
            params = params.append('dataFine', dataFine);

        params = params.append('page', page);
        params = params.append('size', pageSize);
        if (tipologia)
            params = params.append('tipologia', tipologia);
        if (filterText)
            params = params.append('filterText', filterText);


        return this.http.get<IPagedOffers>(
            url,
            {
                params: params,
                observe: 'response',
            })
            .timeout(this.timeout)
            .pipe(
                map(res => {
                    return (res.body as IPagedOffers);
                }),
                catchError(this.handleError)
            );
    }

    // GET /opportunita/details/{id}
    getOppurtunitaDetailAPI(id: any) {

        let url = this.host + this.opportunitaAPIUrl + '/details/' + id;

        return this.http.get<IOffer>(
            url,
            {
                observe: 'response',
            })
            .timeout(this.timeout)
            .pipe(
                map(res => {
                    let offer = res.body as IOffer;
                    return offer;
                }),
                catchError(this.handleError)
            );
    }

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

    // POST/opportunita/details/
    insertOppurtunitaAPI(offer: IOffer): Observable<IOffer> {
        let url = this.host + this.opportunitaAPIUrl + '/details/';
        return this.http.post<IOffer>(
            url,
            offer,
            { observe: 'response', })
            .timeout(this.timeout)
            .pipe(
                map(res => {
                    if (res.ok) {
                        return (res.body as IOffer);
                    } else
                        return res;
                }
                ),
            catchError(this.handleError))
    }

    // DELETE /opportunita/{id}
    deleteOppurtunita(id: number): Observable<boolean> {
        let url = this.host + this.opportunitaAPIUrl + "/" + id;
        return this.http.delete(
            url,
            {
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

    // PUT/opportunita/details/{id}
    updateOppurtunita(offer: IOffer) {
        let url = this.host + this.opportunitaAPIUrl + '/details/' + offer.id;

        return this.http.put(
            url,
            offer,
            {
                observe: 'response',
                responseType: 'text'
            })
            .timeout(this.timeout)
            .pipe(
                map(res => {
                    return res;
                }),
            catchError(this.handleError))
    }
    

    // PUT /opportunita/{id}/competenze
    updateCompetenze(id: any, listComptenze: any) {
        let url = this.host + this.opportunitaAPIUrl + '/' + id + "/competenze";
        return this.http.put<IApiResponse>(
            url,
            listComptenze,
            { observe: 'response', })
            .timeout(this.timeout)
            .pipe(
                map(res => {
                    if (res.ok)
                        return true;
                    else
                        return res;
                }),
            catchError(this.handleError))
    }
    
    // PUT /opportunita/competenze/{id}
    updateRiferente(id: any, rId: any): Observable<IOffer> {
        let url = this.host + this.opportunitaAPIUrl + '/' + id + "/referenteAzienda/" + rId;
        return this.http.put<IOffer>(
            url,
            { observe: 'response', })
            .timeout(this.timeout)
            .pipe(
                map(res => {
                    if (res) {
                        return (res as IOffer);
                    } else
                        return res;
                }
                ),
            catchError(this.handleError))
    }

    /** COMPETENZE **/
    getCompetenze() {
        return this.http.get<Competenza[]>(
            `${this.competenzeBaseUrl}`,
            { observe: 'response' })
            .timeout(this.timeout)
            .pipe(map(res => {
                let competencies = res.body as Competenza[];
                return competencies;

            }), catchError(this.handleError));

    }

    getCompetenzeByAttivita(attivitaId): Observable<Competenza[]> {
        return this.http.get<Competenza[]>(this.competenzeAttivitaEndpoint + "/" + attivitaId)
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

    getAttivitaTipologie(): Observable<object[]> {
        return this.http.get<object[]>(this.host + "/tipologieTipologiaAttivita")
            .timeout(this.timeout)
            .pipe(
                map(tipologie => {
                    return tipologie;
                },
                    catchError(this.handleError)
                )
            );
    }

    /**GET /esperienzaSvolta/ **/

    /* pageable:
       aziendaId : int,
       Istituto: string
       dataInizio: long // optional
       dataFine: long, // optional
       stato: [], // optional
       Tipologia: [int] // optional
       filterText: string // TODO
    */


    /** 
     * GET /esperienzaSvolta/details/{id}
     * **/

    /**
     * GET /schedaValutazione/details/{id}
     * Response: {...}
     * 
     * POST /schedaValutazione/details
     * Body: {Comment:””,evaluation:””}
     * 
     * PUT/schedaValutazione/details/{id}
     * Body: {* commento:””,valutazione:””}
     */

    /**
     * GET /diarioDiBordo/{id}
     * id: esperienzaSvolta
     * Response
     * {Giorni: [{attivitaSvolta:string, data: long, verificata: boolean, presenza: boolean}]}
     * TODO (Check which version is better to save complete diary or day by day.)
     */

    /** OFFER/OPPURTUNITA API BLOCK. */
    getOffersPage(page: number, pageSize: number): Observable<IPagedResults<IOffer[]>> {
        return this.http.get<IOffer[]>(
            `${this.offersBaseUrl}/page/${page}/${pageSize}`,
            { observe: 'response' })
            .timeout(this.timeout)
            .pipe(
                map(res => {
                    const totalRecords = +res.headers.get('X-InlineCount');
                    let offers = res.body as IOffer[];
                    //    this.calculateCustomersOrderTotal(customers);
                    return {
                        results: offers,
                        totalRecords: totalRecords
                    };
                }),
                catchError(this.handleError)
            );
    }

    getOffer(id: number): Observable<IOffer> {
        return this.http.get<IOffer>(this.offersBaseUrl + '/' + id)
            .timeout(this.timeout)
            .pipe(
                map(offer => {
                    return offer;
                }),
                catchError(this.handleError)
            );
    }

    insertOffer(offer: IOffer): Observable<IOffer> {
        return this.http.post<IOffer>(this.offersBaseUrl, offer)
            .timeout(this.timeout)
            .pipe(catchError(this.handleError))
    }

    updateOffer(offer: IOffer): Observable<boolean> {
        return this.http.put<IApiResponse>(this.offersBaseUrl + '/' + offer.id, offer)
            .timeout(this.timeout)
            .pipe(
                map(res => res.status),
                catchError(this.handleError)
            );
    }

    deleteOffer(id: number): Observable<boolean> {
        return this.http.delete<IApiResponse>(this.offersBaseUrl + '/' + id)
            .timeout(this.timeout)
            .pipe(
                map(res => res.status),
                catchError(this.handleError)
            );
    }

    /** TEST ENVIRONMENT. */
    getEsperienzaSvolta(page: number, pageSize: number): Observable<IPagedResults<EsperienzaSvolta[]>> {
        return this.http.get<EsperienzaSvolta[]>(
            `${this.esperienzaSvoltaBaseUrl}/page/${page}/${pageSize}`,
            { observe: 'response' })
            .timeout(this.timeout)
            .pipe(
                map(res => {
                    const totalRecords = +res.headers.get('X-InlineCount');
                    let offers = res.body as EsperienzaSvolta[];
                    //    this.calculateCustomersOrderTotal(customers);
                    return {
                        results: offers,
                        totalRecords: totalRecords
                    };
                }),
                catchError(this.handleError)
            );
    }

    getEsperienzaSvoltaById(id: number): Observable<EsperienzaSvolta> {
        return this.http.get<EsperienzaSvolta>(this.esperienzaSvoltaBaseUrl + '/' + id)
            .timeout(this.timeout)
            .pipe(
                map(activity => {
                    return activity;
                }),
                catchError(this.handleError)
            );
    }

    //NOTE
    saveNoteAzienda(esperienzaId, nota) {
        let params = new HttpParams();
        params = params.append('note', nota);
        return this.http.post(this.host + this.esperienzaSvoltaAPIUrl + "/noteAzienda/" + esperienzaId, {},
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


    getStates(): Observable<IState[]> {
        return this.http.get<IState[]>('/api/states')
            .timeout(this.timeout).pipe(catchError(this.handleError));
    }

    saveAttivitaGiornaliereStudentiPresenze(presenzeObject) {
        return this.http.put(this.attivitaGiornalieraListaEndpoint + '/calendario', presenzeObject,
            {
                observe: 'response',
                responseType: 'text'
            })
            .timeout(this.timeout)
            .pipe(
                map(studenti => {
                    return studenti
                },
                    catchError(this.handleError)
                )
            );
    }

    getAttivitaGiornalieraCalendario(idEsperienza) {
        return this.http.get<any>(this.attivitaGiornalieraListaEndpoint + "/calendario/" + idEsperienza)
            .timeout(this.timeout)
            .pipe(
                map(attivita => {
                    return attivita
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
            })
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

        let displayGrowl:boolean = true;
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
