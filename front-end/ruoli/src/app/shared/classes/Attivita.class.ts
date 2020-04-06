import { Competenza } from "./Competenza.class";

export class Attivita {
    id: number;
    istitutoId: string;
    corso: string;
    corsoId: number;
    titolo: string;
    descrizione: string;
    tipologia: number;
    dataInizio: number;
    dataFine:number;
    oraInizio: string;
    oraFine: string;
    classe: string;
    referenteFormazione: string;
    formatore: string;
    coordinatore: string;
    competenze:Competenza[];
    
    constructor() {}
}