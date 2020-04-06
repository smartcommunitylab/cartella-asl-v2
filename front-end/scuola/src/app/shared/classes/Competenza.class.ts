import { ConoscenzaCompetenza } from "./ConoscenzaCompetenza.class";
import { AbilitaCompetenza } from "./AbilitaCompetenza.class";

export class Competenza {
    id: string;
    abilita: string[];
    attiva: boolean;
    conoscenze: string[];
    classificationCode: string;
    livelloEQF: number;
    ownerId: string;
    ownerName: string;
    titolo: string;
    uri: string;    
    selected?: boolean;
}