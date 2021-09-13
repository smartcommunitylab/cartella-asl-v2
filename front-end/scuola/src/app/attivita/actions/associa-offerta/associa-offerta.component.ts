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
  rendicontazioneOreCorpo: boolean = false;

  constructor(private dataService: DataService,
    private modalService: NgbModal,
    private router: Router,
    private route: ActivatedRoute,
    private growler: GrowlerService) { }

  ngOnInit() {
    this.route.params.subscribe(params => {
      console.log(params['modalita'].toLocaleLowerCase());
      this.rendicontazioneOreCorpo = params['modalita'].toLocaleLowerCase()==='true'?true:false;
    });
  }

  addNewOfferta(offerte) {
    this.offertaAssociata = offerte;
  }

  associaOfferta() {
    this.dataService.associaOffertaToAttivita(this.offertaAssociata[0].id, this.rendicontazioneOreCorpo).subscribe((attivita) => {
      this.growler.growl('Offerta associata con successo!', GrowlerMessageType.Success);
      // this.router.navigate(['../../../detail/' + attivita.id +'/modifica/attivita'], { relativeTo: this.route });
      this.router.navigateByUrl('/attivita/detail/' + attivita.id); 
    },
      (err: any) => console.log(err));
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

}
