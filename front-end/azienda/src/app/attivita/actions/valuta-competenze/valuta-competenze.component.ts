import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service';
import { Component, ViewChild, OnInit } from '@angular/core';
import { IModalContent } from '../../../core/modal/modal.service'
import { registerLocaleData } from '@angular/common';
import { GrowlerService, GrowlerMessageType } from '../../../core/growler/growler.service';
import localeIT from '@angular/common/locales/it'
import { environment } from '../../../../environments/environment';

registerLocaleData(localeIT);

declare var moment: any;
moment['locale']('it');

@Component({
  selector: 'valuta-competenze',
  templateUrl: './valuta-competenze.component.html',
  styleUrls: ['./valuta-competenze.component.scss']
})

export class ValutaCompetenzeComponent implements OnInit {

  breadcrumbItems = [
    {
      title: "Lista attività",
      location: "../../../"
    },
    {
      title: "Dettaglio attività",
      location: "../../"
    },
    {
      title: "Valutazione studente"
    }
  ];

  report: any;
  attivita: any;
  esperienza: any;
  nominativo: string;
  isArchivio: boolean = false;

  menuContent = "In questa sezione trovi le competenze che l'istituto ha associato all'attività. Ti viene chiesto di valutare il livello dell'acquisizione di ciascuna competenza. Puoi usare quattro valori: Non acquisita, Base, Intermedio, Avanzato";
  showContent: boolean = false;
  
  evn = environment;

  constructor(
    private activeRoute: ActivatedRoute,
    private router: Router,
    private dataService: DataService,
    private growler: GrowlerService,
    private modalService: NgbModal) {}

  modalPopup: IModalContent = {
    header: 'Salva giorno',
    body: 'Sei sicuro di voler salvare il giorno?',
    cancelButtonText: 'Annulla',
    OKButtonText: 'Salva'
  };

  ngOnInit() {
    this.evn.modificationFlag=true;
    this.activeRoute.params.subscribe(params => {
      let id = params['id'];
      this.dataService.getAttivita(id).subscribe((res) => {
        this.attivita = res.attivitaAlternanza;
        if(this.attivita.stato=='archiviata') {
          this.isArchivio = true;
        }
        if(res.esperienze.length > 0) {
          this.esperienza = res.esperienze[0];
          this.nominativo = this.esperienza.nominativoStudente;
          this.dataService.getValutazioneCompetenzeReport(this.esperienza.esperienzaSvoltaId).subscribe((report) => {
            this.report = report;
          });  
        }
      });
    });
  }

  ngOnDestroy(){
    this.evn.modificationFlag=false;
  }

  cancel() {
    this.router.navigate(['../../'], { relativeTo: this.activeRoute });
  }

  saveValutazioni() {
    this.dataService.saveValutazioneCompetenze(this.esperienza.esperienzaSvoltaId, this.report.valutazioni).subscribe(report => {
      this.report = report;
      let message = "Salvataggio effettuato con successo!";
      this.growler.growl(message, GrowlerMessageType.Success);
    });
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

}