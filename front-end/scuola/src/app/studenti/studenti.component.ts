import { Component, OnInit, ViewChild } from '@angular/core';
import { PaginationComponent } from '../shared/pagination/pagination.component';
import { DataService } from '../core/services/data.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'cm-studenti',
  templateUrl: './studenti.component.html',
  styleUrls: ['./studenti.component.scss']
})
export class StudentiComponent implements OnInit {

  titolo = 'Lista studenti';
  menuContent = "In questa pagina trovi tutti gli studenti. Clicca sulla riga del singolo studente per visualizzare il dettaglio dei suoi progressi.";
  showContent: boolean = false;
  studenti;
  filtro;
  totalRecords: number = 0;
  pageSize: number = 10;
  currentpage: number = 0;

  @ViewChild('cmPagination') private cmPagination: PaginationComponent;

  constructor(
    private dataService: DataService,
    private route: ActivatedRoute,
    private router: Router) {
    this.filtro = {
      text: null,
      corsoId: null
    }
  }

  ngOnInit() {
    this.getStudenti(1);
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

  cerca() {
    this.cmPagination.changePage(1);
    this.getStudenti(1);
  }

  pageChanged(page: number) {
    this.currentpage = page;
    this.getStudenti(page);
  }

  getStudenti(page) {
    this.dataService.getStudenteForIstituto(this.filtro, (page - 1), this.pageSize)
      .subscribe((response) => {
        this.totalRecords = response.totalElements;
        this.studenti = response.content;
        this.studenti.forEach(studente => {
          if(studente.pianoId) {
            if (studente.oreSvolteTerza.total > 0 && studente.oreSvolteTerza.hours >= studente.oreSvolteTerza.total) {
              studente.oreSvolteTerza.pgBType = 'success';
            } else {
              studente.oreSvolteTerza.pgBType = 'warning';
            }
            if (studente.oreSvolteQuarta.total > 0 && studente.oreSvolteQuarta.hours >= studente.oreSvolteQuarta.total) {
              studente.oreSvolteQuarta.pgBType = 'success';
            } else {
              studente.oreSvolteQuarta.pgBType = 'warning';
            }
            if (studente.oreSvolteQuinta.total > 0 && studente.oreSvolteQuinta.hours >= studente.oreSvolteQuinta.total) {
              studente.oreSvolteQuinta.pgBType = 'success';
            } else {
              studente.oreSvolteQuinta.pgBType = 'warning';
            }
          } else {
            studente.oreSvolteTerza.pgBType = 'error';
            studente.oreSvolteQuarta.pgBType = 'error';
            studente.oreSvolteQuinta.pgBType = 'error';
          }
        });
      }
        ,
        (err: any) => console.log(err),
        () => console.log('get lista studenti'));
  }

  openDetail(studente) {
    this.router.navigate(['../list/detail', studente.idStudente], { relativeTo: this.route });
  }

  showTipRiga(ev, studente, year) {
    // console.log(ev.target.title);
    if(studente.pianoId) {
      let lableConPiano = ' Piano triennale completo al *percentage*% (*fraction*) ore'; // (n/m ore)
      lableConPiano = lableConPiano.replace('*percentage*', ((studente.oreSvolte /  studente.oreTotali) * 100).toFixed(0) );
      lableConPiano = lableConPiano.replace('*fraction*', studente.oreSvolte + '/' + studente.oreTotali);
      studente.toolTipRiga = lableConPiano;  
    } else {
      let labelSenzaPiano = 'Attenzione: non ci sono piani associati a questo corso di studio!';
      studente.toolTipRiga = labelSenzaPiano;
    }
    // let lableConPiano = ' Piano *YS* completo al *percentage*% (*fraction*) ore'; // (n/m ore)
    // if (studente.oreSvolteTerza && studente.oreSvolteQuarta && studente.oreSvolteQuinta) {
    //   switch (year) {
    //     case '3':
    //       {
    //         lableConPiano = lableConPiano.replace('*YS*', 'triennale');
    //         if (studente.oreSvolteTerza.total > 0) {
    //           lableConPiano = lableConPiano.replace('*percentage*', ((studente.oreSvolteTerza.hours / studente.oreSvolteTerza.total) * 100).toFixed(0));
    //         } else {
    //           lableConPiano = lableConPiano.replace('*percentage*', '0');
    //         }
    //         lableConPiano = lableConPiano.replace('*fraction*', studente.oreSvolteTerza.hours + '/' + studente.oreSvolteTerza.total);
    //         studente.toolTipRiga = lableConPiano;
    //         break;
    //       }
    //     case '4':
    //       {
    //         lableConPiano = lableConPiano.replace('*YS*', 'triennale');
    //         if (studente.oreSvolteQuarta.total > 0) {
    //           lableConPiano = lableConPiano.replace('*percentage*', ((studente.oreSvolteQuarta.hours / studente.oreSvolteQuarta.total) * 100).toFixed(0));
    //         } else {
    //           lableConPiano = lableConPiano.replace('*percentage*', '0');
    //         }
    //         lableConPiano = lableConPiano.replace('*fraction*', studente.oreSvolteQuarta.hours + '/' + studente.oreSvolteQuarta.total);
    //         studente.toolTipRiga = lableConPiano;
    //       };
    //     case '5':
    //       {
    //         lableConPiano = lableConPiano.replace('*YS*', 'triennale');
    //         if (studente.oreSvolteQuinta.total > 0) {
    //           lableConPiano = lableConPiano.replace('*percentage*', ((studente.oreSvolteQuinta.hours / studente.oreSvolteQuinta.total) * 100).toFixed(0));
    //         } else {
    //           lableConPiano = lableConPiano.replace('*percentage*', '0');
    //         }
    //         lableConPiano = lableConPiano.replace('*fraction*', studente.oreSvolteQuinta.hours + '/' + studente.oreSvolteQuinta.total);
    //         studente.toolTipRiga = lableConPiano;
    //         break;
    //       }
    //   }
    // } else {
    //   studente.toolTipRiga = labelSenzaPiano;
    // }

  }


}
