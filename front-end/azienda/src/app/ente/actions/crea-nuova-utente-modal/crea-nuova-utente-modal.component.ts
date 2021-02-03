import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service';
import { ValidationService } from '../../../core/services/validation.service';

@Component({
  selector: 'crea-nuova-utente-modal',
  templateUrl: './crea-nuova-utente-modal.component.html',
  styleUrls: ['./crea-nuova-utente-modal.component.scss']
})
export class CreaNuovaUtenteModalComponent implements OnInit {

  forceErrorDisplay: boolean;
  forceErrorDisplayNome: boolean = false;
  forceErrorDisplayCognome: boolean = false;
  forceErrorDisplayCF: boolean = false;
  forceErrorDisplayMail: boolean = false;
  // forceErrorDisplayTelefono: boolean = false;

  profile = { 'name': null, 'surname': null, 'cf': null, 'email': null }; //'phone': null, 'idTipoUtente': 'Tipo'
  // tipologia: any = 'Tipologia';
  // tipologie?: any;
  // tipoUtente = [{ "id": 1, "value": "Associazione" }, { "id": 5, "value": "Cooperativa" }, { "id": 10, "value": "Impresa" }, { "id": 15, "value": "Libero professionista" }, { "id": 20, "value": "Pubblica amministrazione" }, { "id": 25, "value": "Ente privato/Fondazione" }];
  @Output() newUtenteListener = new EventEmitter<Object>();

  constructor(public activeModal: NgbActiveModal, private dataService: DataService, private validationService: ValidationService) { }

  ngOnInit() { }

  create() {
    if (this.allValidated()) {
      this.newUtenteListener.emit(this.profile);
      this.activeModal.dismiss('create')
    } else {
      this.forceErrorDisplay = true;
      if (!this.validationService.isValidEmail(this.profile.email)) {
        this.forceErrorDisplayMail = true;
      } else {
        this.forceErrorDisplayMail = false;
      }
    }
  }

  allValidated() {
    return (
      (this.profile.name && this.profile.name != '' && this.profile.name.trim().length > 0)
      && (this.profile.surname && this.profile.surname != '' && this.profile.surname.trim().length > 0)
      && (this.profile.cf && this.profile.cf != '' && this.profile.cf.trim().length > 0)
      && (this.profile.email && this.profile.email != '' && this.profile.email.trim().length > 0 && this.validationService.isValidEmail(this.profile.email))
      // && (this.profile.phone && this.profile.phone != '' && this.profile.phone.trim().length > 0)
      // && (this.profile.idTipoUtente && this.profile.idTipoUtente != '' && this.profile.idTipoUtente != 'Tipo')
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
      // (event.target.value.trim().length == 0) ? this.forceErrorDisplayTelefono = true : this.forceErrorDisplayTelefono = false;
    } else if (type == 'trim') {
      event.target.value = event.target.value.trim();
    }
  }

}
