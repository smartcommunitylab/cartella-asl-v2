import { Competenza } from "./Competenza.class";

export class AttivitaPiano {
    titolo:string;
    tipologia:number;
    anno;
    id;
}

export class PianoAlternanza {
    id:string;
    corsoDiStudioId:string;
    titolo:string;
    attivo:boolean;
    inUso:boolean;
    inizioValidita:number;
    fineValidita:number;
    annoScolasticoAttivazione:string;
    annoScolasticoDisattivazione:string;
    stato:number;
    anniAlternanza:{
        id,
        nome,
        oreTotali,
        tipologieAttivita:AttivitaPiano[]
    }[]
    competenze:any[];
}