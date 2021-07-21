import { Component, OnInit, Output, EventEmitter, ViewChild, Input } from '@angular/core';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
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

  profile = { 'name': null, 'surname': null, 'cf': null, 'email': null };

  @Output() newUtenteListener = new EventEmitter<Object>();
  @ViewChild('cmPagination') private cmPagination: PaginationComponent;

  constructor(
    public activeModal: NgbActiveModal,
    public dataService: DataService
    ) { }

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

    

}
