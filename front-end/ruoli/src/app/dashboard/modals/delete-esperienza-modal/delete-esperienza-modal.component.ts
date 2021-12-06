import { Component, OnInit, Output, Input, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'cm-delete-esperienza-modal',
  templateUrl: './delete-esperienza-modal.component.html',
  styleUrls: ['./delete-esperienza-modal.component.css']
})
export class DeleteEsperienzaModalComponent implements OnInit {
  
  @Input() nomeEsperienza;
  @Input() nominativoStudente;
  @Input() titoloEsperienza;
  @Output() onDelete = new EventEmitter();

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
  }

  delete() {
    this.activeModal.close();
    this.onDelete.emit();
  }

}
