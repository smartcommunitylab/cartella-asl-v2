import { Component, Output, Input, EventEmitter } from '@angular/core';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service'

@Component({
  selector: 'conv-cancella-modal',
  templateUrl: './conv-cancella-modal.html',
  styleUrls: ['./conv-cancella-modal.scss']
})
export class ConvenzioneCancellaModal {
  closeResult: string;

  @Input() ente;
  @Input() convenzione;
  @Output() onDelete = new EventEmitter<string>();

  constructor(public activeModal: NgbActiveModal, public dataService: DataService) { }
 
  delete() {
    this.activeModal.close();
    this.onDelete.emit('deleted');
  }

}

