import { Component, OnInit } from '@angular/core';
import { DataService } from '../../../core/services/data.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal/modal';
import { Router, ActivatedRoute } from '@angular/router';
import { GrowlerService, GrowlerMessageType } from '../../../core/growler/growler.service';

@Component({
  selector: 'cm-associa-offerta',
  templateUrl: './associa-offerta.component.html',
  styleUrls: ['./associa-offerta.component.scss']
})
export class AssociaOffertaComponent implements OnInit {

  breadcrumbItems = [
    {
      title: "Lista attività",
      location: "../../../"
    },
    {
      title: "Nome attività",
    },
    {
      title: "Associa offerta"
    }
  ];

  offertaAssociata: any[]=[];
  menuContent = 'In questa pagina puoi associare un’offerta all’attività. Seleziona l’offerta che preferisci, quindi usa il tasto “Associa offerta” per salvare l’associazione. L’attività riceverà in automatico i dati dell’offerta.';
  showContent: boolean = false;

  constructor(private dataService: DataService,
    private modalService: NgbModal,
    private router: Router,
    private route: ActivatedRoute,
    private growler: GrowlerService) { }

  ngOnInit() {
  }

  addNewOfferta(offerte) {
    this.offertaAssociata = offerte;
  }

  associaOfferta() {
    this.dataService.associaOffertaToAttivita(this.offertaAssociata[0].id).subscribe((attivita) => {
      this.growler.growl('Offerta associata con successo!', GrowlerMessageType.Success);
      this.router.navigate(['../../../detail/', attivita.id], { relativeTo: this.route });
    },
      (err: any) => console.log(err));
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

}
