import { Component, OnInit, Output, Input, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'cm-delete-role-single-modal',
  templateUrl: './delete-role-single-modal.component.html',
  styleUrls: ['./delete-role-single-modal.component.css']
})
export class DeleteRoleSingleModalComponent implements OnInit {
  
  @Input() roleAssociatedName;
  @Output() onDelete = new EventEmitter();

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
  }

  delete() {
    this.activeModal.close();
    this.onDelete.emit();
  }
}
