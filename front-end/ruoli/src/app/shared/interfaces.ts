import { Moment } from 'moment';


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

export class Valutazione {

    id?: number;
    url: string;
    name: string;
    type?: string;
}

export class AttivitaAlternanza {
    annoScolastico: string;
    annoCorso: number;
    interna: boolean;
    tipologia: number;
    individuale: boolean;
    titolo: string;
    ore: number;
    dataInizio: number;
    dataFine: number;
    oreInizio: string;
    OraFine: string;
    classe: string[];
    opportunita: IOffer;
    constructor() {}
    
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
    ore: number;
    postiDisponibili: number;
    referenteAzienda?: Riferente;
    competenze: Competenza[],
    azienda: Azienda;
    start: Moment;
    end: Moment;
    prerequisiti: string
}


export class Azienda {
    id: number;
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

export interface IPagedResults<T> {
    totalRecords: number;
    results: T;
}

