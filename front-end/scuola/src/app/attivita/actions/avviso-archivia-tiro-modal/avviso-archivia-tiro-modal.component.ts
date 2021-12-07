import { Component, Output, Input, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { LocalizedDatePipe } from '../../../shared/pipes/localizedDatePipe';
import { DataService } from '../../../core/services/data.service';

@Component({
  selector: 'avviso-archivia-tiro-modal',
  templateUrl: './avviso-archivia-tiro-modal.html',
  styleUrls: ['./avviso-archivia-tiro-modal.scss']
})
export class AvvisoArchiviaTiroModal {
  closeResult: string;

  @Input() esperienza;
  @Input() ente;
  @Output() onArchivia = new EventEmitter<Object>();
  valutazionEsperienzaTiro;
  valutazionCompetenzeTiro;
  
  constructor(public activeModal: NgbActiveModal,
     private dataService: DataService,
     private localizedDatePipe: LocalizedDatePipe) {}

  ngOnInit() {
    this.dataService.getAttivitaValutazione(this.esperienza.esperienzaSvoltaId).subscribe((valutazione) => {
      this.valutazionEsperienzaTiro = valutazione;
      this.dataService.getValutazioneCompetenze(this.esperienza.esperienzaSvoltaId).subscribe((res) => {
        this.valutazionCompetenzeTiro = res;
      })
    });
  }

  setStatoValutazione(val) {
    let stato = 'Compilata';
    if (val.stato == 'incompleta') {
      stato = 'In compilata';
    } else if (val.stato == 'non_compilata') {
      stato = 'Non compilata';
    }
    return stato;
  }

  setStatoValutazioneCompetenze(val) {
    let stato = 'Compilata';
    if (val.stato == 'incompleta') {
      stato = 'In compilata';
    } else if (val.stato == 'non_compilata') {
      stato = 'Non compilata';
    }
    return stato;
  }

  styleStatoVal(esp) {
    var style = {
      'color': '#707070', //grey
    };
    if (esp.stato == 'incompleta') {
      style['color'] = '#F83E5A'; // red
    } else if (esp.stato == 'non_compilata') {
      style['color'] = '#F83E5A'; // red
    }
    return style;
  }

  styleStatoValCompetenza(esp) {
    var style = {
      'color': '#707070', //grey
    };
    if (esp.stato == 'incompleta') {
      style['color'] = '#F83E5A'; // red
    } else if (esp.stato == 'non_compilata') {
      style['color'] = '#F83E5A'; // red
    }
    return style;
  }

  setDate(val) {
    let date = '-';
    if (val) {
      date = this.localizedDatePipe.transform(val, 'dd/MM/yyyy')
    }
    return date;
  }

  confirm() {
    this.activeModal.close();
    this.onArchivia.emit('CONFIRM');
  }
 
}

