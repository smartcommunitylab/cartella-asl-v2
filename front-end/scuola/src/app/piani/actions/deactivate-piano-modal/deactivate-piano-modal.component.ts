import { Component, Output, Input, EventEmitter, OnInit } from '@angular/core';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service'
import { PianoAlternanza } from '../../../shared/classes/PianoAlternanza.class';

@Component({
  selector: 'deactivate-piano-modal',
  templateUrl: './deactivate-piano-modal.html',
  styleUrls:['./deactivate-piano-modal.scss']
})
export class DeactivatePianoModal {
  closeResult: string;
  @Input() vecchioPiano;
  @Input() piano: PianoAlternanza;
  @Output() onSuccess = new EventEmitter();

  constructor(public activeModal: NgbActiveModal, private dataService: DataService) { }
  
  activate() {
    this.activeModal.close();
    this.onSuccess.emit('DEACTIVATE');
  }
}

