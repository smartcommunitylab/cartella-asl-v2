import { Component, Input, OnInit } from '@angular/core';
import { DataService } from '../../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';
import { GrowlerMessageType, GrowlerService } from '../../../core/growler/growler.service';


@Component({
  selector: 'cm-modifica-ore-studenti',
  templateUrl: './modifica-ore-studenti.html',
  styleUrls: ['./modifica-ore-studenti.scss']
})
export class ModificaOreStudentiComponent implements OnInit {

  constructor(
    private activeRoute: ActivatedRoute,
    private router: Router,
    private route: ActivatedRoute,
    private growler: GrowlerService,
    private dataService: DataService) {}

  attivita;
  esperienze;
  menuContent = "In questa pagina puoi inserire o modificare le ore assegnate a ciascuno studente. Questa è un’attività con rendicontazione a corpo, quindi non c’è una tabella giornaliera delle presenze. Per ciascuno studente, puoi inserire soltanto il numero totale di ore svolte.";
  showContent: boolean = false;
  forceErrorDisplay: boolean = false;
  
  breadcrumbItems = [
    {
      title: "Lista attività",
      location: "../../../.."
    },
    {
      title: "Dettaglio attività",
      location: "../../.."
    },
    {
      title: "Modifica ore studenti"
    }
  ];

  ngOnInit() {
    this.activeRoute.params.subscribe(params => {
      let id = params['id'];
      this.dataService.getAttivita(id).subscribe((res) => {
        this.attivita = res.attivitaAlternanza;
        this.dataService.getAttivitaPresenzeCorpo(id).subscribe((res) => {
          this.esperienze = res;
          this.esperienze.forEach(esp => {
            if (esp.oreRendicontate < 1) {
              esp.oreRendicontate = '-';
            }
          });
        });
      }, (err: any) => console.log(err),
        () => console.log('getAzienda'));
    });
  }

  cancel() {
    this.router.navigate(['../../..'], { relativeTo: this.route });
  }

  edit(es) {
    if (!es.oreRendicontate || es.oreRendicontate < 0 || es.oreRendicontate > this.attivita.ore) {
      this.forceErrorDisplay = true;
    } else {
      this.forceErrorDisplay = false;
    }
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

}
