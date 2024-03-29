import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service';
import { Component, ViewChild, OnInit } from '@angular/core';
import { addDays } from 'date-fns';
import { IModalContent } from '../../../core/modal/modal.service'
import { registerLocaleData } from '@angular/common';
import { GrowlerService, GrowlerMessageType } from '../../../core/growler/growler.service';
import localeIT from '@angular/common/locales/it'
import { DatePickerComponent } from 'ng2-date-picker';
import { ArchiaviazioneAttivitaModal } from '../archiaviazione-attivita-modal/archiaviazione-attivita.component';
import { environment } from '../../../../environments/environment';
import { AvvisoArchiviaModal } from '../avviso-archivia-modal/avviso-archivia-modal.component';

registerLocaleData(localeIT);

declare var moment: any;
moment['locale']('it');

@Component({
  selector: 'gestione-preseneze-individuale',
  templateUrl: './gestione-preseneze-individuale.component.html',
  styleUrls: ['./gestione-preseneze-individuale.component.scss']
})

export class GestionePresenzeIndividualeComponent implements OnInit {

  breadcrumbItems = [
    {
      title: "Lista attività",
      location: "../../../../../../"
    },
    {
      title: "Dettaglio attività",
      location: "../../../../"
    },
    {
      title: "Gestione diario"
    }
  ];

  report: any;
  presenze = [];
  attivita: any;
  esperienza: any;
  date;
  limitMin: any;
  limitMax: any;
  nomeStudente: any;
  toolTipSave;
  isArchivio: boolean;
  evn = environment;
  tipologie;
  tipoInterna: boolean = false;
  
  datePickerConfig = {
    locale: 'it',
    firstDayOfWeek: 'mo'
  };

  @ViewChild('calendarStart') calendarStart: DatePickerComponent;
  @ViewChild('calendarEnd') calendarEnd: DatePickerComponent;

  openStart() {
    this.calendarStart.api.open();
  }

  closeStart() {
    this.calendarStart.api.close();
  }

  openEnd() {
    this.calendarEnd.api.open();
  }

  closeEnd() {
    this.calendarEnd.api.close();
  }

  refresh() {

    if (this.date.dataInizio >= this.limitMin && this.date.dataInizio <= this.limitMax
      && this.date.dataFine <= this.limitMax && this.date.dataFine >= this.date.dataInizio) {
      this.dataService.getAttivitaPresenzeIndividualeListaGiorni(this.attivita.id, moment(this.date.dataInizio).format('YYYY-MM-DD'), moment(this.date.dataFine).format('YYYY-MM-DD')).subscribe((giornate => {
        this.presenze = giornate;
        this.initDays();
      }));
    }
  }

  constructor(
    private activeRoute: ActivatedRoute,
    private router: Router,
    public dataService: DataService,
    private growler: GrowlerService,
    private modalService: NgbModal) {
    this.date = {
      dataInizio: moment(),
      dataFine: moment()
    }
  }

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
        this.esperienza = res.esperienze[0];
        this.nomeStudente = this.esperienza.nominativoStudente;
        if (this.breadcrumbItems[2].title.indexOf(this.nomeStudente) < 0)
          this.breadcrumbItems[2].title = this.breadcrumbItems[2].title + " di " + this.nomeStudente;

        this.dataService.getAttivitaTipologie().subscribe((res) => {
          this.tipologie = res;
          this.tipologie.filter(tipo => {
            if (tipo.id == this.attivita.tipologia) {
              this.tipoInterna = tipo.interna;
            }
          })
        });
        
