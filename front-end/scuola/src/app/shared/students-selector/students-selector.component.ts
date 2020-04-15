import { Component, Input, Output, EventEmitter, OnInit, ViewChild } from '@angular/core';
import { PaginationComponent } from '../pagination/pagination.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal/modal';
import { ConfirmModalComponent } from './modals/confirm-modal/confirm-modal.component'
import { Studente } from '../classes/Studente.class';
import { DataService } from '../../core/services/data.service';
import { GrowlerService, GrowlerMessageType } from '../../core/growler/growler.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'cm-students-selector',
  templateUrl: './students-selector.component.html',
  styleUrls: ['./students-selector.component.scss']
})
export class StudentsSelectorComponent implements OnInit {
  @Input() individuale: boolean;
  @Input() attachedStudenti: Studente[];
  @Input() addButtonText: string;
  @Input() attachedId; // attivita, risorsa ecc.
  @Output() onNewStudenteAddedListener = new EventEmitter<Studente[]>();

  @ViewChild('cmPagination') cmPagination: PaginationComponent;
  totalRecords: number = 0;
  @ViewChild('cmPaginationAssociate') cmPaginationAssociate: PaginationComponent;
  totalRecordsAssociate: number = 0;

  pageSize: number = 10;
  studenti: Studente[];
  studentiAssociateWindow: Studente[];
  searchStudenteNome: string;
  searchStudenteAssociateNome: string;
  sentStudenti;
  attivita;
  evn = environment;

  constructor(
    private dataService: DataService,
    private modalService: NgbModal,
    private growler: GrowlerService,) {
  }

  ngOnInit() {
    this.evn.modificationFlag=true;
    this.sentStudenti = this.attachedStudenti.slice(0);

    this.dataService.getAttivita(this.attachedId).subscribe((res) => {
      this.attivita = res.attivitaAlternanza;
      this.getStudente(1);
      this.getStudenteAssociate(1);
    });
    
  }

  ngOnChanges(changes) {
    this.mergeStudente();
  }
  
  ngOnDestroy(){
    this.evn.modificationFlag=false;
  }

  mergeStudente() {
    if (this.studenti && this.attachedStudenti) {
      this.studenti.forEach(element => {
        if (this.attachedStudenti.find(function (el) { return el.studenteId == element.studenteId; })) {
          element['disabled'] = true;
        }
      });
    }
  }

  pageChanged(page: number) {
    this.getStudente(page);
  }
  pageChangedAssociate(page: number) {
    this.getStudenteAssociate(page);
  }

  getStudente(page) {
    this.dataService.getAttivitaRegistrations(this.searchStudenteNome, this.attachedId, (page - 1), this.pageSize).subscribe((res) => {
      this.studenti = res.content;
      this.totalRecords = res.totalElements;
      this.mergeStudente();
    });
  }

  getStudenteAssociate(page: number) {

    this.studentiAssociateWindow = this.attachedStudenti.slice((page - 1) * this.pageSize, page * this.pageSize);
    this.totalRecordsAssociate = this.attachedStudenti.length;

    if (this.searchStudenteAssociateNome) {
      var txt = this.searchStudenteAssociateNome;
      this.studentiAssociateWindow = this.attachedStudenti.filter(function (tag) {
        if (tag.nominativoStudente.indexOf(txt.toUpperCase()) >= 0 || tag.classeStudente.indexOf(txt.toUpperCase()) >= 0) {
          return tag;
        }
        // return tag.nominativoStudente.indexOf(txt.toUpperCase()) >= 0;
      });
    }

  }

  searchStudente() {
    this.cmPagination.changePage(1);
    this.getStudente(1);
  }

  searchStudenteAssociate() {
    this.cmPaginationAssociate.changePage(1);
    this.getStudenteAssociate(1);
  }

  toggleStudente(studente) {
    if (!this.individuale) {
      if (this.attachedStudenti.indexOf(studente) < 0) {
        this.attachedStudenti.push(studente);
        this.getStudenteAssociate(1);
      }
      this.studenti.forEach(element => {
        if (element.studenteId == studente.studenteId) {
          element['disabled'] = true;
        }
      });
    } else {
      if (this.attachedStudenti.length > 0) {
        let message = "Attenzione. Questa è una tipologia di attività singola, che permette l'associazione di UN SOLO studente.\n Se vuoi associare un nuovo studente, cancella prima quello già associato.";
        this.growler.growl(message, GrowlerMessageType.Warning);
        //const modalRef = this.modalService.open(ConfirmModalComponent, { windowClass: "cancellaModalClass" });
        //modalRef.componentInstance.showBody = false;
        //modalRef.componentInstance.popupText = "Attenzione. Questa è una tipologia di attività singola, che permette l'associazione di UN SOLO studente.\n Se vuoi associare un nuovo studente, cancella prima quello già associato.";
      } else {
        if (this.attachedStudenti.indexOf(studente) < 0) {
          this.attachedStudenti.push(studente);
          this.getStudenteAssociate(1);
        }
        this.studenti.forEach(element => {
          if (element.studenteId == studente.studenteId) {
            element['disabled'] = true;
          }
        });
      }
    }

  }

  addStudenti() {
    if (this.attachedStudenti.length == 0) {
      const modalRef = this.modalService.open(ConfirmModalComponent, { windowClass: "cancellaModalClass" });
      modalRef.componentInstance.showBody = true;
      modalRef.componentInstance.popupText = 'Non ci sono studenti associati a questa attività. Salvare ugualmente le modifiche?';
      modalRef.componentInstance.actionListener.subscribe((res) => {
        if (res == 'ok') {
          this.onNewStudenteAddedListener.emit(this.attachedStudenti);
        }
      });
    } else {
      let nRemoved = 0;
      this.sentStudenti.forEach(sent => {
        if (!this.containsObject(sent, this.attachedStudenti)) {
          nRemoved++;
        }
      })

      if (nRemoved > 0) {
        const modalRef = this.modalService.open(ConfirmModalComponent, { windowClass: "cancellaModalClass" });
        modalRef.componentInstance.showBody = true;
        modalRef.componentInstance.popupText = 'Stai per rimuovere' + nRemoved + ' studenti dall’attività: questo cancellerà tutte ore che hanno svolto su questa attività. Questa azione non è reversibile! Salvare ugualmente le modifiche';
        modalRef.componentInstance.actionListener.subscribe((res) => {
          if (res == 'ok') {
            this.onNewStudenteAddedListener.emit(this.attachedStudenti);
          }
        });
      } else {
        this.onNewStudenteAddedListener.emit(this.attachedStudenti);
      }
    }
  }

  containsObject(obj, list) {
    var i;
    for (i = 0; i < list.length; i++) {
      if (list[i].studenteId === obj.studenteId) {
        return true;
      }
    }

    return false;
  }

  deleteStudente(studente) {
    this.attachedStudenti.splice(this.attachedStudenti.indexOf(studente), 1);
    this.studenti.forEach(element => {
      if (element.studenteId == studente.studenteId) {
        element['disabled'] = false;
      }
    })
    this.getStudenteAssociate(1);
  }

}

