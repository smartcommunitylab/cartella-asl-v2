import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'document-upload-modal',
  templateUrl: './document-upload-modal.component.html',
  styleUrls: ['./document-upload-modal.component.scss']
})
export class DocumentUploadModalComponent implements OnInit {
  optionSelected: boolean = false;
  fileSelected: boolean = false;
  selectedFileName;
  saveFileObj = { type: null, file: null };
  optionTypes = {
    "1": "valutazione_studente",
    "2": "doc_generico",
  }
  @Input() attivitaIndividuale: boolean;
  @Input() tirocinioCurriculare: boolean;
  @Output() newDocumentListener = new EventEmitter<Object>();

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() { }

  create(option) {
    this.optionSelected = true;
    this.saveFileObj.type = this.optionTypes[option];
    var x = document.getElementById(option);
    if (x)
      x.classList.add('active');
    for (let i = 1; i <= 2; i++) {
      if (i !== option) {
        var x = document.getElementById(i + '');
        if (x)
          x.classList.remove('active');
      }
    }
  }

  uploadDocument(fileInput) {
    if (fileInput.target.files && fileInput.target.files[0]) {
      this.fileSelected = true;
      this.selectedFileName = fileInput.target.files[0].name;
      this.saveFileObj.file = fileInput.target.files[0];
    }
  }

  carica() {
    this.newDocumentListener.emit(this.saveFileObj);
    this.activeModal.dismiss(this.saveFileObj);;
  }

  styleModalBody() {
    var style = {
      'left': '0px',
    };
    if (!this.attivitaIndividuale)
      style['left'] = '120px';

    return style;
  }

  styleModalDivButton() {
    var style = {
      'position': 'relative',
      'left': '-90px'
    };
    if (!this.attivitaIndividuale) {
      return style;
    } else
      return '';
  }

}