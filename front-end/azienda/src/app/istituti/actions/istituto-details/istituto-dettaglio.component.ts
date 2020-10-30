import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';
import { environment } from '../../../../../src/environments/environment';
import { AttivitaAlternanza } from '../../../shared/classes/AttivitaAlternanza.class';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';
import { PaginationComponent } from '../../../shared/pagination/pagination.component';
import * as moment from 'moment';

@Component({
  selector: 'cm-istituto-dettaglio',
  templateUrl: './istituto-dettaglio.component.html',
  styleUrls: ['./istituto-dettaglio.component.scss']
})
export class IstitutoDettaglioComponent implements OnInit {
  istituto;
  filtro;
  attivitaAAs: AttivitaAlternanza[] = [];
  navTitle: string = "Dettaglio istituto";
  currentpage: number = 0;
  tipologie;
  stato;
  totalRecords: number = 0;
  pageSize: number = 10;
  showContent: boolean = false;
  menuContent = 'In questa pagina trovi tutte le attività degli studenti di questo istituto, ed alcuni dati per metterti in contatto.';
  stati = [{ "name": "In attesa", "value": "in_attesa" }, { "name": "In corso", "value": "in_corso" }, { "name": "Revisionare", "value": "revisione" }, { "name": "Archiviata", "value": "archiviata" }];
  env = environment;
  timeoutTooltip = 250;

  @ViewChild('tooltip') tooltip: NgbTooltip;
  @ViewChild('cmPagination') private cmPagination: PaginationComponent;

  breadcrumbItems = [
    {
      title: "Lista istituti",
      location: "../../",
    },
    {
      title: "Dettaglio istituto"
    }
  ];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService) {
    this.filtro = {
      titolo: '',
      stato: ''
    }
  }

  ngOnInit() {

    this.route.params.subscribe(params => {
      let id = params['id'];
      this.dataService.getistitutoDettaglio(id).subscribe((res) => {
        this.istituto = res;
        this.filtro.istitutoId = this.istituto.id;
        this.breadcrumbItems[1].title = this.istituto.name;

        this.dataService.getAttivitaTipologie().subscribe((res) => {
          this.tipologie = res;
          this.getAttivitaAltPage(1);
        });
      },
        (err: any) => console.log(err),
        () => console.log('getAttivita'));
    });

  }

  getAttivitaAltPage(page: number) {
    this.dataService.getAttivitaAlternanzaForEnteAPI(this.filtro, (page - 1), this.pageSize)
      .subscribe((response) => {
        this.totalRecords = response.totalElements;
        this.attivitaAAs = response.content;
        for (let aa of this.attivitaAAs) {
          this.tipologie.filter(tipo => {
            if (tipo.id == aa.tipologia) {
              aa.individuale = tipo.individuale;
            }
          })
        }
      }, (err: any) => console.log(err),
        () => console.log('getAttivitaAlternanzaForIstitutoAPI'));
  }

  getTipologia(tipologiaId) {
    if (this.tipologie) {
      return this.tipologie.find(data => data.id == tipologiaId);
    } else {
      return tipologiaId;
    }
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

 
  getEsperienzeistitutoCsv() {
    // this.dataService.getEsperienzeistitutoCsv(this.istituto).subscribe((doc) => {
    //   const downloadLink = document.createElement("a");
    //   downloadLink.href = doc.url;
    //   downloadLink.download = doc.filename;
    //   document.body.appendChild(downloadLink);
    //   downloadLink.click();
    //   document.body.removeChild(downloadLink);  
    // });
  }

}