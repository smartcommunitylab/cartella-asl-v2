import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../../../core/services/data.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal/modal';
import { Router, ActivatedRoute } from '@angular/router';
import { GrowlerService } from '../../../core/growler/growler.service';
import { ConfirmModalClasseComponent } from '../../actions/confirm-modal-classe/confirm-modal-classe.component';
import { PaginationComponent } from '../../../shared/pagination/pagination.component';
import { ConfirmModalAddClasseComponent } from '../confirm-modal-add-classe/confirm-modal--add-classe.component';
import { ConfirmModalRemoveClasseComponent } from '../confirm-modal-remove-classe/confirm-modal-remove-classe.component';

@Component({
  selector: 'cm-docente-modifica-studenti',
  templateUrl: './docente-modifica-studenti.component.html',
  styleUrls: ['./docente-modifica-studenti.component.scss']
})
export class DocenteModificaStudentiComponent implements OnInit {

  professoriClassi = [];
  classeAssociateWindow = [];
  registrazioneId;
  addRemovalState: string = "";
  nomeAccount;
  filterText;

  breadcrumbItems = [
    {
      title: "Istituto",
      location: "../../"
    },
    {
      title: "Gestione account docenti",
    }
  ];

  attachedClassi = []; //classe already added to professor.
  menuContent = "In questa sezione puoi selezionare quali classi saranno visibili sul portale EDIT per ciascun account professore. I docenti associati potranno monitorare tutto il percorso ASL del ragazzi, scaricare i dati e modificare competenze e documenti. Se si vuole dare loro anche i permessi di validazione/modifica ore sarà necessario assegnarli a ciascuna attività come “tutor scolastico”";
  showContent: boolean = false;

  @ViewChild('cmPagination') cmPagination: PaginationComponent;
  totalRecords: number = 0;
  @ViewChild('cmPaginationAssociate') cmPaginationAssociate: PaginationComponent;
  totalRecordsAssociate: number = 0;
  pageSize: number = 10;

  constructor(public dataService: DataService,
    private modalService: NgbModal,
    private router: Router,
    private activeRoute: ActivatedRoute,
    private growler: GrowlerService
  ) { }

  ngOnInit() {
    this.activeRoute.params.subscribe(params => {
      this.registrazioneId = params['id'];
      this.dataService.getRegistrazioneDocenteClassiDaAssociare(1, this.pageSize, this.registrazioneId, this.filterText).subscribe((res) => {
        this.professoriClassi = res.content;
        this.totalRecords = res.totalElements;
        this.dataService.getRegistrazioneDocenteDetail(this.registrazioneId).subscribe((res) => {
          this.nomeAccount = res.nominativoDocente;
          this.breadcrumbItems[1].title = 'Gestione account docenti / ' + this.nomeAccount;
          if (res.classi != null)
            this.attachedClassi = res.classi.split("-");
          this.totalRecordsAssociate = this.attachedClassi.length;
          this.mergeDocenti();
          this.getDocenteAssociate(1);
        });
      });
    });
  }

  getRegistrazione(page) {
    this.dataService.getRegistrazioneDocenteClassiDaAssociare(page-1, this.pageSize, this.registrazioneId, this.filterText).subscribe((res) => {
      this.professoriClassi = res.content;
      this.totalRecords = res.totalElements;
      this.dataService.getRegistrazioneDocenteDetail(this.registrazioneId).subscribe((res) => {
        this.nomeAccount = res.nominativoDocente;
        this.breadcrumbItems[1].title = 'Gestione account docenti / ' + this.nomeAccount;
        this.mergeDocenti();
        this.getDocenteAssociate(1);
      });
    },
      (err: any) => console.log(err),
      () => console.log('getAttivita'));
  }

  pageChanged(page: number) {
    this.getRegistrazione(page);
  }

  pageChangedAssociate(page: number) {
    this.getDocenteAssociate(page);
  }

  getDocenteAssociate(page: number) {
    this.classeAssociateWindow = this.attachedClassi.slice((page - 1) * this.pageSize, page * this.pageSize);
    this.totalRecordsAssociate = this.attachedClassi.length;
  }
  
  searchClasse() {
    this.cmPagination.changePage(1);
    this.getRegistrazione(1);
  }

  toggleClass(profClass) {
    if (this.addRemovalState == 'remove' || this.addRemovalState == 'both') {
      this.addRemovalState = 'both';
    } else {
      this.addRemovalState = 'add';
    }
    if (this.attachedClassi.indexOf(profClass.classe) < 0) {
      this.attachedClassi.push(profClass.classe.trim());
      this.getDocenteAssociate(1);
      this.professoriClassi.forEach(element => {
        if (element.classe == profClass.classe) {
          element['disabled'] = true;
        }
      });
    }
  }

  deleteClasse(classe) {
    if (this.addRemovalState == 'add' || this.addRemovalState == 'both') {
      this.addRemovalState = 'both';
    } else {
      this.addRemovalState = 'remove';
    }
    this.attachedClassi.splice(this.attachedClassi.indexOf(classe), 1);
    this.professoriClassi.forEach(element => {
      if (element.classe == classe.trim()) {
        element['disabled'] = false;
      }
    })
    this.getDocenteAssociate(1);
  }

  updateAssociazioneDocentiClassi() {
    if (this.addRemovalState == 'both') {
      this.popupGeneral();
    } else if (this.addRemovalState == 'add') {
      this.popupAddClasse();
    } else {
      this.popupRemoveClasse();
    }
  }

  popupRemoveClasse() {
    const modalRef = this.modalService.open(ConfirmModalRemoveClasseComponent, { windowClass: "cancellaModalClass" });
    modalRef.componentInstance.nomeAccount = this.nomeAccount;
    modalRef.componentInstance.actionListener.subscribe((res) => {
      if (res == 'ok') {
        this.performSave();
      }
    });
  }

  popupAddClasse() {
    const modalRef = this.modalService.open(ConfirmModalAddClasseComponent, { windowClass: "cancellaModalClass" });
    modalRef.componentInstance.nomeAccount = this.nomeAccount;
    modalRef.componentInstance.actionListener.subscribe((res) => {
      if (res == 'ok') {
        this.performSave();
      }
    });
  }

  popupGeneral() {
    const modalRef = this.modalService.open(ConfirmModalClasseComponent, { windowClass: "cancellaModalClass" });
    modalRef.componentInstance.nomeAccount = this.nomeAccount;
    modalRef.componentInstance.actionListener.subscribe((res) => {
      if (res == 'ok') {
        this.performSave();
      }
    });
  }

  performSave() {
    this.dataService.updateAssociazioneDocentiClassi(this.registrazioneId, this.attachedClassi)
      .subscribe((res) => {
        this.router.navigate(['../../'], { relativeTo: this.activeRoute });
      },
        (err: any) => console.log(err));
  }

  menuContentShow() {
    this.showContent = !this.showContent;
  }

  mergeDocenti() {
    if (this.professoriClassi && this.attachedClassi) {
      this.professoriClassi.forEach(element => {
        if (this.attachedClassi.find(function (el) { return el.trim() == element.classe; })) {
          element['disabled'] = true;
        }
      });
    }
  }

}
