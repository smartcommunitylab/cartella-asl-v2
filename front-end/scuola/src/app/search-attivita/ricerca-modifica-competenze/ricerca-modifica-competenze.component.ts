import { Component, OnInit } from '@angular/core';
import { Competenza } from '../../shared/classes/Competenza.class';
import { DataService } from '../../core/services/data.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal/modal';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'cm-ricerca-modifica-competenze',
  templateUrl: './ricerca-modifica-competenze.component.html',
  styleUrls: ['./ricerca-modifica-competenze.component.css']
})
export class RicercaModificaCompetenzeComponent implements OnInit {
  ricercaParam: any;

  // piano: PianoAlternanza;
  addCompetenzeBtnText: string = "Aggiungi competenze";
  navTitle: string = "Modifica competenze ricerca";
  breadcrumbItems = [
    {
      title: "Lista programmi",
      location: "../../../../../../../incorso"
    },
    {
      title: "Dettaglio programma",
      location: "../../../../../"
    },
    {
      title: "Dettaglio ",
      location: "../../../"
    },
    {
      title: "Ricerca",
      location: "../"
    },
    {
      title: "Modifica Competenze",
      location: "./"
    }
  ];


  attachedCompetenze: Competenza[]; //competenze already added to the piano

  constructor(private dataService: DataService,
    private modalService: NgbModal,
    private router: Router,
    private activeRoute: ActivatedRoute) { }


  ngOnChanges() {
    this.initAttachedCompetenze();
  }
  ngOnInit() {
    this.initAttachedCompetenze();
  }

  initAttachedCompetenze() {
    this.activeRoute.params.subscribe(params => {
      this.ricercaParam = JSON.parse(sessionStorage.getItem('searchActivitiesByCompetenzeOptions'));
      // this.dataService.getListCompetenzeByIds(this.ricercaParam.competenze).subscribe(
      //   (competenze: Array<Competenza>) => {
      //     if (competenze) {
      //       this.attachedCompetenze = competenze;
      //     }
      //     else {
      //       this.attachedCompetenze = [];
      //     }
      //   },
      //   (err: any) => console.log(err),
      //   () => console.log('get Lista competenze'));
    });
  }
  addNewCompetenze(competenze: Competenza[]) {
    for (let i = 0; i < competenze.length; i++) {
      this.ricercaParam.competenze.push(competenze[i].id)
    };
    sessionStorage.setItem('searchActivitiesOptions', JSON.stringify(this.ricercaParam));
    this.router.navigate(['../'], { relativeTo: this.activeRoute });
  }

}
