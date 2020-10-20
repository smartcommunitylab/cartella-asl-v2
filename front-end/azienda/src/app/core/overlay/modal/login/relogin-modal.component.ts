import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'relogin-modal',
  templateUrl: './relogin-modal.html',
  styleUrls:['./relogin-modal.scss']
})
export class ReloginModal {

  text: any;

  constructor(public activeModal: NgbActiveModal) { }

  login() {
    this.activeModal.close("LOGIN");
  }

}

