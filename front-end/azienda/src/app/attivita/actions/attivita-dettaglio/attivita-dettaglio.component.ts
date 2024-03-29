import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';
import { AttivitaAlternanza } from '../../../shared/classes/AttivitaAlternanza.class';
import { DocumentoCancellaModal } from '../documento-cancella-modal/documento-cancella-modal.component';
import { GrowlerService } from '../../../core/growler/growler.service';
import { registerLocaleData } from '@angular/common';
import localeIT from '@angular/common/locales/it'
import { DocumentUploadModalComponent } from '../documento-upload-modal/document-upload-modal.component';
import { areAllEquivalent } from '@angular/compiler/src/output/output_ast';
registerLocaleData(localeIT);

declare var moment: any;
moment['locale']('it');

@Component({
  selector: 'cm-attivita-dettaglio',
  templateUrl: './attivita-dettaglio.component.html',
  styleUrls: ['./attivita-dettaglio.component.scss']
})
export class AttivitaDettaglioComponent implements OnInit {

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService,
    private modalService: NgbModal) { }

  attivita: AttivitaAlternanza;
  esperienze;
  offertaAssociata;
  valutazioneCompetenzeReport;
  valutazioneCompetenzeActive: boolean = false;
  dataValutazione: string;
  esitoValutazione: string;
  statoValutazione: string;
  statoValutazioneCss: string;
  nomeEnte: string;
  navTitle: string = "Dettaglio attivita alternanza";
  cardTitle: string = "Vedi";
  individuale: boolean;
  agreegatedTipo = [];
  agreegatedTotal = {};
  corsiStudio;
  tipologie;
  yearsHours;
  yearsHoursTotal;
  tabDefaultSelectedId;
  noActivitySetted: boolean = true;
  order: string = 'titolo';
  documenti = [];
  painoTipologieTerza: any = [];
  painoTipologieQuarto: any = [];
  painoTipologieQuinto: any = [];
  totale = {};
  pianoTipologie = {};
  atttivitaCompetenze = [];
  tipoInterna: boolean = false;
  menuContent = "In questa pagina trovi tutti i dettagli di una particolare attività. Puoi gestire le presenze degli studenti con il tasto blu “Gestisci presenze”. Puoi allegare dei file all’attività con il tasto “carica file”. Puoi modificare alcuni dati dell’attività con il tasto “Modifica dati attività”. Se vuoi fare ulteriori modifiche, contatta l’istituto di riferimento.";
  showContent: boolean = false;
  stati = [{ "name": "In attesa", "value": "in_attesa" }, { "name": "In corso", "value": "in_corso" }, { "name": "Revisionare", "value": "revisione" }, { "name": "Archiviata", "value": "archiviata" }];
  tipiDoc = [{ "name": "Piano formativo", "value": "piano_formativo" }, { "name": "Convenzione", "value": "convenzione" }, { "name": "Valutazione studente", "value": "valutazione_studente" }, { "name": "Valutazione esperienza", "value": "valutazione_esperienza" }, { "name": "Altro", "value": "doc_generico" }, { "name": "Pregresso", "Altro": "pregresso" }];
  removableDoc = ["valutazione_studente", "doc_generico"];
  zeroStudent: boolean;
  breadcrumbItems = [
    {
      title: "Lista attività",
      location: "../../",
    },
    {
      title: "Dettaglio attività"
    }
  ];


  ngOnInit() {
    this.route.params.subscribe(params => {
      let id = params['id'];
      this.dataService.getAttivita(id).subscribe((res) => {
        this.attivita = res.attivitaAlternanza;
        this.attivita.nomeIstituto = res.nomeIstituto;
        this.esperienze = res.esperienze;
        this.navTitle = res.titolo;
        this.valutazioneCompetenzeActive = this.isValutazioneCompetenzeActive();

        if (this.attivita.offertaId) {
          this.dataService.getOfferta(this.attivita.offertaId).subscribe((off) => {
            this.offertaAssociata = off;
          })
        }

        this.dataService.downloadAttivitaDocumenti(this.attivita.uuid).subscribe((docs) => {
          this.documenti = docs;
        });

        this.dataService.getAttivitaTipologie().subscribe((res) => {
          this.tipologie = res;
          this.tipologie.filter(tipo => {
            if (tipo.id == this.attivita.tipologia) {
              this.individuale = tipo.individuale;
              this.tipoInterna = tipo.interna;
            }
          })
        });

        this.dataService.getRisorsaCompetenze(this.attivita.uuid).subscribe((res) => {
          this.atttivitaCompetenze = res;
        });

        if(this.valutazioneCompetenzeActive) {
          if(this.esperienze.length > 0) {
            var esp = this.esperienze[0];
            this.dataService.getValutazioneCompetenzeReport(esp.esperienzaSvoltaId).subscribe((res) => {
              this.loadReport(res);
            });  
          }
        }

      },
        (err: any) => console.log(err),
        () => console.log('getAttivita'));
    });
  }

  loadReport(report) {
    this.valutazioneCompetenzeReport = report;
    this.dataValutazione = this.getDataValutazione();
    this.esitoValutazione = this.getEsitoValutazione();
    this.statoValutazione = this.getStatoValutazione();
    this.statoValutazioneCss = report.stato;
    this.nomeEnte = this.dataService.aziendaName;
  }

  openDetail(attivita) {
    this.router.navigate([attivita.id], { relativeTo: this.route });
  }

  getAttivitaType() { }

  modifica() {
    this.router.navigate(['modifica/attivita/'], { relativeTo: this.route });
  }

  getCorsoDiStudioString(corsoStudioId) {
    if (this.corsiStudio) {
      let rtn = this.corsiStudio.find(data => data.courseId == corsoStudioId);
      if (rtn) return rtn.titolo;
      return corsoStudioId;
    } else {
      return corsoStudioId;
    }
  }

  getTipologiaString(tipologiaId) {
    if (this.tipologie) {
      let rtn = this.tipologie.find(data => data.id == tipologiaId);
      if (rtn) return rtn.titolo;
      return tipologiaId;
    } else {
      return tipologiaId;
    }
  }

  getTipologia(tipologiaId) {
    if (this.tipologie) {
      return this.tipologie.find(data => data.id == tipologiaId);
    } else {
      return tipologiaId;
    }
  }

  deleteDoc(doc) {
    const modalRef = this.modalService.open(DocumentoCancellaModal);
    modalRef.componentInstance.documento = doc;
    modalRef.result.then((result) => {
      if (result == 'deleted') {
        this.dataService.downloadAttivitaDocumenti(this.attivita.uuid).subscribe((docs) => {
          this.documenti = docs;
        });
      }
    });
  }

  downloadDoc(doc) {
    this.dataService.downloadDocumentBlob(doc).subscribe((url) => {
      const downloadLink = document.createElement("a");
      downloadLink.href = url;
      downloadLink.download = doc.nomeFile;
      document.body.appendChild(downloadLink);
      downloadLink.click();
      document.body.removeChild(downloadLink);
    });
  }

  gestionePresenze() {
    if (this.individuale) {
      this.router.navigate(['modifica/studenti/presenze/individuale'], { relativeTo: this.route });
    }
  }

  updateStudentiAssociate() {
    this.router.navigate(['modifica/studenti/'], { relativeTo: this.route });
  }

  updateCompetenzePiano() {
    this.router.navigate(['modifica/competenze/'], { relativeTo: this.route });
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

  getStatoNome(statoValue) {
    if (this.stati) {
      let rtn = this.stati.find(data => data.value == statoValue);
      if (rtn) return rtn.name;
      return statoValue;
    }
  }

  setIstiutoLabel(off) {
    let label = '';
    if (off.istitutiAssociati.length > 0) {
      if (off.istitutiAssociati.length > 1) {
        label = off.istitutiAssociati.length + ' istituti';
      } else {
        label = off.istitutiAssociati.length + ' istituto';
      }
    } else {
      label = '0 istituto';
    }
    return label;
  }

  openDocumentUpload() {
    const modalRef = this.modalService.open(DocumentUploadModalComponent, { windowClass: "documentUploadModalClass" });
    modalRef.componentInstance.attivitaIndividuale = this.individuale;
    modalRef.componentInstance.tirocinioCurriculare = (this.attivita.tipologia == 7);
    modalRef.componentInstance.newDocumentListener.subscribe((option) => {
      console.log(option);
      this.dataService.uploadDocumentToRisorsa(option, this.attivita.uuid + '').subscribe((doc) => {
        this.dataService.downloadAttivitaDocumenti(this.attivita.uuid).subscribe((docs) => {
          this.documenti = docs;
        });
      });
    });
  }

  setDocType(type) {
    if (this.tipiDoc) {
      let rtn = this.tipiDoc.find(data => data.value == type);
      if (rtn) return rtn.name;
      return type;
    }
  }

  isRemovable(doc) {
    let removable = false;
    if (this.removableDoc.indexOf(doc.tipo) > -1 && this.attivita.stato != 'archiviata') {
      removable = true;
    }
    return removable;
  }

  isRendicontazioneOre(aa) {
    if (aa.rendicontazioneCorpo) {
      return 'Monte ore forfettario';
    } else {
      return 'Rendicontazione ore giornaliera';
    }
  }

  isValutazioneCompetenzeActive() {
    if(this.attivita.tipologia == 7) {
      var dataMinima = moment(this.attivita.dataFine).subtract(1, 'days');
      var now = moment();
      if(dataMinima.isBefore(now)) {
        return true;
      }
    }
    return false;
  }

  getStatoValutazione() {
    if(this.valutazioneCompetenzeReport) {
      if(this.valutazioneCompetenzeReport.stato == 'non_compilata') {
        return "Non compilata";
      }
      if(this.valutazioneCompetenzeReport.stato == 'incompleta') {
        return "Incompleta";
      }
      if(this.valutazioneCompetenzeReport.stato == 'compilata') {
        return "Compilata";
      }  
    }
  }

  getEsitoValutazione() {
    if(this.valutazioneCompetenzeReport && (this.valutazioneCompetenzeReport.stato != 'non_compilata')) {
      var acquisite = 0;
      this.valutazioneCompetenzeReport.valutazioni.forEach(v => {
        if(v.punteggio > 1) {
          acquisite++;
        }
      });
      return acquisite + "/" + this.valutazioneCompetenzeReport.valutazioni.length + " competenze acquisite";  
    }
    return "-";
  }


  getDataValutazione() {
    if(this.valutazioneCompetenzeReport && this.valutazioneCompetenzeReport.ultimaModifica) {
      return this.valutazioneCompetenzeReport.ultimaModifica;
    }
    return "-";
  }

  valutaCompetenze() {
    if (this.valutazioneCompetenzeReport) {
      this.router.navigate(['valuta/competenze/'], { relativeTo: this.route });
    }
  }

  getValutazione(competenza) {
    const punteggio = this.getValutazioneByUri(competenza.uri);
    switch(punteggio) {
      case 0: return "-";
      case 1: return "Non acquisita";
      case 2: return "Base";
      case 3: return "Intermedio";
      case 4: return "Avanzato";
      default: return "-";
    }
  }

  getValutazioneCss(competenza) {
    const punteggio = this.getValutazioneByUri(competenza.uri);
    switch(punteggio) {
      case 0: return "non_acquisita";
      case 1: return "non_acquisita";
      default: return "acquisita";
    }
  }

  getValutazioneByUri(uri) {
    if (this.valutazioneCompetenzeReport) {
      for (let index = 0; index < this.valutazioneCompetenzeReport.valutazioni.length; index++) {
        const v = this.valutazioneCompetenzeReport.valutazioni[index];
        if(v.competenzaUri == uri) {
          return v.punteggio;
        }
      }
    }
    return 0;
  }

}
