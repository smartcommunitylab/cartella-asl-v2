import { Component, OnInit } from '@angular/core';
import { Competenza } from '../../../shared/classes/Competenza.class';
import { DataService } from '../../../core/services/data.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal/modal';
import { Router, ActivatedRoute } from '@angular/router';
import { GrowlerService, GrowlerMessageType } from '../../../core/growler/growler.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'cm-competenza-modifica-conoscenze',
  templateUrl: './competenza-modifica-conoscenze.component.html',
  styleUrls: ['./competenza-modifica-conoscenze.component.scss']
})
export class CompetenzaModificaConoscenzeComponent implements OnInit {

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
      title: "Associa conoscenza"
    }
  ];


  conoscenza;
  listConoscenze = [];
  forceErrorDisplay: boolean;
  competenza: Competenza;
  menuContent = "Raccolta di tutte le competenze sia ministeriali che di quelle inserite dalla scuola stessa. Le competenze inserite dalla scuola sono visibili solo ad essa e non condivise con altri istituti.";
  showContent: boolean = false;
  evn = environment;

  constructor(private dataService: DataService,
    private modalService: NgbModal,
    private router: Router,
    private activeRoute: ActivatedRoute,
    private growler: GrowlerService) { }

  ngOnInit() {
    this.evn.modificationFlag=true;
    this.activeRoute.params.subscribe(params => {
      let id = params['id'];

      this.dataService.getIstiutoCompetenzaDetail(id).subscribe(response => {
        this.competenza = response.competenza;
        if (this.competenza.conoscenze) {
          this.listConoscenze = this.competenza.conoscenze;
        }

      },
        (err: any) => console.log(err),
        () => console.log('get istiuto competenza detail'));
    });
  }

  ngOnDestroy(){
    this.evn.modificationFlag=false;
  }

  addNewConoscenza() {
    if (this.allValidated()) {
      this.forceErrorDisplay = false;
      this.listConoscenze.push(this.conoscenza);
      this.conoscenza=null;
      // this.growler.growl("Conoscenza associata con successo!", GrowlerMessageType.Success);
    } else {
      this.forceErrorDisplay = true;
    }
  }

  removeConoscenza(conoscenza) {
    this.listConoscenze.splice(this.listConoscenze.indexOf(conoscenza), 1)
  }

  allValidated() {
    return (this.conoscenza && this.conoscenza != '');
  }

  save() {
    this.competenza.conoscenze = this.listConoscenze;
    this.dataService.updateIstitutoCompetenza(this.competenza).subscribe((res) => {
      this.router.navigate(['../../'], { relativeTo: this.activeRoute });
    });
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }
}
