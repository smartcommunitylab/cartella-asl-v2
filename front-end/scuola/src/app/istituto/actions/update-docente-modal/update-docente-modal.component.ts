import { Component, OnInit, Output, EventEmitter, ViewChild, Input } from '@angular/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { PaginationComponent } from '../../../shared/pagination/pagination.component';
import { DataService } from '../../../core/services/data.service';
import { ValidationService } from '../../../core/services/validation.service';

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
    public dataService: DataService,
    private validationService: ValidationService,
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

        },
          (err: any) => console.log(err),
          () => console.log('get list per aggiungi account api'));
    },
      (err: any) => console.log(err),
      () => console.log('get registrazione docente api'));

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


}