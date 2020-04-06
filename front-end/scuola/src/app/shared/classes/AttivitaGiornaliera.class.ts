import { Attivita } from './Attivita.class'
import { Competenza } from './Competenza.class';
export class AttivitaGiornaliera extends Attivita {
    competenze:Competenza[];
    ore:number;
    studenti;
}