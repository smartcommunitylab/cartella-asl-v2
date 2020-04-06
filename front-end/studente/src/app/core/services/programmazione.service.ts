import { Injectable } from '@angular/core';
import { PianoAlternanza } from '../../shared/classes/PianoAlternanza.class'
import { StatoProgramma } from '../../shared/classes/StatoProgramma.class'
import { OnInit } from '@angular/core/src/metadata/lifecycle_hooks';
import { DataService } from './data.service';

@Injectable()
export class ProgrammazioneService {


    constructor() { }
    getActualYear() {
        return "2017-18";
    }
    getStatoProgramma(piano: PianoAlternanza) {
        //check aanoScolasticoDisattivazione and return enum value of Stato Programma 
        return piano.stato;
        // let nowYear = this.getActualYear().substring(this.getActualYear().indexOf('-') + 1);
        // if (!piano.annoScolasticoDisattivazione) {
        //     return StatoProgramma.inCorso;
        // }
        // let pianoYear = piano.annoScolasticoDisattivazione.substring(piano.annoScolasticoDisattivazione.indexOf('-') + 1);
        // if (parseInt(nowYear) > (parseInt(pianoYear) + 3)) {
        //     return StatoProgramma.scaduto;
        // }
        // return StatoProgramma.inScadenza;
    }

    getDifferenzaAnni(piano: PianoAlternanza) {
        //check aanoScolasticoDisattivazione and return enum value of Stato Programma 
        let nowYear = this.getActualYear().substring(this.getActualYear().indexOf('-') + 1);
        if (!piano.annoScolasticoDisattivazione) {
            return 3;
        }
        let pianoYear = piano.annoScolasticoDisattivazione.substring(piano.annoScolasticoDisattivazione.indexOf('-') + 1);
        return ((parseInt(pianoYear) - parseInt(nowYear)));

    }
    getArrayStati() {
        return [
            // {
            //    "id":StatoProgramma.scaduto,
            //    "value":"scaduto"
            // },
            {
                "id": StatoProgramma.inScadenza,
                "value": "in scadenza"
            },
            {
                "id": StatoProgramma.inCorso,
                "value": "in corso"
            }
        ];
    }

}