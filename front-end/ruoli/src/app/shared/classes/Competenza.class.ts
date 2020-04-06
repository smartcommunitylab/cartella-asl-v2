import { ConoscenzaCompetenza } from "./ConoscenzaCompetenza.class";
import { AbilitaCompetenza } from "./AbilitaCompetenza.class";

export class Competenza {
    id: string;
    idProfilo: string;
    livelloEQF: number;
    titolo: string;
    profilo: string;
    conoscenze: ConoscenzaCompetenza;
    abilita: AbilitaCompetenza;
    selected?: boolean;
}