import { Component, OnInit, Output, Input, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'cm-delete-ente-ruolo-modal',
  templateUrl: './delete-ente-ruolo-modal.component.html',
  styleUrls: ['./delete-ente-ruolo-modal.component.css']
})
export class DeleteRuoloEnteModalComponent implements OnInit {
  
  @Input() role: string;
  @Input() email: string;
  @Output() onDelete = new EventEmitter();

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
  }

  delete() {
    this.activeModal.close();
    this.onDelete.emit();
  }

}
