import { Component, OnInit, ViewChild } from '@angular/core';
import { PaginationComponent } from '../shared/pagination/pagination.component';
import { DataService } from '../core/services/data.service';
import { ActivatedRoute, Router } from '@angular/router';
import { StateStorageService } from '../core/auth/state-storage.service';

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
  filterSearch = false;
  totalRecords: number = 0;
  pageSize: number = 10;
  currentpage: number = 0;
  @ViewChild('cmPagination') private cmPagination: PaginationComponent;

  constructor(
    private dataService: DataService,
    private route: ActivatedRoute,
    private router: Router,
    private storageService: StateStorageService) {
    this.filtro = {
      text: null
    }
  }

  ngOnInit() {
    // retrieve filter states.
    this.filtro = JSON.parse(this.storageService.getfiltroStudente());
    if (!this.filtro) {
      this.filtro = {
        text: ''
      };
    }
    this.getStudenti(1);
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

  cerca() {
    this.cmPagination.changePage(1);
    this.filterSearch = true;
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
          if (studente.pianoId) {

            if (studente.oreSvolteSeconda) {
              if (studente.oreSvolteSeconda.total > 0 && studente.oreSvolteSeconda.hours >= studente.oreSvolteSeconda.total) {
                studente.oreSvolteSeconda.pgBType = 'success';
              } else {
                studente.oreSvolteSeconda.pgBType = 'warning';
              }
            }

            if (studente.oreSvolteTerza) {
              if (studente.oreSvolteTerza.total > 0 && studente.oreSvolteTerza.hours >= studente.oreSvolteTerza.total) {
                studente.oreSvolteTerza.pgBType = 'success';
              } else {
                studente.oreSvolteTerza.pgBType = 'warning';
              }
            }

            if (studente.oreSvolteQuarta) {
              if (studente.oreSvolteQuarta.total > 0 && studente.oreSvolteQuarta.hours >= studente.oreSvolteQuarta.total) {
                studente.oreSvolteQuarta.pgBType = 'success';
              } else {
                studente.oreSvolteQuarta.pgBType = 'warning';
              }
            }

            if (studente.oreSvolteQuinta) {
              if (studente.oreSvolteQuinta.total > 0 && studente.oreSvolteQuinta.hours >= studente.oreSvolteQuinta.total) {
                studente.oreSvolteQuinta.pgBType = 'success';
              } else {
                studente.oreSvolteQuinta.pgBType = 'warning';
              }
            }

          } else {
            studente.oreSvolteSeconda.pgBType = 'error';
            studente.oreSvolteTerza.pgBType = 'error';
            studente.oreSvolteQuarta.pgBType = 'error';
            studente.oreSvolteQuinta.pgBType = 'error';
          }
        });
      },
        (err: any) => console.log(err),
        () => console.log('get lista studenti'));
  }

  setProgressBarType(std, anno) {
    var label = '-';
    switch (anno) {
      case 2: {
        label = std.oreSvolteSeconda.pgBType;
        break;
      }
      case 3: {
        label = std.oreSvolteTerza.pgBType;
        break;
      }
      case 4: {
        label = std.oreSvolteQuarta.pgBType;
        break;
      }
      case 5: {
        label = std.oreSvolteQuinta.pgBType;
        break;
      }
    }
    return label;
  }

  openDetail(studente) {
    this.router.navigate(['../list/detail', studente.idStudente], { relativeTo: this.route });
  }

  showTipRiga(ev, studente, year) {
    if (studente.pianoId) {
      let lableConPiano = ' Piano triennale completo al *percentage*% (*fraction*) ore'; // (n/m ore)
      lableConPiano = lableConPiano.replace('*percentage*', ((studente.oreValidate / studente.oreTotali) * 100).toFixed(0));
      lableConPiano = lableConPiano.replace('*fraction*', studente.oreValidate + '/' + studente.oreTotali);
      studente.toolTipRiga = lableConPiano;
    } else {
      let labelSenzaPiano = 'Attenzione: non ci sono piani associati a questo corso di studio!';
      studente.toolTipRiga = labelSenzaPiano;
    }
  }

  refreshStudenti() {
    this.filtro = {
      text: ''
    }
    this.filterSearch = false;
    this.getStudenti(1);
  }

  customSearchOption() {
    var style = {
      'border-bottom': '2px solid #06c',
      'font-weight': 'bold'
    };
    if (this.filtro.text != '') {
      return style;
    }
  }

  ngOnDestroy() {
    this.storageService.storefiltroStudente(JSON.stringify(this.filtro));
  }

}
