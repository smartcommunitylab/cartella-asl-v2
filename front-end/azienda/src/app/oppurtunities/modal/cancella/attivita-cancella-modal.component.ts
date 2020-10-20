import { Component, Output, Input, EventEmitter } from '@angular/core';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service'
import { GrowlerService, GrowlerMessageType } from '../../../core/growler/growler.service';
import { IOffer} from '../../../shared/interfaces';

@Component({
  selector: 'attivita-cancella-modal',
  templateUrl: './attivita-cancella-modal.html',
  styleUrls:['./attivita-cancella-modal.scss']
})
export class AttivitaCancellaModal {
  closeResult: string;

  @Input() offer: IOffer;
  @Output() onDelete = new EventEmitter<string>();

  constructor(public activeModal: NgbActiveModal, private dataService: DataService, private growler: GrowlerService) { }
  delete() {
    this.dataService.deleteOppurtunita(this.offer.id).subscribe(updated => {
      this.growler.growl('Successo', GrowlerMessageType.Success);
      this.activeModal.close("deleted");
    }, (err: any) => {
      this.activeModal.close("error");
    });
  }
}

