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

      },
        (err: any) => console.log(err),
        () => console.log('getAttivita'));
    });
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
      return 'Rendicontazione a corpo';
    } else {
      return 'Rendicontazione ore giornaliera';
    }
  }

}
