import { Component, OnInit } from '@angular/core';
import { DataService } from '../../../core/services/data.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal/modal';
import { Router, ActivatedRoute } from '@angular/router';
import { Studente } from '../../../shared/classes/Studente.class';

@Component({
  selector: 'cm-attivita-modifica-studenti',
  templateUrl: './attivita-modifica-studenti.component.html',
  styleUrls: ['./attivita-modifica-studenti.component.scss']
})
export class AttivitaModificaStudentiComponent implements OnInit {

  attivita
  addStudentiBtnText: string = "Associa studenti";
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
      title: "Associa studenti"
    }
  ];


  attachedStudenti: Studente[]; //students already added to the attivita
  attachedId: number;
  individuale: boolean;
  menuContent = "In questa pagina puoi associare gli studenti all’attività. Nella lista a sinistra trovi gli studenti che è possibile aggiungere, a destra invece quelli già aggiunti. Utilizza il tasto “+” blu per aggiungere uno studente, e la “x” rossa per rimuoverlo. Ricordati di salvare!";
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
        this.attachedStudenti = res.esperienze;
        this.attachedId = this.attivita.id;
        this.navTitle = this.attivita.titolo + ' - Aggiunta studenti';

        this.dataService.getAttivitaTipologie().subscribe((tipologie: any) => {
          tipologie.filter(tipo => { 
            if (tipo.id == this.attivita.tipologia) {
              this.individuale = tipo.individuale;
            }
          })
        });
        
      },
        (err: any) => console.log(err),
        () => console.log('getAttivita'));
    });
  }

  addNewStudenti(studenti: Studente[]) {
    this.dataService.assignStudentiToAttivita(this.attivita.id, studenti)
      .subscribe((res) => {
        this.router.navigate(['../../'], { relativeTo: this.activeRoute });
      },
        (err: any) => console.log(err));
  }
  addStudenti(){
    this.dataService.assignStudentiToAttivita(this.attivita.id, this.attachedStudenti)
      .subscribe((res) => {
        this.router.navigate(['../../'], { relativeTo: this.activeRoute });
      },
        (err: any) => console.log(err));
  }
  menuContentShow() {
    this.showContent = !this.showContent;
  }
  
}
