import { Competenza } from "./Competenza.class";

export class Opportunita {
    id: number;
    titolo: string;
    descrizione: string;
    tipo: number;
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