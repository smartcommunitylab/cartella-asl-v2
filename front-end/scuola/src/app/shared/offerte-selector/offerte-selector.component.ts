import { Component, Input, Output, EventEmitter, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../../core/services/data.service'
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { environment } from '../../../environments/environment';
import { NgbTooltip } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'cm-offerte-selector',
  templateUrl: './offerte-selector.component.html',
  styleUrls: ['./offerte-selector.component.scss']
})
export class OfferteSelectorComponent implements OnInit {

  @Input() offertaAssociata: any[];
  @Output() onNewOffertaAddedListener = new EventEmitter<any[]>();

  filtro;
  filterSearch = false;
  offerte;
  selectedId;
  tipologie;
  tipologia = "Tipologia";
  owner;
  filterText;
  evn = environment;
  totalRecords: number = 0;
  pageSize: number = 10;
  menuContent = "In questa pagina puoi associare un’offerta all’attività. Seleziona l’offerta che preferisci, quindi usa il tasto “Associa offerta” per salvare l’associazione. L’attività riceverà in automatico i dati dell’offerta.";
  showContent: boolean = false;
  stati = [{ "name": "Disponibile", "value": "disponibile" }, { "name": "Scaduta", "value": "scaduta" }];
  sources = [{ "name": "Istituto", "value": "istituto" }, { "name": "Ente", "value": "ente" }];

  @ViewChild('cmPagination') cmPagination: PaginationComponent;
  @ViewChild('tooltip') tooltip: NgbTooltip;

  constructor(private dataService: DataService) {
    this.filtro = {
      tipologia: '',
      titolo: '',
      stato: '',
      ownerIstituto: null
    }
  }

  ngOnInit() {
    this.evn.modificationFlag = true;
    this.dataService.getAttivitaTipologie().subscribe((res) => {
      this.tipologie = res;
      if (this.tipologia && this.tipologia != 'Tipologia') {
        this.filtro.tipologia = this.tipologia;
      }
      this.getOffertePage(1);
    },
      (err: any) => console.log(err),
      () => console.log('getAttivitaTipologie'));
  }

  getOffertePage(page: number) {
    this.dataService.getOffeteForIstitutoAPI(this.filtro, (page - 1), this.pageSize)
      .subscribe((response) => {
        this.totalRecords = response.totalElements;
        this.offerte = response.content;
        if (this.offertaAssociata.length > 0) {
          var index = this.offerte.findIndex(x => x.id === this.offertaAssociata[0].id);
          if (index > -1) {
            this.offerte[index].valida = true;
          }
        }
      },
        (err: any) => console.log(err),
        () => console.log('getAttivitaAlternanzaForIstitutoAPI'));
  }

  ngOnDestroy() {
    this.evn.modificationFlag = false;
  }

  pageChanged(page: number) {
    this.getOffertePage(page);
  }

  searchCompetenza() {
    this.cmPagination.changePage(1);
    this.getOffertePage(1);
  }

  associaOfferta() {
    this.onNewOffertaAddedListener.emit(this.offertaAssociata);
    // this.getCompetenzeAssociate(1);
  }

  showPostiLiberi(off) {
    let label = '';
    label = off.toolTipoStatoRiga = off.postiRimanenti + '/' + off.postiDisponibili;
    return label;
  }

  setNomeEnte(off) {
    let label = '';
    if (off.nomeEnte) {
      label = off.nomeEnte;
    } else {
      label = 'Istituto';
    }
    return label;
  }

  setSource(off) {
    let label = '';
    if (off.istitutoId) {
      label = 'Istituto';
    } else {
      label = 'Ente';
    }
    return label;
  }

  getTipologia(tipologiaId) {
    if (this.tipologie) {
      return this.tipologie.find(data => data.id == tipologiaId);
    } else {
      return tipologiaId;
    }
  }

  cerca() {
    if (this.cmPagination)
      this.cmPagination.changePage(1);
    if (this.filterText) {
      this.filtro.titolo = this.filterText;
    } else {
      this.filtro.titolo = null;
    }
    this.filterSearch = true;
    this.getOffertePage(1);
  }

  selectTipologiaFilter() {
    if (this.cmPagination)
      this.cmPagination.changePage(1);
    if (this.tipologia && this.tipologia != 'Tipologia') {
      this.filtro.tipologia = this.tipologia;
    } else {
      this.filtro.tipologia = null;
    }
    this.filterSearch = true;
    this.getOffertePage(1);
  }

  selectOwnerFilter() {
    if (this.cmPagination)
      this.cmPagination.changePage(1);
    if (this.owner) {
      if (this.owner == 'istituto') {
        this.filtro.ownerIstituto = true;
      } else {
        this.filtro.ownerIstituto = false;
      }
    } else {
      this.filtro.ownerIstituto = null;
    }
    this.filterSearch = true;
    this.getOffertePage(1);
  }

  onFilterChange(off, tp) {
    this.filterSearch = true;
    tp.close();
    off.valida = !off.valida;
    off.toolTip = 'Attività selezionata';
    while (this.offertaAssociata.length > 0) {
      var index = this.offerte.findIndex(x => x.id === this.offertaAssociata[0].id);
      if (index > -1) {
        this.offerte[index].valida = false;
        this.offerte[index].toolTip = 'Seleziona attività'
      }
      this.offertaAssociata.pop();
    }
    this.offertaAssociata.push(off);
    tp.ngbTooltip = off.toolTip;
    tp.open();
    this.onNewOffertaAddedListener.emit(this.offertaAssociata);
  }

  cancelSelection() {
    while (this.offertaAssociata.length > 0) {
      var index = this.offerte.findIndex(x => x.id === this.offertaAssociata[0].id);
      if (index > -1) {
        this.offerte[index].valida = false;
        this.offerte[index].toolTip = 'Seleziona attività'
      }
      this.offertaAssociata.pop();
    }
    this.onNewOffertaAddedListener.emit(this.offertaAssociata);
  }

  showTip(ev, off, tp) {
    if (off.valida) {
      off.toolTip = 'Attività selezionata';
    } else {
      off.toolTip = 'Seleziona attività';
    }
    tp.open();
  }

  refreshOfferte() {
    this.filtro = {
      tipologia: '',
      titolo: '',
      stato: '',
      ownerIstituto: null
    }
    this.tipologia = "Tipologie"
    this.owner = undefined;
    this.filterText = undefined;
    this.filterSearch = false;
    this.getOffertePage(1);
  }

}
