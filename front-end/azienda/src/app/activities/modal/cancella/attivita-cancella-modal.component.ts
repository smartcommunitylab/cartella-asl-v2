import { Component, Output, Input, EventEmitter } from '@angular/core';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service'
import { GrowlerService, GrowlerMessageType } from '../../../core/growler/growler.service';
import { IOffer, EsperienzaSvolta} from '../../../shared/interfaces';

@Component({
  selector: 'attivita-cancella-modal',
  templateUrl: './attivita-cancella-modal.html',
  styleUrls:['./attivita-cancella-modal.scss']
})
export class AttivitaCancellaModal {
  closeResult: string;

  @Input() esp: EsperienzaSvolta;
  @Output() onDelete = new EventEmitter<string>();

  constructor(public activeModal: NgbActiveModal, private dataService: DataService, private growler: GrowlerService) { }
  delete() {
    this.deleteDocument(this.esp.id).then(deleted => {
      if (deleted) {
        this.activeModal.close("deleted");
        this.growler.growl('Successo', GrowlerMessageType.Success);
      } else {
        this.growler.growl('Errore.', GrowlerMessageType.Warning);
      }
    });
  }

  deleteDocument(id): Promise<any> {
     
    return new Promise<any>((resolve, reject) => {
      this.dataService.deleteDocument(id).then(response => {
        resolve(response)
      }).catch((error: any): any => {
        reject()
      })
    })
  }
}

