import { Component, OnInit, ViewChild, EventEmitter, Output, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'archiaviazione-attivita',
    templateUrl: './archiaviazione-attivita.component.html',
    styleUrls: ['./archiaviazione-attivita.component.scss']
})

export class ArchiaviazioneAttivitaModal implements OnInit {

    @Input() esperienze;
    @Input() titolo;
    @Output() onArchivia = new EventEmitter<Object>();
    nrStudentiNonCompletato = 0;

    constructor(public activeModal: NgbActiveModal) { }

    ngOnInit() {
        this.esperienze.forEach(esp => {
            if (!esp.valida)
                this.nrStudentiNonCompletato++;
            esp.percentage = ((esp.oreValidate / esp.oreAttivita) * 100).toFixed(0)
        });
    }

    confirm() {
        this.activeModal.close();
        var updatedNrstudentiNonCompleto = 0;
        this.esperienze.forEach(esp => {
            if (!esp.valida)
            updatedNrstudentiNonCompleto++;
        });
        this.onArchivia.emit({esperienze: this.esperienze, nrStudenteNonCompletato: updatedNrstudentiNonCompleto});
    }

    onFilterChange(esp) {
        esp.valida == !esp.valida;
    }

    showTipRiga(ev, esp) {
        if (esp.valida) {
            esp.toolTipRiga = "Attività valida. Premi per ANNULLARE questa attività";
        } else {
            esp.toolTipRiga = "Attività annullata. Premi per rendere VALIDA questa attività";
        }
    }

    customColor(esperienza) {
        var style = {
            'color': '#F83E5A',
            'font-weight': 'bold'
        };
        if (!esperienza.valida) {
            return style;
        }
    }

}