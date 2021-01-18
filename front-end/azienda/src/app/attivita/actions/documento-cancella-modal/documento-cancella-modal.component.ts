import { Component, Output, Input, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service'
import { GrowlerService, GrowlerMessageType } from '../../../core/growler/growler.service';

@Component({
  selector: 'documento-cancella-modal',
  templateUrl: './documento-cancella-modal.html',
  styleUrls: ['./documento-cancella-modal.scss']
})
export class DocumentoCancellaModal {
  closeResult: string;
  @Input() documento: any;
  @Output() onDelete = new EventEmitter<string>();

  constructor(public activeModal: NgbActiveModal, private dataService: DataService, private growler: GrowlerService) { }

  delete() {
    this.deleteDocument(this.documento.uuid).then(deleted => {
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

