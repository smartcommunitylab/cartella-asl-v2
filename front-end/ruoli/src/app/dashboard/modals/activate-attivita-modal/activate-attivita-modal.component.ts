import { Component, OnInit, Output, Input, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'cm-activate-attivita-modal',
  templateUrl: './activate-attivita-modal.component.html',
  styleUrls: ['./activate-attivita-modal.component.css']
})
export class ActivateAttivitaModalComponent implements OnInit {
  
  @Input() titolo;
  @Output() onActivate = new EventEmitter();

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
  }

  activate() {
    this.activeModal.close();
    this.onActivate.emit();
  }

}
