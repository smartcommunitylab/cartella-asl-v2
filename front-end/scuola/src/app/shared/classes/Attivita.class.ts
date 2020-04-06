import { Competenza } from "./Competenza.class";
import { Opportunita } from "./Opportunita.class";

export class Attivita {
    id: number;
    istitutoId: string;
    istituto: string;
    corso: string;
    corsoId: number;
    titolo: string;
    descrizione: string;
    tipologia: number;
    tipologia_descrizione: string;
    dataInizio: number;
    dataFine:number;
    oraInizio: string;
    oraFine: string;
    ore: number;
    classe: string;
    referenteScuola: string;
    referenteFormazione: string;
    referenteFormazioneCF: string;
    formatore: string;
    formatoreCF: string;
    coordinatore: string;
    coordinatoreCF: string;
    competenze:Competenza[];
    coordinate;
    completata;
    opportunita:Opportunita;
    corsoInterno;
    
    constructor() {}
}