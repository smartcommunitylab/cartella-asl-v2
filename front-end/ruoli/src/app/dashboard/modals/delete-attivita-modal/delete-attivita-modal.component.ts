import { Component, OnInit, Output, Input, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'cm-delete-attivita-modal',
  templateUrl: './delete-attivita-modal.component.html',
  styleUrls: ['./delete-attivita-modal.component.css']
})
export class DeleteAttivitaModalComponent implements OnInit {
  
  @Input() titolo;
  @Output() onDelete = new EventEmitter();

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
  }

  delete() {
    this.activeModal.close();
    this.onDelete.emit();
  }

}
