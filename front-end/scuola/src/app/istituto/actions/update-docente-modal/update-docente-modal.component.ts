import { Component, OnInit, Output, EventEmitter, ViewChild } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { PaginationComponent } from '../../../shared/pagination/pagination.component';
import { DataService } from '../../../core/services/data.service';

@Component({
  selector: 'update-docente-modal',
  templateUrl: './update-docente-modal.component.html',
  styleUrls: ['./update-docente-modal.component.scss']
})
export class UpdateDocenteModalComponent implements OnInit {
  registeredDocenti;
  professori;
  tobeSaved = [];
  totalRecords: number = 0;
  pageSize: number = 10;
  currentpage: number = 0;
  filtro;
  forceErrorDisplay: boolean;
  forceErrorDisplayNome: boolean = false;
  forceErrorDisplayCognome: boolean = false;
  forceErrorDisplayCF: boolean = false;
  forceErrorDisplayMail: boolean = false;

  @Output() newUtenteListener = new EventEmitter<Object>();
  @ViewChild('cmPagination') private cmPagination: PaginationComponent;

  constructor(
    public activeModal: NgbActiveModal,
    public dataService: DataService
  ) {}

  ngOnInit() {
    this.getListPage(1);
  }

  getListPage(page: number) {
    this.dataService.getRegistrazioneDocente().subscribe((response) => {
      this.registeredDocenti = response;
      this.dataService.listPerAggiungiAccount((page - 1), this.pageSize, this.filtro)
        .subscribe((response) => {
          this.totalRecords = response.totalElements;
          this.professori = response.content;
          // set checkboxes.
          this.updateCheckBoxes();
        },
          (err: any) => console.log(err),
          () => console.log('get list per aggiungi account api'));
    },
      (err: any) => console.log(err),
      () => console.log('get registrazione docente api'));
 }

  updateCheckBoxes() {
    this.registeredDocenti.forEach(regDocente => {
      this.professori.forEach((elem2) => {
        if (elem2.referenteAlternanza.cf === regDocente.cfDocente) {
          regDocente.id = elem2.referenteAlternanza.id;
          elem2.referenteAlternanza.checked = true;
          elem2.referenteAlternanza.disabled = true;
        }
      });
    });
  }

  cerca() {
    this.cmPagination.changePage(1);
    this.getListPage(1);
  }

  pageChanged(page: number) {
    this.currentpage = page;
    this.getListPage(page);
  }

  trimValue(event, type) {
    if (type == 'nome') {
      (event.target.value.trim().length == 0) ? this.forceErrorDisplayNome = true : this.forceErrorDisplayNome = false;
    } else if (type == 'cognome') {
      (event.target.value.trim().length == 0) ? this.forceErrorDisplayCognome = true : this.forceErrorDisplayCognome = false;
    } else if (type == 'cf') {
      (event.target.value.trim().length == 0) ? this.forceErrorDisplayCF = true : this.forceErrorDisplayCF = false;
    } else if (type == 'mail') {
      (event.target.value.trim().length == 0) ? this.forceErrorDisplayMail = true : this.forceErrorDisplayMail = false;
    } else if (type == 'trim') {
      event.target.value = event.target.value.trim();
    }
  }

  setClasseAssociate(listClasse) {
    let classeString = '';
    listClasse.forEach(element => {
      classeString += element.classroom + '-';
    });
    classeString = classeString.replace(/.$/, "");
    return classeString;
  }

  onFilterChange(prof) {
    prof.attivo = -1;
    prof.referenteAlternanza.checked = !prof.referenteAlternanza.checked;

    var index = this.tobeSaved.findIndex(x => x.id == prof.referenteAlternanza.id)
    if (index > -1) {
      //found.
      if (!prof.referenteAlternanza.checked) {
        this.tobeSaved.splice(index, 1);
      }
    } else {
      if (prof.referenteAlternanza.checked) {
        var registerNewDocente = {};
        registerNewDocente['id'] = prof.referenteAlternanza.id;
        this.tobeSaved.push(registerNewDocente);
      }
    }
  }

  confirm() {
    this.newUtenteListener.emit(this.tobeSaved);
    this.activeModal.dismiss();
  }

}