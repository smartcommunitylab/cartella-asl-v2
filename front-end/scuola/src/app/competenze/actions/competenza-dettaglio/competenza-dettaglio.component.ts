import { Component } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';
import { GrowlerService, GrowlerMessageType } from '../../../core/growler/growler.service';
import { CompetenzaCancellaModal } from '../cancella-competenza-modal/competenza-cancella-modal.component';
import { ReportCompetenza } from '../../../shared/classes/ReportCompetenza.class';

@Component({
  selector: 'Competenza-dettaglio',
  templateUrl: './competenza-dettaglio.html',
  styleUrls: ['./competenza-dettaglio.scss']
})
export class CompetenzaDettaglioComponent {
  reportCompetenza: ReportCompetenza;
  competenza;
  menuContent = "In questa pagina trovi il dettaglio di una singola competenza. Usa i tasti blu per modificare le diverse sezioni. Una competenza che sia già stata acquisita da almeno uno studente non è più eliminabile, ma può solo essere disattivata.";
  showContent: boolean = false;
 
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService,
    private modalService: NgbModal,
    private growler: GrowlerService) { }


  navTitle: string = "Dettaglio competenza";
  breadcrumbItems = [
    {
      title: "Lista competenze",
      location: "../../list"
    },
    {
      title: "Dettaglio competenza"     
    }
  ];

  ngOnInit() {
    this.route.params.subscribe(params => {
      let id = params['id'];
      this.dataService.getIstiutoCompetenzaDetail(id).subscribe(response => {
        this.competenza = response.competenza;
        this.reportCompetenza = response;
      },
        (err: any) => console.log(err),
        () => console.log('get istiuto competenza detail'));
    });
  }

  deleteCompetenza() {
    const modalRef = this.modalService.open(CompetenzaCancellaModal, {windowClass: "cancellaModalClass"});
    // send report data.    
    modalRef.componentInstance.competenzaReport = this.reportCompetenza;    
    modalRef.componentInstance.onDelete.subscribe((result) => {
      if (result == 'DEL') {
        this.dataService.deleteCompetenzaIstituto(this.competenza.id).subscribe(
          (response: boolean) => {
            if (response) { 
            this.growler.growl('Successo.', GrowlerMessageType.Success);
            this.router.navigate(['../../list'], { relativeTo: this.route });
            } else {
            this.growler.growl("Errore", GrowlerMessageType.Danger);
            }
          },
          (err: any) => {
            console.log(err);
            this.growler.growl("Errore", GrowlerMessageType.Danger);
          },
          () => {
            console.log('delete competenza')
          }
        );
      }
    });
  }

  editCompetenza() {
    this.router.navigate(['modifica/dati/'], { relativeTo: this.route });
  }

  updateCompetenzaAbilita() {
    this.router.navigate(['modifica/abilita/'], { relativeTo: this.route });
  }

  updateCompetenzeConoscenze() {
    this.router.navigate(['modifica/conoscenze/'], { relativeTo: this.route });
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }
}