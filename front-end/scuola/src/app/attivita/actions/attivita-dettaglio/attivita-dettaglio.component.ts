import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';
import { AttivitaAlternanza } from '../../../shared/classes/AttivitaAlternanza.class';
import { DocumentoCancellaModal } from '../documento-cancella-modal/documento-cancella-modal.component';
import { AttivitaCancellaModal } from '../cancella-attivita-modal/attivita-cancella-modal.component';
import { ArchiaviazioneAttivitaModal } from '../archiaviazione-attivita-modal/archiaviazione-attivita.component';
import { GrowlerService, GrowlerMessageType } from '../../../core/growler/growler.service';
import { registerLocaleData } from '@angular/common';
import localeIT from '@angular/common/locales/it'
import { DocumentUploadModalComponent } from '../documento-upload-modal/document-upload-modal.component';
import { LocalizedDatePipe } from '../../../shared/pipes/localizedDatePipe';
import { AvvisoEnteConvenzioneModal } from '../avviso-ente-convenzione-modal/avviso-ente-convenzione-modal.component';
declare var moment: any;
moment['locale']('it');
registerLocaleData(localeIT);

@Component({
  selector: 'cm-attivita-dettaglio',
  templateUrl: './attivita-dettaglio.component.html',
  styleUrls: ['./attivita-dettaglio.component.scss']
})
export class AttivitaDettaglioComponent implements OnInit {

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    public dataService: DataService,
    private growler: GrowlerService,
    private modalService: NgbModal,
    private localizedDatePipe: LocalizedDatePipe) {}

  attivita: AttivitaAlternanza;
  esperienze;
  navTitle: string = "Dettaglio attivita alternanza";
  individuale: boolean;
  corsiStudio;
  tipologie;
  documenti;
  atttivitaCompetenze = [];
  tipoInterna: boolean = false;
  menuContent = "In questa pagina trovi tutte le informazioni relative all’attività che stai visualizzando. Utilizza i tasti blu per modificare ciascuna sezione.";
  showContent: boolean = false;
  stati = [{ "name": "In attesa", "value": "in_attesa" }, { "name": "In corso", "value": "in_corso" }, { "name": "Revisionare", "value": "revisione" }, { "name": "Archiviata", "value": "archiviata" }];
  tipiDoc = [{ "name": "Piano formativo", "value": "piano_formativo" }, { "name": "Convenzione", "value": "convenzione" }, { "name": "Valutazione studente", "value": "valutazione_studente" }, { "name": "Valutazione esperienza", "value": "valutazione_esperienza" }, { "name": "Altro", "value": "doc_generico" }, { "name": "Pregresso", "Altro": "pregresso" }];
  zeroStudent: boolean;
  ente;
  convenzioni = [];
  valEsperienzaTiro;
  valCompetenzeTiro;
  competenzeTotale = 0;
  competenzeValutate = 0;
  competenzeAcquisite = 0;
  valutazioni = [
    { titolo: 'Avanzato', punteggio: 4 },
    { titolo: 'Intermedio', punteggio: 3 },
    { titolo: 'Base', punteggio: 2 },
    { titolo: 'Non acquisita', punteggio: 1 },
    { titolo: '-', punteggio: 0 }
  ];

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
        this.esperienze = res.esperienze;
        this.navTitle = res.titolo;

        this.esperienze.length == 0 ? this.zeroStudent = true : this.zeroStudent = false;
        
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

        if (this.attivita.rendicontazioneCorpo) {
            this.esperienze.forEach(esp => {
              if (esp.oreRendicontate < 1) {
                esp.oreRendicontate = '-';
              }
            });
        }

        if (this.attivita.tipologia == 7 || this.attivita.tipologia == 10) {
          this.dataService.getAzienda(this.attivita.enteId).subscribe((res) => {
            this.ente = res;
            this.dataService.getAttivaConvenzione(this.attivita.enteId).subscribe((convAttiva) => {
              if (convAttiva) {
                if (moment(convAttiva.dataFine) < moment(this.attivita.dataFine)) {
                  const modalRef = this.modalService.open(AvvisoEnteConvenzioneModal, { windowClass: "abilitaEnteModalClass" });
                  modalRef.componentInstance.attivita = this.attivita;
                  modalRef.componentInstance.convAttiva = convAttiva;
                }
              }
              this.dataService.getEnteConvenzione(this.attivita.enteId).subscribe((res) => {
                this.convenzioni = res;
                if (this.isValutazioneActive() && this.isTiro() && this.esperienze[0]) {
                  this.dataService.getAttivitaValutazione(this.esperienze[0].esperienzaSvoltaId).subscribe((valutazione) => {
                    this.valEsperienzaTiro = valutazione;
                    this.initCounter();
                    this.dataService.getValutazioneCompetenze(this.esperienze[0].esperienzaSvoltaId).subscribe((res) => {
                      this.valCompetenzeTiro = res;
                      res.valutazioni.forEach(d => {
                        this.competenzeTotale++;
                        if (d.punteggio > 0) {
                          this.competenzeValutate++;
                        }
                        if (d.punteggio > 1) {
                          this.competenzeAcquisite++;
                        }
                      })
                    })
                  });
                }
              },
                (err: any) => console.log(err),
                () => console.log('getEnteConvenzioni'));
            },
              (err: any) => console.log(err),
              () => console.log('getAttivaConvenzione'));
          },
            (err: any) => console.log(err),
            () => console.log('getEnte'));
        }
      },
        (err: any) => console.log(err),
        () => console.log('getAttivita'));
    });
  }

  openDetail(attivita) {
    this.router.navigate([attivita.id], { relativeTo: this.route });
  }

  modifica() {
    this.router.navigate(['modifica/attivita/'], { relativeTo: this.route });
  }

  navigateEnteDettaglio() {
    this.router.navigateByUrl('/enti/detail/' + this.ente.id); 
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

  gestionePresenze() {
    if (this.individuale) {
      this.router.navigate(['modifica/studenti/presenze/individuale'], { relativeTo: this.route });
    } else {
      this.router.navigate(['modifica/studenti/presenze/gruppo'], { relativeTo: this.route });
    }
  }

  updateStudentiAssociate() {
    this.router.navigate(['modifica/studenti/'], { relativeTo: this.route });
  }

  modificaOreStudenti() {
    this.router.navigate(['modifica/studenti/ore'], { relativeTo: this.route });
  }

  updateCompetenzePiano() {
    this.router.navigate(['modifica/competenze/'], { relativeTo: this.route });
  }

  delete() {
    const modalRef = this.modalService.open(AttivitaCancellaModal, { windowClass: "cancellaModalClass" });
    modalRef.componentInstance.attivita = this.attivita;
    modalRef.componentInstance.onDelete.subscribe((res) => {
      this.dataService.deleteAttivita(this.attivita.id).subscribe((res) => {

        this.router.navigate(['../../'], { relativeTo: this.route });
      })
    });
  }

  archivia() {
    this.dataService.attivitaAlternanzaEsperienzeReport(this.attivita.id)
      .subscribe((response) => {
        const modalRef = this.modalService.open(ArchiaviazioneAttivitaModal, { windowClass: "archiviazioneModalClass" });
        modalRef.componentInstance.esperienze = response;
        modalRef.componentInstance.titolo = this.attivita.titolo;
        modalRef.componentInstance.onArchivia.subscribe((esperienze) => {
          this.dataService.archiviaAttivita(this.attivita.id, esperienze).subscribe((res) => {
            let message = "Attività " + this.attivita.titolo + " correttamente archiviata";
            this.growler.growl(message, GrowlerMessageType.Success);
            this.ngOnInit();
          })
        })
      }, (err: any) => console.log(err),
        () => console.log('archiviaAttivita'));
  }

  showTip(ev, piano) {
    console.log(ev.target.title);
    if (!piano.toolTip) {
      piano.fetchingToolTip = true;
      let in_scadenza_msg = 'Premi qui per attivare il piano. Se attivato, questo piano sara sostituito da *nomepiano* all’inizio del nuovo anno scolastico il gg/mm/aaaa';
      let in_attesa_msg = 'Premi qui per attivare il piano. Se attivato, questo piano sostituirà *nomepiano* all’inizio del nuovo anno scolastico il gg/mm/aaaa';
      this.dataService.getDulicaPiano(piano.id).subscribe((duplicaPiano) => {
        piano.fetchingToolTip = false;
        var date = new Date(duplicaPiano.dataAttivazione)
        if (piano.stato == 'in_scadenza') {
          in_scadenza_msg = in_scadenza_msg.replace("*nomepiano*", duplicaPiano.titolo);
          in_scadenza_msg = in_scadenza_msg.replace("gg/mm/aaaa", date.getDate() + '/' + (date.getMonth() + 1) + '/' + date.getFullYear());
          piano.toolTip = in_scadenza_msg;
        } else if (piano.stato == 'in_attesa') {
          in_attesa_msg = in_attesa_msg.replace("*nomepiano*", duplicaPiano.titolo);
          in_attesa_msg = in_attesa_msg.replace("gg/mm/aaaa", date.getDate() + '/' + (date.getMonth() + 1) + '/' + date.getFullYear());
          piano.toolTip = in_attesa_msg;
        }
      });
    }
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

  setNomeEnte(off) {
    let label = '';
    if (off.nomeEnte) {
      label = off.nomeEnte;
    } else {
      label = 'Istituto';
    }
    return label;
  }

  openDocumentUpload() {
    const modalRef = this.modalService.open(DocumentUploadModalComponent, { windowClass: "documentUploadModalClass" });
    modalRef.componentInstance.attivitaIndividuale = this.individuale;
    modalRef.componentInstance.tipologiaId = this.attivita.tipologia;
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

  duplicaAA() {
    this.dataService.duplicaAA(this.attivita).subscribe((res) => {
      this.router.navigate(['../../detail/' + res.id + '/modifica/attivita'], { relativeTo: this.route });
    });
  }

  isRendicontazioneOre(aa) {
    if (aa.rendicontazioneCorpo) {
      return 'Monte ore forfettario';
    } else {
      return 'Rendicontazione ore giornaliera';
    }
  }
  
  setStatoConvenzione(convenzione) {
    let stato = 'Assente';
    if (convenzione.stato == 'attiva') {
        stato = 'Attiva';
    } else if (convenzione.stato == 'non_attiva') {
        stato = 'Non attiva';
    }
    return stato;
  }

  styleOptionConvenzione(convenzione) {
    var style = {
        'color': '#707070', //grey
    };
    if (convenzione.stato == 'non_attiva') {
        style['color'] = '#F83E5A'; // red
    } else if (convenzione.stato == 'attiva') {
        style['color'] = '#00CF86'; // green
    }
    return style;
  }

  downloadConvenzioneDoc(doc) {
    this.dataService.downloadDocumentConvenzioneBlob(doc).subscribe((url) => {
      const downloadLink = document.createElement("a");
      downloadLink.href = url;
      downloadLink.download = doc.nomeFile;
      document.body.appendChild(downloadLink);
      downloadLink.click();
      document.body.removeChild(downloadLink);
    });
  }

  routeEntity() {
    this.router.navigateByUrl('/enti/detail/' + this.ente.id); 
  }

  generaProgettoFormativo() {
    this.dataService.downloadProgettoFormazione(this.esperienze[0].esperienzaSvoltaId).subscribe((url) => {
      const downloadLink = document.createElement("a");
      downloadLink.href = url;
      document.body.appendChild(downloadLink);
      downloadLink.click();
      document.body.removeChild(downloadLink);
    });
  }

  isTiro() {
    if (this.attivita.tipologia == 7) {
      return true;
    }
    return false;
  }

  styleLabel(competenza) {
    var style = {};
    const punteggio = this.getValutazioneByUri(competenza.uri);
    if (punteggio > 1) {
      style['font-weight'] = 600;      
    }
    return style;
  }

  setLabel(competenza) {
    let titolo = '-';
    const punteggio = this.getValutazioneByUri(competenza.uri);
    let rtn = this.valutazioni.find(data => data.punteggio == punteggio);
    if (rtn) titolo = rtn.titolo;
    return titolo;
  }

  getValutazioneByUri(uri) {
    if (this.valCompetenzeTiro) {
      for (let index = 0; index < this.valCompetenzeTiro.valutazioni.length; index++) {
        const v = this.valCompetenzeTiro.valutazioni[index];
        if(v.competenzaUri == uri) {
          return v.punteggio;
        }
      }
    }
    return 0;
  }

  isValutazioneActive() {
    var dataMinima = moment(this.attivita.dataFine).subtract(1, 'days');
    var now = moment();
    if (dataMinima.isBefore(now)) {
      return true;
    }
    return false;
  }

  routeValutazioneEsperienza() {
    this.router.navigate(['valutazione/esperienza/'], { relativeTo: this.route });    
  }

  setDate(val) {
    let date = '-';
    if (val) {
      date = this.localizedDatePipe.transform(val, 'dd/MM/yyyy')
    }
    return date;
  }

  setMedia(val) {
    if (val == 'NaN' || !val || val == 'undefined' || val == 0) {
      return '-';
    } else {
      return 'Media risposte ' + val;
    }
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

  setStatoValutazione(val) {
    let stato = 'Compilata';
    if (val.stato == 'incompleta') {
      stato = 'In compilata';
    } else if (val.stato == 'non_compilata') {
      stato = 'Non compilata';
    }
    return stato;
  }

  routeValutazioneCompetenze() {
    this.router.navigate(['valutazione/competenze/'], { relativeTo: this.route });    
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
  
  setStatoValutazioneCompetenze(val) {
    let stato = 'Compilata';
    if (val.stato == 'incompleta') {
      stato = 'In compilata';
    } else if (val.stato == 'non_compilata') {
      stato = 'Non compilata';
    }
    return stato;
  }

  setEsitoCompetenze(val) {
    let esito = '-';
    if (val.stato != 'non_compilata') {
      esito = this.competenzeAcquisite + ' su ' +  this.competenzeTotale;
    }
    return esito;
  }

  initCounter() {
    this.competenzeTotale = 0;
    this.competenzeValutate = 0;
    this.competenzeAcquisite = 0;
  }
  
}