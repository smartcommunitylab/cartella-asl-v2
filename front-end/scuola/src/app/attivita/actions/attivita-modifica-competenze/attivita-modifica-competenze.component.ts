import { Component, OnInit } from '@angular/core';
import { Competenza } from '../../../shared/classes/Competenza.class';
import { DataService } from '../../../core/services/data.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal/modal';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'cm-attivita-modifica-competenze',
  templateUrl: './attivita-modifica-competenze.component.html',
  styleUrls: ['./attivita-modifica-competenze.component.scss']
})
export class AttivitaModificaCompetenzeComponent implements OnInit {

  attivita
  addCompetenzeBtnText: string = "Associa competenze";
  navTitle: string = "";
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
      title: "Associa competenze"
    }
  ];

  attachedCompetenze: Competenza[]; //competenze already added to the piano
  menuContent = 'In questa pagina puoi associare le competenze all’attività. A sinistra trovi tutte le competenze associabili, a destra quelle già associate. Usa il “+” blu per associare una competenza, e la “x” rossa per rimuovere una competenza associata. Ricordati di salvare!';
  showContent: boolean = false;

  constructor(private dataService: DataService,
    private modalService: NgbModal,
    private router: Router,
    private activeRoute: ActivatedRoute) { }

  ngOnInit() {
    this.activeRoute.params.subscribe(params => {
      let id = params['id'];

      this.dataService.getAttivita(id).subscribe((res) => {
        this.attivita = res.attivitaAlternanza;
        this.dataService.getRisorsaCompetenze(this.attivita.uuid).subscribe((res) => {
          this.attachedCompetenze = res;
        });

        this.navTitle = this.attivita.titolo + ' - Aggiunta competenze';
      },
        (err: any) => console.log(err),
        () => console.log('getAttivita'));
    });
  }

  addNewCompetenze(competenze: Competenza[]) {
    this.dataService.assignCompetenzeToRisorsa(this.attivita.uuid, competenze)
      .subscribe((res) => {
        this.router.navigate(['../../'], { relativeTo: this.activeRoute });
      },
        (err: any) => console.log(err));
  }
  addCompetenze(){
    this.dataService.assignCompetenzeToRisorsa(this.attivita.uuid, this.attachedCompetenze)
      .subscribe((res) => {
        this.router.navigate(['../../'], { relativeTo: this.activeRoute });
      },
        (err: any) => console.log(err));
  }
  menuContentShow() {
    this.showContent = !this.showContent;
  }

}
