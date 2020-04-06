import { Component, OnInit, Output, Input, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'cm-delete-role-modal',
  templateUrl: './delete-role-modal.component.html',
  styleUrls: ['./delete-role-modal.component.css']
})
export class DeleteRoleModalComponent implements OnInit {

  @Input() roleName;
  @Output() onDelete = new EventEmitter();

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
  }

  delete() {
    this.activeModal.close();
    this.onDelete.emit();
  }
}