        this.dataService.getAttivitaPresenzeIndividualeReport(id).subscribe((res => {
          this.report = res;

          if (this.report.giornateDaValidare == 0) {
            if ((this.report.oreTotali - this.report.oreValidate) > 0) {
              this.toolTipSave = 'Valida ore studenti';
            } else {
              this.toolTipSave = 'Non ci sono ore da validare';
            }
          } else {
            this.toolTipSave = 'Valida ore studenti';
          }

          this.attivita.stato == 'archiviata' ? (this.isArchivio = true) : (this.isArchivio = false);

          this.limitMin = moment(this.report.dataInizio);
          this.limitMax = moment(this.report.dataFine);

          this.date.dataInizio = moment(this.report.dataInizio);
          this.date.dataFine = this.limitMax;
          // var dataFine = addDays(this.date.dataInizio, 7);
          // if (dataFine < this.limitMax) {
          //   this.date.dataFine = moment(dataFine);
          // } else {
          //   this.date.dataFine = this.limitMax;
          // }

          this.dataService.getAttivitaPresenzeIndividualeListaGiorni(id, moment(this.date.dataInizio).format('YYYY-MM-DD'), moment(this.date.dataFine).format('YYYY-MM-DD')).subscribe((giornate => {
            this.presenze = giornate;
            this.initDays();
          }));
        }));
      });
    });
  }
  
  ngOnDestroy(){
    this.evn.modificationFlag=false;
  }

  initDays() {

    var startDate = moment(this.date.dataInizio);
    var endDate = moment(this.date.dataFine);
    var tot = endDate.diff(startDate, 'days');   // =1

    var now = startDate.clone();
    if (this.presenze.length < (tot + 1)) {
      while (now.diff(endDate, 'days') <= 0) {
        var index = this.presenze.findIndex(x => x.giornata === now.format('YYYY-MM-DD'));
        if (index < 0) {
          this.presenze.push({
            "attivitaSvolta": "",
            "esperienzaSvoltaId": this.esperienza.esperienzaSvoltaId,
            "giornata": moment(now).format('YYYY-MM-DD'),
            "istitutoId": this.esperienza.istitutoId,
            "oreSvolte": null,
            "verificata": false
          });
        }
        now.add(1, 'days');
      }
    }

    // sort by giornata.
    this.presenze = this.presenze.sort((a, b) => {
      return moment(a.giornata).diff(moment(b.giornata));
    });

  }

  daydiff(first, second) {
    return Math.round((second - first) / (1000 * 60 * 60 * 24));
  }

  edit(event, newHour) {
    if(event.oreSvolte > 12) {
      event.oreSvolte = new Number(12);
    }
    event.verificata = false;
    event.validataEnte = false;
    event.isModifiedState = true;
    this.toolTipSave = 'Valida ore studenti';

  }

  // styleOption(giornata) {
  //   var style = {
  //     'color': '#F83E5A',
  //     'font-size.px': 18
  //   };
  //   if (giornata.verificata) 
  //     style['color'] = '#435a70';
  //   if (giornata.oreSvolte == 0)
  //     style['font-size.px'] = 16;
  //   return style;
  // }

  
  styleOption(giornata) {
    var style = {
      'color': '#707070',
      'font-size.px': 18
    };
    if (!giornata.verificata && !giornata.validataEnte) {
      style['color'] = '#F83E5A';
    } else if (giornata.validataEnte && !giornata.verificata) {
      style['color'] = '#7A73FF';
    }      
    if (giornata.oreSvolte == 0)
      style['font-size.px'] = 16;
    return style;
  }

  styleOptionTextArea(giornata) {
    var style = {
      'color': '#707070',
    };
    if (!giornata.verificata && !giornata.validataEnte) {
      style['color'] = '#F83E5A';
    } else if (giornata.validataEnte && !giornata.verificata) {
      style['color'] = '#7A73FF';
    }      
    return style;
  }

  setToolTipGiorno(giornata) {
    if (!giornata.verificata && !giornata.validataEnte) {
      return 'Questa riga deve essere validata o, se errata, corretta e validata';
    } else if (giornata.validataEnte && !giornata.verificata) {
      return 'Presenze validate da ente';
    } else {
      return 'Presenze validate da istituto';
    }
  }
    
  savePresenze() {
    let toBeSaved = this.prepareSaveArray();

    this.dataService.validaPresenzeAttivitaIndividuale(this.attivita.id, toBeSaved).subscribe((res) => {
      var start = moment(this.date.dataInizio);
      var end = moment(this.date.dataFine);
      let message = "Presenze comprese tra il " + start.format('DD/MM/YYYY') + " ed il " + end.format('DD/MM/YYYY') + " validate con successo";
      this.growler.growl(message, GrowlerMessageType.Success);
      this.ngOnInit();
    },
      (err: any) => {
        console.log(err)
      },
      () => console.log('valida presenze individuale'));
  }

  prepareSaveArray() {
    var toBeSaved = [];

    this.presenze.forEach(ps => {
      if (ps.isModifiedState || ps.validataEnte || ps.oreSvolte!=null ) {
        var save = JSON.parse(JSON.stringify(ps))
        save.verificata = true;
        save.giornata = moment(ps.giornata, 'YYYY-MM-DD').valueOf();
        toBeSaved.push(save);
      }
    });
    
    return toBeSaved;
  }

  showTipOra(ev, event) {
    if (event.verificata) {
      event.toolTipOra = 'validato';
    } else {
      event.toolTipOra = 'da validare';
    }
  }

  cancel() {
    this.router.navigate(['../../../../'], { relativeTo: this.activeRoute });
  }

  archivia() {
    this.dataService.attivitaAlternanzaEsperienzeReport(this.attivita.id)
      .subscribe((response) => {
        const modalRef = this.modalService.open(ArchiaviazioneAttivitaModal, { windowClass: "archiviazioneModalClass" });
        modalRef.componentInstance.esperienze = response;
        modalRef.componentInstance.titolo = this.attivita.titolo;
        modalRef.componentInstance.onArchivia.subscribe((res) => {
          let esperienze = res.esperienze;
          if (res.nrStudenteNonCompletato > 0) {
            const modalRef = this.modalService.open(AvvisoArchiviaModal, { windowClass: "cancellaModalClass" });
            modalRef.componentInstance.nrStudentiNonCompletato = res.nrStudenteNonCompletato;
            modalRef.componentInstance.nrTotale = res.esperienze.length;
            modalRef.componentInstance.onArchivia.subscribe((res) => {
              if (res == 'ARCHIVIA') {
                this.archiviaEsperienze(esperienze);
              }
            })
          } else {
            this.archiviaEsperienze(esperienze);
          }

        })
      }, (err: any) => console.log(err),
        () => console.log('attivitaAlternanzaEsperienzeReport'));
  }

  archiviaEsperienze(esperienze) {
    this.dataService.archiviaAttivita(this.attivita.id, esperienze).subscribe((res) => {
      let message = "Attività " + this.attivita.titolo + " correttamente archiviata";
      this.growler.growl(message, GrowlerMessageType.Success);
      this.router.navigate(['../../../../'], { relativeTo: this.activeRoute });
    })
  }

}