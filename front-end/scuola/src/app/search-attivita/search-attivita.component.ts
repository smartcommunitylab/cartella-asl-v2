import { Component, OnInit, EventEmitter, Output, Input, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { DataService } from '../core/services/data.service';
import { GeoService } from '../core/services/geo.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PianoAlternanza } from '../shared/classes/PianoAlternanza.class';
import { Competenza } from '../shared/classes/Competenza.class';
import { PaginationComponent } from '../shared/pagination/pagination.component';
import { ProgrammaAddAttivitaModal } from './dettaglio-attivita-add/dettaglio-attivita-add.component';
import { ProgrammaRicercaAddressModal } from './ricerca-address-modal/ricerca-address-modal.component';
import { ProgrammaOpenOpportunitaModal } from './dettaglio-opp-modal/dettaglio-opp-modal.component';

@Component({
  selector: 'search-attivita',
  templateUrl: './search-attivita.component.html',
  styleUrls: ['./search-attivita.component.css']
})
export class SearchAttivitaComponent implements OnInit {

  @ViewChild('cmPagination') cmPagination: PaginationComponent;

  @Output() addOpportunita = new EventEmitter();
  @Input() action: string;

  
  classe;
  studente;
  classeSearch: boolean = false;
  studenteSearch: boolean = false;
  selectedRaggio = "Tutti";
  selectedCompetenze:Competenza[];
  ricercaParams: any;
  idClasse: string;
  idPiano: string;
  piano: PianoAlternanza;
  tipologieAttivita;
  listaRaggi = [10, 20, 50];
  ricercaSemplice: boolean = true;
  listaOpportunita;
  items = [];
  location: string;
  coordinates: number[];
  showList = false;
  totalRecords: number = 0;
 
  pageSize: number = 10;
  datePickerConfig = {
    locale: 'it',
    firstDayOfWeek: 'mo'
  };
  filtro = {
    istitutoId:null,
    tipologia: {},
    competenze: [],
    fromDate: null,
    toDate: null,
    coordinates: [],
    distance: 0,
    individuale: null,
    annoCorso: '',
    titolo: null,
    order: 'titolo'
  }
  searchedCompetenzeNumber: number;
  
  constructor(private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService,
    private modalService: NgbModal,
    private geoService: GeoService) {

  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      if (params['classe']) {
        this.classe = JSON.parse(params['classe']);
      }
      if (params['studente']) {
        this.studente = JSON.parse(params['studente']);
      }
      if (params['id']) {
        this.idPiano = params['id'];
      }
      if (params['classeNome']) {
       this.filtro.individuale = false;
      }
      if (params['studenteId']) {
       this.filtro.individuale = true;
      }
      if (params['annoCorso']) {
        this.filtro.annoCorso = params['annoCorso'];
      } 

      this.ricercaParams = JSON.parse(sessionStorage.getItem('searchActivitiesOptions'));
      if (!this.ricercaParams) {
        this.router.navigate(['../'],{ relativeTo: this.route });
        return;
      }
      if (typeof this.ricercaParams.attivita === 'object') {      
        this.filtro.tipologia = "";
      } else {
        this.filtro.tipologia = this.ricercaParams.attivita;
      }
      // if (this.ricercaParams.istitutoId) {
      //   this.filtro.istitutoId = this.ricercaParams.istitutoId;
      // }
      this.filtro.istitutoId = this.dataService.istitutoId;

      this.filtro.competenze = this.ricercaParams.competenze;

      // this.dataService.getListCompetenzeByIds(this.ricercaParams.competenze).subscribe((competenze: Array<Competenza>) => {
      //   if (competenze) {
      //     this.selectedCompetenze = competenze;
      //   }        
      // })
      // if (this.idPiano) {
      //   this.dataService.getPianoByIdJson(this.idPiano).subscribe((piano: PianoAlternanza) => {
      //     this.piano = piano;
      //   },
      //     (err: any) => console.log(err),
      //     () => console.log('get piano'));
      // }
      this.dataService.getAttivitaTipologie().subscribe(tipologie => {
        this.tipologieAttivita = tipologie;
        this.searchOpportunita(1, this.filtro);
      },
        (err: any) => console.log(err),
        () => console.log('get tipologie attivita'));
    });

  }

  getTipologiaTitle(id) {
    if (!this.tipologieAttivita) return id;
    return this.tipologieAttivita.find(tipologia => tipologia.id == id).titolo;
  }
  
  
  addCompetenze() {
    sessionStorage.setItem('searchActivitiesByCompetenzeOptions', JSON.stringify(this.ricercaParams));
    this.router.navigate(['modificacompetenze'], { relativeTo: this.route });
  }
  matchCompetenzeLista() {
    this.listaOpportunita.forEach(opportunita => {
      opportunita.matchCompetenze = 0;
      opportunita.competenze.forEach(competenza => {
        if (this.filtro.competenze.indexOf(competenza.id) >= 0) {
          opportunita.matchCompetenze++;
          console.log("match");
        }
      });
    });
  }
  pageChanged(page: number) {
    this.search(page);
  }
  searchOpportunita(page,filtro) {
    // this.dataService.getOpportunitaByFilter((page - 1), this.pageSize, this.filtro).subscribe((opportunita) => {
    //   this.listaOpportunita = opportunita.content;
    //   this.totalRecords = opportunita.totalElements;
      
    //   this.matchCompetenzeLista();
    //   this.searchedCompetenzeNumber = this.filtro.competenze.length;

    //   // fill tipologia title string.
    //   this.fillTipoString();
     
    // })
  }
  search(page) {
    this.searchOpportunita(page,this.filtro);
  }
  ricercaChange() {
    this.ricercaSemplice = !this.ricercaSemplice;
    this.filtro.fromDate=null;
    this.filtro.coordinates=[];
    this.filtro.toDate=null;
    this.filtro.distance=0; 
    this.location="";
  }
  selectRaggioFilter(raggio) {
    this.filtro.distance = raggio;
    if (raggio) {
      this.selectedRaggio = raggio;
    }
    else {
      this.selectedRaggio = "Tutti"
    }
  }
  openDetailOpportunita(opportunita) {
    const modalRef = this.modalService.open(ProgrammaOpenOpportunitaModal, { size: "lg" });
    modalRef.componentInstance.opportunita = opportunita;
    modalRef.componentInstance.filtro = this.filtro;
    modalRef.componentInstance.oppTipologiaObj = this.tipologieAttivita.find(tip => tip.id == opportunita.tipologia);
  }

  removeCompetenza(competenza) {
    this.selectedCompetenze.splice(this.selectedCompetenze.indexOf(competenza), 1);
    this.ricercaParams.competenze = this.filtro.competenze = this.selectedCompetenze.map(a => a.id);
    sessionStorage.setItem('searchActivitiesOptions', JSON.stringify(this.ricercaParams));
  }

  openDetailCompetenza(competenza, $event) {
    // if ($event) $event.stopPropagation();
    // const modalRef = this.modalService.open(CompetenzaDetailModalComponent, { size: "lg" });
    // modalRef.componentInstance.competenza = competenza;
  }

  openAddOpportunita(opportunita) {
    const modalRef = this.modalService.open(ProgrammaAddAttivitaModal, { size: "lg" });
    modalRef.componentInstance.opportunita = opportunita;
    modalRef.componentInstance.piano = this.piano;
    modalRef.componentInstance.oppTipologiaObj = this.tipologieAttivita.find(tip => tip.id == opportunita.tipologia);
    modalRef.componentInstance.onAdd.subscribe((opportunita) => {
      this.addOpportunita.emit(opportunita);
    })
  }
  selectPlace(item) {
    //set name and coordinates of the selected place
    this.location = item.name;
    this.coordinates = [item.location[0], item.location[1]];
    this.filtro.coordinates = this.coordinates;
    this.showList = false;
  }

  createAttivitaForClasse() {
    return {
      annoScolastico: this.dataService.schoolYear
    }
  }
  createAttivitaForStudente() {
    return {
      annoScolastico: this.dataService.schoolYear
    }
  }

  getItems(ev: any) {
    if (!ev.target.value) {
      this.filtro.coordinates = [];
    }
    // get items from geocoder
    this.geoService.getAddressFromString(ev.target.value).then(locations => {
      // set val to the value of the searchbar
      let val = ev.target.value;
      if (locations instanceof Array) {
        this.items = locations;
      }
      // if the value is an empty string don't filter the items
      if (val && val.trim() != '') {

        // Filter the items
        this.items = this.items.filter((item) => {
          return (item.name.toLowerCase().indexOf(val.toLowerCase()) > -1);
        });

        // Show the results
        this.showList = true;
      } else {

        // hide the results when the query is empty
        this.showList = false;
      }
    });
  }
  chooseAddress(): void {
    const modalRef = this.modalService.open(ProgrammaRicercaAddressModal, { size: "lg" });
    if (this.coordinates) {
      modalRef.componentInstance.actualLocation = JSON.stringify({'name': this.location, 'coordinates': this.filtro.coordinates}); 
    }
    modalRef.componentInstance.onSelectAddress.subscribe((res) => {
      let address = JSON.parse(res);
      this.location = address.name;
      this.filtro.coordinates = address.coordinates;
      this.coordinates = [address.coordinates[0], address.coordinates[1]];
    })
  }

  fillTipoString() {
    for (let opp of this.listaOpportunita) {
      opp.tipoTitle = this.getTipologiaTitle(opp.tipologia);
    }

  }

  onSelectChange(ev) {
    this.cmPagination.changePage(1);
    this.searchOpportunita(1, this.filtro);
  }
  
}

