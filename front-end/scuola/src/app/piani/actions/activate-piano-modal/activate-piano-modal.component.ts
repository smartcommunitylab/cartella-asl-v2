import { Component, Output, Input, EventEmitter } from '@angular/core';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service'
import { PianoAlternanza } from '../../../shared/classes/PianoAlternanza.class';

@Component({
  selector: 'activate-piano-modal',
  templateUrl: './activate-piano-modal.html',
  styleUrls:['./activate-piano-modal.scss']
})
export class ActivatePianoModal {
  closeResult: string;

  @Input() piano: PianoAlternanza;
  @Output() onSuccess = new EventEmitter();

  constructor(public activeModal: NgbActiveModal, private dataService: DataService) { }
  activate() {
    this.activeModal.close();
    this.onSuccess.emit('ACTIVATE');
  }
}

