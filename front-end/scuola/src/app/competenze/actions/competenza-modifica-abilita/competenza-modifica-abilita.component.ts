import { Component, OnInit } from '@angular/core';
import { Competenza } from '../../../shared/classes/Competenza.class';
import { DataService } from '../../../core/services/data.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal/modal';
import { Router, ActivatedRoute } from '@angular/router';
import { GrowlerService, GrowlerMessageType } from '../../../core/growler/growler.service';

@Component({
  selector: 'cm-competenza-modifica-abilita',
  templateUrl: './competenza-modifica-abilita.component.html',
  styleUrls: ['./competenza-modifica-abilita.component.scss']
})
export class CompetenzaModificaAbilitaComponent implements OnInit {

  breadcrumbItems = [
    {
      title: "Lista competenze",
      location: "../../../"
    },
    {
      title: "Dettaglio competenza",
      location: "../../"
    },
    {
      title: "Associa abilità"
    }
  ];


  abilita;
  listAbilita = [];
  forceErrorDisplay: boolean;
  competenza: Competenza;
  menuContent = "Raccolta di tutte le competenze sia ministeriali che di quelle inserite dalla scuola stessa. Le competenze inserite dalla scuola sono visibili solo ad essa e non condivise con altri istituti.";
  showContent: boolean = false;

  constructor(private dataService: DataService,
    private modalService: NgbModal,
    private router: Router,
    private activeRoute: ActivatedRoute,
    private growler: GrowlerService) { }

  ngOnInit() {
    this.activeRoute.params.subscribe(params => {
      let id = params['id'];

      this.dataService.getIstiutoCompetenzaDetail(id).subscribe(response => {
        this.competenza = response.competenza;
        if (this.competenza.abilita) {
          this.listAbilita = this.competenza.abilita;
        }

      },
        (err: any) => console.log(err),
        () => console.log('get istiuto competenza detail'));
    });
  }


  addNewAbilita() {
    if (this.allValidated()) {
      this.forceErrorDisplay = false;
      this.listAbilita.push(this.abilita);
      this.abilita=null;
      // this.growler.growl("Abilità aggiunta con successo", GrowlerMessageType.Success);
    } else {
      this.forceErrorDisplay = true;
    }
  }

  removeAbilita(abilita) {
    this.listAbilita.splice(this.listAbilita.indexOf(abilita), 1)
  }

  allValidated() {
    return (this.abilita && this.abilita != '');
  }

  save() {
    this.competenza.abilita = this.listAbilita;
    this.dataService.updateIstitutoCompetenza(this.competenza).subscribe((res) => {
      this.router.navigate(['../../'], { relativeTo: this.activeRoute });
    });
  }
  
  menuContentShow() {
    this.showContent = !this.showContent;
  }

}
