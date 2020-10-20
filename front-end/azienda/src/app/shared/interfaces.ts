import { ModuleWithProviders } from '@angular/core';
import { Routes } from '@angular/router';
import { Moment } from 'moment';

export interface ICustomer {
    id: number;
    firstName: string;
    lastName: string;
    gender: string;
    address: string;
    city: string;
    state: IState;
    orders?: IOrder[];
    orderTotal?: number;
    latitude?: number,
    longitude?: number
}


export class EsperienzaSvolta {
    id: number;
    stato: number;
    noteStudente: string;
    noteAzienda: string;
    completato: boolean;
    studente: Studente;
    attivitaAlternanza: AttivitaAlternanza;
    documento: Documento[];
    presenzeContoInterno: any;
    schedaValutazioneStudente: any;
    schedaValutazioneAzienda: Valutazione;
    diarioDiBordo: any;
    reportValutazione: any
    
}

export interface IPagedES { 

    totalPages: number;
    totalElements: number;
    content: EsperienzaSvolta[];

}
    
export class Valutazione {

    id?: number;
    url: string;
    name: string;
    type?: string;
}

export class AttivitaAlternanza {
    titolo: string;
    istituto: string;
    annoScolastico: string;
    annoCorso: number;
    interna: boolean;
    tipologia: number;
    individuale: boolean;
    ore: number;
    dataInizio: number;
    dataFine: number;
    oreInizio: string;
    OraFine: string;
    classe: string[];
    opportunita: IOffer;
    constructor() {}
    
}

export interface IPagedAA { 

    totalPages: number;
    totalElements: number;
    content: AttivitaAlternanza[];

}

export class Documento {
    id: number;
    nome: string
    constructor() {}
}

export class Studente {
    id:string;
    origin?:string;
    extId?:string;
    name:string;
    surname:string;
    imageUrl:string;
    cf:string;
    birthdate:string;
    address:string;
    phone:string;
    mobilePhone:string;
    email:string;
    socialMap: any = {};
    constructor() {}
}

export interface IOffer {
    id: number;
    titolo: string;
    descrizione: string;
    tipologia: number;
    dataInizio: number;
    dataFine: number;
    oraInizio: string;
    oraFine: string;
    ore: number;
    postiDisponibili: number;
    postiRimanenti: number;
    referenteAzienda?: Riferente;
    competenze: Competenza[],
    azienda: Azienda;
    start: Moment;
    end: Moment;
    prerequisiti: string;
    referente: string;
    referenteCF: string;
    coordinate;

}

export interface IPagedOffers { 

    totalPages: number;
    totalElements: number;
    content: IOffer[];

}

export interface IPagedCompetenze { 

    totalPages: number;
    totalElements: number;
    content: Competenza[];

}

export class Azienda {
    id;
    nome?: string;
    convenzione?: Convenzione;
    referentiAzienda?: Riferente[];
}

export class Convenzione{
    convenzionePAT: {};
    convenzioneIstituto: {}
  }

export class Competenza {
    id: string;
    idProfilo: string;
    livelloEQF: number;
    titolo: string;
    profilo: string;
    conoscenze: ConoscenzaCompetenza;
    abilita: AbilitaCompetenza;
    selected: boolean;
}

export class ConoscenzaCompetenza {
    id_competenza: string;
    id_conoscenza: string;
    descrizione: string;
    constructor() {}
}

export class AbilitaCompetenza {
    id_competenza: string;
    id_conoscenza: string;
    descrizione: string;
    constructor() {}
}

export class ProfiloCompetenza {
    id_profilo: string;
    titolo: string;
    descrizione: string;
    constructor() {}
}

export class Riferente {
    id: string;
    nome?: string;
    imageLink?: string;
    selected?: boolean;
}


export class DiarioDiBordo {
    id: number;
    giornate: Giornate[]

}

export class Giornate {
    id: number;
    attivitaSvolta: string;
	data: Date;
	verificata: boolean;
    presenza: boolean;
    isModifiedState: boolean;
}


export interface IState {
    abbreviation: string;
    name: string;
}

export interface IOrder {
    productName: string;
    itemCost: number;
}

export interface IOrderItem {
    id: number;
    productName: string;
    itemCost: number;
}

export interface IPagedResults<T> {
    totalRecords: number;
    results: T;
}

export interface IUserLogin {
    email: string;
    password: string;
}

export interface IApiResponse {
    status: boolean;
    error?: string;
}

export class TipologiaAzienda {
    id: number;
    titolo: string;
    descrizione?: string;
    selected?: boolean;
}

export class Stato {
    id: number;
    titolo: string;
    selected?: boolean
}
