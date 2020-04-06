import { Component, Output, Input, EventEmitter } from '@angular/core';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service'
import { PianoAlternanza } from '../../../shared/classes/PianoAlternanza.class';

@Component({
  selector: 'pianifica-cancella-modal',
  templateUrl: './pianifica-cancella-modal.html',
  styleUrls:['./pianifica-cancella-modal.scss']
})
export class PianificaCancellaModal {
  closeResult: string;

  @Input() piano: PianoAlternanza;
  @Output() onDelete = new EventEmitter<string>();

  constructor(public activeModal: NgbActiveModal, private dataService: DataService) { }
  delete() {
    this.dataService.deletePiano(this.piano.id).subscribe(
      (response: boolean) => {
        if (response) {
          this.activeModal.close();
          this.onDelete.emit('deleted');
        } else {
          console.log('not deleted');
          alert("Errore nella cancellazione");
        }

      },
      (err: any) => console.log(err),
      () => console.log('delete piano')
    );
  }
}

