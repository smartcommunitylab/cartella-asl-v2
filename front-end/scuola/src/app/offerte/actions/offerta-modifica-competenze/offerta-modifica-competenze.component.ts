import { Component, OnInit } from '@angular/core';
import { Competenza } from '../../../shared/classes/Competenza.class';
import { DataService } from '../../../core/services/data.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal/modal';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'cm-offerta-modifica-competenze',
  templateUrl: './offerta-modifica-competenze.component.html',
  styleUrls: ['./offerta-modifica-competenze.component.scss']
})
export class OffertaModificaCompetenzeComponent implements OnInit {

  offerta;
  breadcrumbItems = [
    {
      title: "Lista offerte",
      location: "../../../"
    },
    {
      title: "Dettaglio offerta",
      location: "../../"
    },
    {
      title: "Associa competenze"
    }
  ];

  attachedCompetenze: Competenza[]; //competenze already added to the piano
  menuContent = 'In questa pagina puoi associare le competenze all’offerta. A sinistra trovi tutte le competenze associabili, a destra quelle già associate. Usa il “+” blu per associare una competenza, e la “x” rossa per rimuovere una competenza associata. Ricordati di salvare!';
  showContent: boolean = false;

  constructor(private dataService: DataService,
    private modalService: NgbModal,
    private router: Router,
    private activeRoute: ActivatedRoute) { }

  ngOnInit() {
    this.activeRoute.params.subscribe(params => {
      let id = params['id'];

      this.dataService.getOfferta(id).subscribe((off) => {
        this.offerta = off;
        this.dataService.getRisorsaCompetenze(this.offerta.uuid).subscribe((res) => {
          this.attachedCompetenze = res;
        });
      },
        (err: any) => console.log(err),
        () => console.log('getOfferta'));
    });
  }

  addNewCompetenze(competenze: Competenza[]) {
    this.dataService.assignCompetenzeToRisorsa(this.offerta.uuid, competenze)
      .subscribe((res) => {
        this.router.navigate(['../../'], { relativeTo: this.activeRoute });
      },
        (err: any) => console.log(err));
  }

  addCompetenze(){
    this.dataService.assignCompetenzeToRisorsa(this.offerta.uuid, this.attachedCompetenze)
      .subscribe((res) => {
        this.router.navigate(['../../'], { relativeTo: this.activeRoute });
      },
        (err: any) => console.log(err));
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

}
