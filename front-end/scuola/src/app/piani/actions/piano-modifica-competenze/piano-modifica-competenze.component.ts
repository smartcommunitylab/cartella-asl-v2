import { Component, OnInit } from '@angular/core';
import { PianoAlternanza } from '../../../shared/classes/PianoAlternanza.class';
import { Competenza } from '../../../shared/classes/Competenza.class';
import { DataService } from '../../../core/services/data.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal/modal';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'cm-piano-modifica-competenze',
  templateUrl: './piano-modifica-competenze.component.html',
  styleUrls: ['./piano-modifica-competenze.component.scss']
})
export class PianoModificaCompetenzeComponent implements OnInit {

  piano: PianoAlternanza;
  addCompetenzeBtnText: string = "Associa competenze";
  navTitle: string = "";
  breadcrumbItems = [
    {
      title: "Lista piani",
      location: "../../../"
    },
    {
      title: "Dettaglio piano",
      location: "../../"
    },
    {
      title: "Associa competenze"
    }
  ];

  attachedCompetenze: Competenza[]; //competenze already added to the piano

  constructor(private dataService: DataService,
    private modalService: NgbModal,
    private router: Router,
    private activeRoute: ActivatedRoute) { }

  ngOnInit() {
    this.activeRoute.params.subscribe(params => {
      let id = params['id'];

      this.dataService.getPianoById(id).subscribe((piano: PianoAlternanza) => {
        this.piano = piano;
        this.dataService.getRisorsaCompetenze(this.piano.uuid).subscribe((res) => {
          this.attachedCompetenze = res;
        });

        this.navTitle = this.piano.titolo + ' - Aggiunta competenze';
      },
        (err: any) => console.log(err),
        () => console.log('get piano'));
    });
  }

  addNewCompetenze(competenze: Competenza[]) {
    this.dataService.assignCompetenzeToRisorsa(this.piano.uuid, competenze)
      .subscribe((res) => {
        this.router.navigate(['../../'], { relativeTo: this.activeRoute });
      },
        (err: any) => console.log(err));
  }

  /**/
  addCompetenze(){
    this.dataService.assignCompetenzeToRisorsa(this.piano.uuid, this.attachedCompetenze)
      .subscribe((res) => {
        this.router.navigate(['../../'], { relativeTo: this.activeRoute });
      },
        (err: any) => console.log(err));
  }
}
