import { Component, OnInit, ViewChild } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';
import { registerLocaleData } from '@angular/common';
import localeIT from '@angular/common/locales/it'
registerLocaleData(localeIT);

declare var moment: any;
moment['locale']('it');

@Component({
  selector: 'cm-offerte-dettaglio',
  templateUrl: './offerte-dettaglio.component.html',
  styleUrls: ['./offerte-dettaglio.component.scss']
})
export class OfferteDettaglioComponent implements OnInit {

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService,
    private modalService: NgbModal) { }

  offerta;
  tipologie;
  documenti;
  offertaCompetenze = [];
  tipoInterna: boolean = false;
  menuContent = "In questa pagina trovi tutti i dati dell’offerta. Usa i tasti blu per modificare ciascuna sezione. Usa il tasto verde “Crea attività a partire da offerta” per creare un’attività associata a questa offerta.";
  showContent: boolean = false;
  breadcrumbItems = [
    {
      title: "Lista offerte",
      location: "../../",
    },
    {
      title: "Dettaglio offerta"
    }
  ];

  ngOnInit() {
    this.route.params.subscribe(params => {
      let id = params['id'];
      this.dataService.getOfferta(id).subscribe((res) => {
        this.offerta = res;
        // this.dataService.downloadAttivitaDocumenti(this.offerta.uuid).subscribe((docs) => {
        //   this.documenti = docs;
        // });
        this.dataService.getAttivitaTipologie().subscribe((res) => {
          this.tipologie = res;
          this.tipologie.filter(tipo => {
            if (tipo.id == this.offerta.tipologia) {
              this.tipoInterna = tipo.interna;
            }
          })
        });
        this.dataService.getRisorsaCompetenze(this.offerta.uuid).subscribe((res) => {
          this.offertaCompetenze = res;
        });
      },
        (err: any) => console.log(err),
        () => console.log('getOfferta'));
    });
  }

  modifica() {
    this.router.navigate(['modifica/offerta/'], { relativeTo: this.route });
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


  uploadDocument(fileInput) {
    // if (fileInput.target.files && fileInput.target.files[0]) {
    //   this.dataService.uploadDocumentToRisorsa(fileInput.target.files[0], this.offerta.uuid + '').subscribe((doc) => {
    //     this.dataService.downloadAttivitaDocumenti(this.offerta.uuid).subscribe((docs) => {
    //       this.documenti = docs;
    //     });
    //   });
    // }
  }

  deleteDoc(doc) {
    // const modalRef = this.modalService.open(DocumentoCancellaModal);
    // modalRef.componentInstance.documento = doc;
    // modalRef.result.then((result) => {
    //   if (result == 'deleted') {
    //     this.dataService.downloadAttivitaDocumenti(this.attivita.uuid).subscribe((docs) => {
    //       this.documenti = docs;
    //     });
    //   }
    // });
  }

  downloadDoc(doc) {
    // this.dataService.downloadDocumentBlob(doc).subscribe((url) => {
    //   const downloadLink = document.createElement("a");
    //   downloadLink.href = url;
    //   downloadLink.download = doc.nomeFile;
    //   document.body.appendChild(downloadLink);
    //   downloadLink.click();
    //   document.body.removeChild(downloadLink);    
    // });
  }

  updateCompetenzePiano() {
    this.router.navigate(['modifica/istituti/'], { relativeTo: this.route });
  }

  associaOfferta() {
    this.router.navigate(['associa/offerta'], { relativeTo: this.route });
  }


  menuContentShow() {
    this.showContent = !this.showContent;
  }

  
  setIstitutiLabel(off) {
    let label = '';
    if (off.istitutiAssociati.length == 1) {
        label = off.istitutiAssociati[0].nomeIstituto;    
    } else if (off.istitutiAssociati.length > 1) {
        label = off.istitutiAssociati.length + ' istituti associati';
    } else if (off.stato == 'bozza') {
        label = 'Nessun istituto associato'
    }
    return label;
}

}
