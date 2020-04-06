import { Component, OnInit, ViewChild } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';
import { PianoAlternanza } from '../../../shared/classes/PianoAlternanza.class';
import { PianificaCancellaModal } from '../cancella-piano-modal/pianifica-cancella-modal.component';
import { DocumentoCancellaModal } from '../documento-cancella-modal/documento-cancella-modal.component';
import { ActivatePianoModal } from '../activate-piano-modal/activate-piano-modal.component';
import { SorterService } from '../../../core/services/sorter.service';
import { serverAPIConfig } from '../../../core/serverAPIConfig'
import { EditPianoModalComponent } from '../edit-piano-modal/edit-piano-modal.component';
import { DuplicaPianoModal } from '../duplica-piano-modal/duplica-piano-modal.component';
import { DeactivatePianoModal } from '../deactivate-piano-modal/deactivate-piano-modal.component';

@Component({
  selector: 'cm-piano-dettaglio',
  templateUrl: './piano-dettaglio.component.html',
  styleUrls: ['./piano-dettaglio.component.scss']
})
export class PianoDettaglioComponent implements OnInit {
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService,
    private modalService: NgbModal,
    private sorterService: SorterService) { }

  piano: PianoAlternanza;
  navTitle: string = "Dettaglio piano alternanza";
  cardTitle: string = "Vedi";

  agreegatedTipo = [];
  agreegatedTotal = {};
  corsiStudio;
  tipologie;
  yearsHours;
  yearsHoursTotal;
  tabDefaultSelectedId;
  noActivitySetted: boolean = true;
  order: string = 'titolo';
  documenti;
  painoTipologieTerza: any = [];
  painoTipologieQuarto: any = [];
  painoTipologieQuinto: any = [];
  totale = {};
  pianoTipologie = {};
  pianoCompetenze = {};
  stati = [{ "name": "Attivo", "value": "attivo" }, { "name": "Bozza", "value": "bozza" }, { "name": "In scadenza", "value": "in_scadenza" }, {"name": "Scaduto", "value": "scaduto"}, {"name": "In attesa", "value": "in_attesa"}];
  menuContent = "";
  showContent: boolean = false;
  
  breadcrumbItems = [
    {
      title: "Lista piani",
      location: "../../",
    },
    {
      title: "Dettaglio piano"
    }
  ];


  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      if (params['year']) {
        this.tabDefaultSelectedId = 'tab-year-' + params['year'];
        console.log(this.tabDefaultSelectedId);
      }
    });
    this.route.params.subscribe(params => {
      let id = params['id'];

      this.dataService.getPianoById(id).subscribe((piano: PianoAlternanza) => {
        this.piano = piano;
        this.navTitle = piano.titolo;

        this.dataService.downloadPianoDocumenti(piano.uuid).subscribe((docs) => {
          this.documenti = docs;
          for (let doc of this.documenti) {
            this.dataService.downloadDocumentBlob(doc);
          }
        });

        this.dataService.getCorsiStudio().subscribe((response) => {
          this.corsiStudio = response;
        })

        this.dataService.getAttivitaTipologie().subscribe((res) => {
          this.tipologie = res;
          this.dataService.getPianoTipologie(id).subscribe((res) => {
            this.pianoTipologie = res;
            if (this.pianoTipologie[3])
              this.painoTipologieTerza = this.pianoTipologie[3];
            if (this.pianoTipologie[4])
              this.painoTipologieQuarto = this.pianoTipologie[4];
            if (this.pianoTipologie[5])
              this.painoTipologieQuinto = this.pianoTipologie[5];

            this.totale[3] = 0;
            this.totale[4] = 0;
            this.totale[5] = 0;

            if (this.painoTipologieTerza != null) {
              for (let pt of this.painoTipologieTerza) {
                this.totale[3] = this.totale[3] + pt.monteOre;
              }
            }
            if (this.painoTipologieQuarto != null) {
              for (let pt of this.painoTipologieQuarto) {
                this.totale[4] = this.totale[4] + pt.monteOre;
              }
            }
            if (this.painoTipologieQuinto != null) {
              for (let pt of this.painoTipologieQuinto) {
                this.totale[5] = this.totale[5] + pt.monteOre;
              }
            }
          });

        });

        this.dataService.getRisorsaCompetenze(piano.uuid).subscribe((res) => {
          this.pianoCompetenze = res;
        });

      },
        (err: any) => console.log(err),
        () => console.log('get piano by id'));
    });



  }
  openDetailCompetenza(competenza, $event) {
    // if ($event) $event.stopPropagation();
    // const modalRef = this.modalService.open(CompetenzaDetailModalComponent, { size: "lg" });
    // modalRef.componentInstance.competenza = competenza;
  }

  openDetail(attivita) {
    this.router.navigate([attivita.id], { relativeTo: this.route });
  }

  // changeStatusPiano(attivo) {
  //   const modalRef = this.modalService.open(ActivatePianoModal);
  //   modalRef.componentInstance.piano = this.piano;
  //   modalRef.componentInstance.onSuccess.subscribe((piano) => {
  //     this.dataService.attivaPiano(this.piano, attivo).subscribe((res) => {
  //       this.ngOnInit();
  //     });
  //   });
  // }

  editPiano() {
    const modalRef = this.modalService.open(EditPianoModalComponent,  { windowClass: "myCustomModalClass" } );
    modalRef.componentInstance.piano = this.piano;
    modalRef.componentInstance.corsiStudio = this.corsiStudio;
    modalRef.componentInstance.editPianoListener.subscribe((piano) => {
      this.dataService.updatePianoDetails(piano).subscribe((pianoUpdated) => {
        this.ngOnInit();
      });
    });
  }

      
  duplicaPiano() {
    const modalRef = this.modalService.open(DuplicaPianoModal, { windowClass: "myCustomModalClass" });
    let duplicaPiano = JSON.parse(JSON.stringify(this.piano));
    duplicaPiano.titolo = duplicaPiano.titolo + '- copia';
    modalRef.componentInstance.piano = duplicaPiano;
    modalRef.componentInstance.corsiStudio = this.corsiStudio;
    modalRef.componentInstance.onDupicateConfirm.subscribe((res) => {
        this.dataService.duplicaPiano(res).subscribe((piano) => {
          this.router.navigate(['../', piano.id], { relativeTo: this.route });
        });
    });
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

  onSelectChange(ev) {
    this.sorterService.sort(this.piano.competenze, this.order, -1);
  }

  uploadDocument(fileInput) {
    if (fileInput.target.files && fileInput.target.files[0]) {
      this.dataService.uploadDocumentToRisorsa(fileInput.target.files[0], this.piano.uuid + '').subscribe((doc) => {
        this.dataService.downloadPianoDocumenti(this.piano.uuid).subscribe((docs) => {
          this.documenti = docs;
          for (let doc of this.documenti) {
            this.dataService.downloadDocumentBlob(doc);
          }
        });
      });
    }
  }

  deleteDoc(doc) {
    const modalRef = this.modalService.open(DocumentoCancellaModal);
    modalRef.componentInstance.documento = doc;
    modalRef.result.then((result) => {
      if (result == 'deleted') {
        this.dataService.downloadPianoDocumenti(this.piano.uuid).subscribe((docs) => {
          this.documenti = docs;
          for (let doc of this.documenti) {
            this.dataService.downloadDocumentBlob(doc);
          }
        });
      }
    });
  }

  // openDocument(doc) {
  //   this.dataService.openDocument(doc);
  // }

  updateDatiPiano() {
    this.router.navigate(['modifica/dati/'], { relativeTo: this.route });
  }

  updateTipologiePiano() {
    this.router.navigate(['modifica/tipologia/'], { relativeTo: this.route });
  }

  updateCompetenzePiano() {
    this.router.navigate(['modifica/competenze/'], { relativeTo: this.route });
  }

  deletePiano() {
    const modalRef = this.modalService.open(PianificaCancellaModal, {windowClass: "cancellaModalClass"});
    modalRef.componentInstance.piano = this.piano;
    modalRef.componentInstance.onDelete.subscribe((res) => {
      this.router.navigate(['../../'], { relativeTo: this.route });
    });
  }

  attivaPiano() {
    const modalRef = this.modalService.open(ActivatePianoModal, {windowClass: "cancellaModalClass"});
    modalRef.componentInstance.piano = this.piano;
    modalRef.componentInstance.onSuccess.subscribe((res) => {
      if (res == 'ACTIVATE') {
        this.dataService.activatePiano(this.piano.id).subscribe((res) => { 
          this.router.navigate(['../../'], { relativeTo: this.route });
        })        
      }      
    });
  }

  deAttivaPiano() {

    this.dataService.getDulicaPiano(this.piano.id).subscribe((vecchioPiano) => { 
      const modalRef = this.modalService.open(DeactivatePianoModal, { windowClass: "cancellaModalClass" });
      modalRef.componentInstance.piano = this.piano;
      modalRef.componentInstance.vecchioPiano = vecchioPiano;
      modalRef.componentInstance.onSuccess.subscribe((res) => {
        if (res == 'DEACTIVATE') {
          this.dataService.deactivatePiano(this.piano.id).subscribe((res) => { 
            this.router.navigate(['../../'], { relativeTo: this.route });
          })        
        }      
      });  
    })
    
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
                in_scadenza_msg = in_scadenza_msg.replace("gg/mm/aaaa", date.getDate() + '/' + (date.getMonth()+1) + '/' + date.getFullYear());
                piano.toolTip = in_scadenza_msg;
            } else if (piano.stato == 'in_attesa') {
                in_attesa_msg = in_attesa_msg.replace("*nomepiano*", duplicaPiano.titolo);
                in_attesa_msg = in_attesa_msg.replace("gg/mm/aaaa",  date.getDate() + '/' + (date.getMonth()+1) + '/' + date.getFullYear());
                piano.toolTip = in_attesa_msg;
            }            
        });    
    }         
  }
  
  getStatoNome(statoValue) {
    if (this.stati) {
        let rtn = this.stati.find(data => data.value == statoValue);
        if (rtn) return rtn.name;
        return statoValue;
    }
  }
  menuContentShow() {
    this.menuContent = " ";
    if(this.piano.stato=='attivo'){
      this.menuContent += "Questo piano è attivo dal "+this.piano.dataAttivazione+". In questa pagina trovi tutte le informazioni riguardanti il piano. Usa i tasti blu per entrare in ciascuna sezione e modificarla. Ricordati di salvare le modifiche!";
    }else if(this.piano.stato=='bozza'){
      this.menuContent += "Questo piano NON è attivo. In questa pagina trovi tutte le informazioni riguardanti il piano. Usa i tasti blu per entrare in ciascuna sezione e modificarla. Ricordati di salvare le modifiche!";
    }else if(this.piano.stato=='scaduto'){
      this.menuContent += "Questo piano è scaduto: non può più essere modificato o riattivato. Puoi duplicarlo con il tasto “duplica”.";
    }else if(this.piano.stato=='in_scadenza'){
      this.menuContent += "Questo piano sarà sostituito da “"+this.piano.pianoCorrelato.titolo+"” all’inizio del prossimo anno scolastico. In questa pagina trovi tutte le informazioni riguardanti il piano. Usa i tasti blu per entrare in ciascuna sezione e modificarla. Ricordati di salvare le modifiche!";
    }else if(this.piano.stato=='in_attesa'){
      this.menuContent += "Questo piano sostituirà “"+this.piano.pianoCorrelato.titolo+"” all’inizio del prossimo anno scolastico.  In questa pagina trovi tutte le informazioni riguardanti il piano. Usa i tasti blu per entrare in ciascuna sezione e modificarla. Ricordati di salvare le modifiche!";
    }else{
      this.menuContent += "In questa pagina trovi tutte le informazioni riguardanti il piano. Usa i tasti blu per entrare in ciascuna sezione e modificarla. Ricordati di salvare le modifiche!";
    }
    
    this.showContent = !this.showContent;
  }

}
