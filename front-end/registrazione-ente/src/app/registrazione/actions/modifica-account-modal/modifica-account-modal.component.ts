import { Component, OnInit, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service';

@Component({
  selector: 'modifica-account-modal',
  templateUrl: './modifica-account-modal.component.html',
  styleUrls: ['./modifica-account-modal.component.scss']
})
export class ModificaAccountModalComponent implements OnInit {

  forceErrorDisplay: boolean;
  forceErrorDisplayNome: boolean = false;
  forceErrorDisplayCognome: boolean = false;
  forceErrorDisplayCF: boolean = false;
  forceErrorDisplayMail: boolean = false;
  // forceErrorDisplayTelefono: boolean = false;

  @Input() profile?: any;
  @Output() newOffertaListener = new EventEmitter<Object>();
  
  constructor(public activeModal: NgbActiveModal, private dataService: DataService) { }

  ngOnInit() { }

  save() {
    if (this.allValidated()) {
      this.newOffertaListener.emit(this.profile);
      this.activeModal.dismiss('create')
    } else {
      this.forceErrorDisplay = true;
    }
  }

  allValidated() {
      return (
        (this.profile.name && this.profile.name != '' && this.profile.name.trim().length > 0)
        && (this.profile.surname && this.profile.surname != '' && this.profile.surname.trim().length > 0)
        && (this.profile.cf && this.profile.cf != '' && this.profile.cf.trim().length > 0)
        && (this.profile.email && this.profile.email != '' && this.profile.email.trim().length > 0)
        // && (this.profile.phone && this.profile.phone != '' && this.profile.phone.trim().length > 0)
      );
  }

  trimValue(event, type) {
    if (type == 'nome') {
      (event.target.value.trim().length == 0) ? this.forceErrorDisplayNome = true : this.forceErrorDisplayNome = false;
    } else if (type == 'cognome') {
      (event.target.value.trim().length == 0) ? this.forceErrorDisplayCognome = true : this.forceErrorDisplayCognome = false;
    } else if (type == 'cf') {
      (event.target.value.trim().length == 0) ? this.forceErrorDisplayCF = true : this.forceErrorDisplayCF = false;
    } else if (type == 'mail') {
      (event.target.value.trim().length == 0) ? this.forceErrorDisplayMail = true : this.forceErrorDisplayMail = false;
    // } else if (type == 'telefono') {
    //   (event.target.value.trim().length == 0) ? this.forceErrorDisplayTelefono = true : this.forceErrorDisplayTelefono = false;
    } else if(type == 'trim'){
      event.target.value = event.target.value.trim(); 
    } 
  }

}
