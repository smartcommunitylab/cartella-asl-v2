import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { DataService } from '../../../../core/services/data.service';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal/modal-ref';

@Component({
  selector: 'cm-note-azienda-edit-modal',
  templateUrl: './note-azienda-edit-modal.component.html',
  styleUrls: ['./note-azienda-edit-modal.component.css']
})
export class NoteAziendaEditModalComponent implements OnInit {

  @Input() note:string;
  @Input() titolo:string;
  @Input() esperienzaId;

  @Output() noteSaved = new EventEmitter<string>();

  constructor(public activeModal: NgbActiveModal, private dataService: DataService) { }

  ngOnInit() {
    console.log(this.note);
  }

  save() {  
    this.dataService.saveNoteAzienda(this.esperienzaId, this.note).subscribe(res => {
      this.noteSaved.emit(this.note);
      this.activeModal.close();
    },
      (err: any) => console.log(err),
      () => console.log('save note azienda')
    );
  }

}
