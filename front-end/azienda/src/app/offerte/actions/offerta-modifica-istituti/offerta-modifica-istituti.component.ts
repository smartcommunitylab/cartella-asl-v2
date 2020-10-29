import { Component, OnInit } from '@angular/core';
import { DataService } from '../../../core/services/data.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal/modal';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'cm-offerta-modifica-istituti',
  templateUrl: './offerta-modifica-istituti.component.html',
  styleUrls: ['./offerta-modifica-istituti.component.scss']
})
export class OffertaModificaIstitutiComponent implements OnInit {

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
      title: "Associa istituto"
    }
  ];

  attachedIstituti: any[]; //istituti already associated with offerta
  menuContent = 'In questa pagina puoi scegliere quali istituti possono associare i loro studenti a questa offerta. Nella colonna di sinistra, usa il tasto “+” blu per associare l’istituto all’offerta. Se invece vuoi rimuoverlo dall’offerta, usa il tasto “x” in rosso nella colonna di destra. Ricordati di salvare le modifiche!';
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
        this.attachedIstituti = this.offerta.istitutiAssociati;
      },
        (err: any) => console.log(err),
        () => console.log('getOfferta'));
    });
  }

  addNewIstituti(istituti: any[]) {
    this.dataService.associaIstitutiToOfferta(this.offerta.id, istituti)
      .subscribe((res) => {
        this.router.navigate(['../../'], { relativeTo: this.activeRoute });
      },
        (err: any) => console.log(err));
  }

  addIstituti() {
    this.dataService.associaIstitutiToOfferta(this.offerta.id, this.attachedIstituti)
      .subscribe((res) => {
        this.router.navigate(['../../'], { relativeTo: this.activeRoute });
      },
        (err: any) => console.log(err));
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

}
