import { ModuleWithProviders } from '@angular/core';
import { Routes } from '@angular/router';
import { Moment } from 'moment';
import { AttivitaAlternanza } from './classes/AttivitaAlternanza.class';
import { Studente } from './classes/Studente.class';

export class EsperienzaSvolta {
    id: string;
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

export interface IPagedAA { 

    totalPages: number;
    totalElements: number;
    content: AttivitaAlternanza[];

}

export class Documento {
    id: number;
    nome: string;
    url: string;
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
    address?: string;
    description?: string;
    email?: string;
    latitude: number;
    longitude: number;
    nome?: string;
    origin?: string;
    partita_iva?: string;
    pec?: string;
    phone?: string;
    idTipoAzienda;
    coordinate;
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
    oreSvolte: Number;
	data: Date;
    verificata: boolean;
    validataEnte: boolean;
    presenza: boolean;
    isModifiedState: boolean;
}

export interface IPagedResults<T> {
    totalRecords: number;
    results: T;
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

export interface IPagedResults<T> {
    totalRecords: number;
    results: T;
}

export interface IApiResponse {
    status: boolean;
    error?: string;
}