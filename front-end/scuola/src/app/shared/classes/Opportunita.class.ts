import { Competenza } from "./Competenza.class";

export class Opportunita {
    id: number;
    istituteId: string;
    titolo: string;
    descrizione: string;
    tipo: number;
    dataInizio: number;
    dataFine:number;
    oraInizio: string;
    oraFine: string;
    classe: string;
    referenteFormazione: string;
    referenteFormazioneCF: string;
    referente: string;
    referenteCF: string;
    formatore: string;
    coordinatore: string;
    competenze:Competenza[];
    ore:number;
    tipologia;
    
    constructor() {}
}