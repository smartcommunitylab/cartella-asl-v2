export class AttivitaAlternanza {
    id;
    istitutoId: string;
    titolo: string;    
    tipologia: number;
    annoScolastico: string;
    stato;
    descrizione;
    dataInizio;
    dataFine;
    oraInizio;
    oraFine;
    ore: number;
    offertaId;
    titoloOfferta;
    enteId;
    nomeEnte;
    referenteScuola: string;
    referenteScuolaCF: string;
    referenteEsterno: string;
    referenteEsternoCF: string;
    formatore: string;
    formatoreCF: string;
    luogoSvolgimento: string;
    latitude;
    longitude;
    dataArchiviazione: string;
    uuid: string;
    individuale;
    classi;
    classSet;
    groupRigaTip: any;
    studenti: any;
    rendicontazioneCorpo: boolean;
    referenteScuolaEmail: string;
    constructor() {}
    
}