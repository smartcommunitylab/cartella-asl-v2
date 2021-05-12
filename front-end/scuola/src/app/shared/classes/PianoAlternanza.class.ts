export class AttivitaPiano {
    titolo:string;
    tipologia: number;
    preventivoOre: number;
    anno;
    id;
}

export class PianoAlternanza {
    id:string;
    corsoDiStudioId:string;
    corsoDiStudio:string;
    titolo:string;
    stato: any;
    oreSecondoAnno: number;
    oreTerzoAnno: number;
    oreQuartoAnno: number;
    oreQuintoAnno: number;
    ore: number;
    periodo;
    inizioValidita:number;
    fineValidita:number;
    dataAttivazione:any;
    competenze: any[];
    note: string;
    uuid: string;
    anni: string;   
    pianoCorrelato: PianoAlternanza;
    corsoSperimentale: boolean;
}