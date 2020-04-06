import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';
import { CompetenzaDetailModalComponent } from '../../skills-selector/modals/competenza-detail-modal/competenza-detail-modal.component';

@Component({
  selector: 'cm-attivita-detail',
  templateUrl: './attivita-detail.component.html',
  styleUrls: ['./attivita-detail.component.css']
})
export class AttivitaDetailComponent implements OnInit {

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService,
    private modalService: NgbModal) { }

  navTitle: string = "Dettaglio attività";
  breadcrumbItems = [
    {
      title: "Piani alternanza",
      location: "../../"
    },
    {
      title: "Dettaglio attività",
      location: "./"
    }
  ];

  corsiStudio;
  tipologie;
  attivita;
  schedaValutazioneLink;

  ngOnInit() {
    this.route.params.subscribe(params => {
      let id = params['id'];

      this.dataService.getAttivitaTipologie().subscribe((res) => {
        this.tipologie = res;

        this.dataService.getAttivitaStudenteById(id).subscribe((attivita) => {

          this.attivita = attivita;
          this.navTitle = this.attivita.aa.titolo;

          this.dataService.getAttivitaTipologie().subscribe((res) => {
            this.tipologie = res;
          });

          this.tipologie.filter(tipo => {
            if (tipo.id == this.attivita.aa.tipologia) {
              this.attivita.aa.interna = tipo.interna;
            }
          })

          this.dataService.getAttivitaCompetenze(this.attivita.aa.uuid).subscribe((res => {
            this.attivita.competenze = res;
          }));

          this.dataService.getAttivitaDocumenti(this.attivita.es.uuid).subscribe(resp => {
            this.attivita.documenti = resp;
          });

        },
          (err: any) => console.log(err),
          () => console.log('get attivita studente by id'));
      });

    });

  }

  openDetailCompetenza(competenza, $event) {
    if ($event) $event.stopPropagation();
    const modalRef = this.modalService.open(CompetenzaDetailModalComponent, { size: "lg" });
    modalRef.componentInstance.competenza = competenza;
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

  getTipologiaAttivita(tipologiaId) {
    if (this.tipologie) {
      return this.tipologie.find(data => data.id == tipologiaId);
    } else {
      return tipologiaId;
    }
  }

  uploadDocument(fileInput) {
    if (fileInput.target.files && fileInput.target.files[0]) {
      this.dataService.uploadDocumentToRisorsa(fileInput.target.files[0], this.attivita.es.uuid).subscribe((doc) => {
        this.dataService.downloadRisorsaDocumenti(this.attivita.es.uuid).subscribe((docs) => {
          this.attivita.documenti = docs;
        });
      });
    }
  }

  openDocument(doc) {
    this.dataService.openDocument(doc);
  }

  deleteDocumento(doc) {
    this.dataService.deleteDocument(doc.uuid).subscribe(response => {
      this.dataService.downloadRisorsaDocumenti(this.attivita.es.uuid).subscribe((docs) => {
        this.attivita.documenti = docs;
      });
    })
  }

}