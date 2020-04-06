import { Component, Output, Input, EventEmitter } from '@angular/core';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service'

@Component({
  selector: 'ente-cancella-modal',
  templateUrl: './ente-cancella-modal.html',
  styleUrls: ['./ente-cancella-modal.scss']
})
export class EnteCancellaModal {
  closeResult: string;

  @Input() ente;
  @Output() onDelete = new EventEmitter<string>();

  constructor(public activeModal: NgbActiveModal, private dataService: DataService) { }
 
  delete() {
    this.activeModal.close();
    this.onDelete.emit('deleted');
  }

}

