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


  profile = { 'name': null, 'surname': null, 'cf': null, 'email': null }; //'phone': null, 'idTipoUtente': 'Tipo'
  // tipologia: any = 'Tipologia';
  // tipologie?: any;
  // tipoUtente = [{ "id": 1, "value": "Associazione" }, { "id": 5, "value": "Cooperativa" }, { "id": 10, "value": "Impresa" }, { "id": 15, "value": "Libero professionista" }, { "id": 20, "value": "Pubblica amministrazione" }, { "id": 25, "value": "Ente privato/Fondazione" }];
  @Output() newUtenteListener = new EventEmitter<Object>();

  constructor(public activeModal: NgbActiveModal, private dataService: DataService, private validationService: ValidationService) { }

  ngOnInit() { }

  create() {
  
      this.newUtenteListener.emit(this.profile);
      this.activeModal.dismiss('create')
  
  }



}
