import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'document-upload-modal',
  templateUrl: './document-upload-modal.component.html',
  styleUrls: ['./document-upload-modal.component.scss']
})
export class DocumentUploadModalComponent implements OnInit {
  forceFileErrorDisplay: boolean = false;
  optionSelected: boolean = false;
  optionType;
  fileSelected: boolean = false;
  selectedFileName;
  saveFileObj = { type: null, file: null };
  optionTypes = {
    "1": "piano_formativo",
    "2": "valutazione_studente",
    "3": "valutazione_esperienza",
    "4": "doc_generico"
  }
  docTypes;
  @Input() attivitaIndividuale: boolean;
  @Input() tipologiaId;
  @Output() newDocumentListener = new EventEmitter<Object>();

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
    this.docTypes = this.getDocumentTypes();
  }

  create(option) {
    this.optionSelected = true;
    this.saveFileObj.type = this.optionTypes[option];
    var x = document.getElementById(option);
    if (x)
      x.classList.add('active');
    for (let i = 1; i <= 4; i++) {
      if (i !== option) {
        var x = document.getElementById(i + '');
        if (x)
          x.classList.remove('active');
      }
    }
  }

  uploadDocument(fileInput) {
    if (fileInput.target.files && fileInput.target.files[0]) {
      if(fileInput.target.files[0].size > 10240000) {
        this.forceFileErrorDisplay = true;
        this.fileSelected = false;
        this.selectedFileName = '';
        this.saveFileObj.file = null;  
      } else {
        this.forceFileErrorDisplay = false;
        this.fileSelected = true;
        this.selectedFileName = fileInput.target.files[0].name;
        this.saveFileObj.file = fileInput.target.files[0];  
      }
    }
  }

  carica() {
    this.newDocumentListener.emit(this.saveFileObj);
    this.activeModal.dismiss(this.saveFileObj);
  }

  getDocumentTypes() {
    switch (this.tipologiaId) {
      case 1: {
        return [
          {
            "titolo": "Piano formativo",
            "id": 1,
            "type": "piano_formativo",
            "descr": "Il piano formativo è il documento che descrive le specificità del percorso relativo al tirocinio in oggetto.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          },
          {
            "titolo": "Altro",
            "id": 4,
            "type": "doc_generico",
            "descr": "Se vuoi condividere un documento rapidamente puoi scegliere questa opzione.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          }
        ]
      }
      case 2: {
        return [
          {
            "titolo": "Piano formativo",
            "id": 1,
            "type": "piano_formativo",
            "descr": "Il piano formativo è il documento che descrive le specificità del percorso relativo al tirocinio in oggetto.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          },
          {
            "titolo": "Altro",
            "id": 4,
            "type": "doc_generico",
            "descr": "Se vuoi condividere un documento rapidamente puoi scegliere questa opzione.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          }
        ]
      }
      case 3: {
        return [
          {
            "titolo": "Piano formativo",
            "id": 1,
            "type": "piano_formativo",
            "descr": "Il piano formativo è il documento che descrive le specificità del percorso relativo al tirocinio in oggetto.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          },
          {
            "titolo": "Altro",
            "id": 4,
            "type": "doc_generico",
            "descr": "Se vuoi condividere un documento rapidamente puoi scegliere questa opzione.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          }
        ]
      }
      case 4: {
        return [
          {
            "titolo": "Piano formativo",
            "id": 1,
            "type": "piano_formativo",
            "descr": "Il piano formativo è il documento che descrive le specificità del percorso relativo al tirocinio in oggetto.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          },
          {
            "titolo": "Altro",
            "id": 4,
            "type": "doc_generico",
            "descr": "Se vuoi condividere un documento rapidamente puoi scegliere questa opzione.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          }
        ]
      }
      case 5: {
        return [
          {
            "titolo": "Piano formativo",
            "id": 1,
            "type": "piano_formativo",
            "descr": "Il piano formativo è il documento che descrive le specificità del percorso relativo al tirocinio in oggetto.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          },
          {
            "titolo": "Altro",
            "id": 4,
            "type": "doc_generico",
            "descr": "Se vuoi condividere un documento rapidamente puoi scegliere questa opzione.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          }
        ]
      }
      case 6: {
        return [
          {
            "titolo": "Piano formativo",
            "id": 1,
            "type": "piano_formativo",
            "descr": "Il piano formativo è il documento che descrive le specificità del percorso relativo al tirocinio in oggetto.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          },
          {
            "titolo": "Valutazione studente",
            "id": 2,
            "type": "valutazione_studente",
            "descr": "Il documento di valutazione compilato dall’ente sullo studente coinvolto. Se l’ente non ha un profilo attivo, è possibile per la scuola caricarlo qui.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          },
          {
            "titolo": "Valutazione esperienza",
            "id": 3,
            "type": "valutazione_esperienza",
            "descr": "Il documento di valutazione compilato dallo studente rispetto all’esperienza svolta presso l’ente. Se lo studente non ha un profilo attivo, è possibile per la scuola caricarlo qui.",
            "visibile": ['Scuola', 'Studente']
          },
          {
            "titolo": "Altro",
            "id": 4,
            "type": "doc_generico",
            "descr": "Se vuoi condividere un documento rapidamente puoi scegliere questa opzione.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          }
        ]
      }
      case 7: {
        return [
          {
            "titolo": "Piano formativo",
            "id": 1,
            "type": "piano_formativo",
            "descr": "Il piano formativo è il documento che descrive le specificità del percorso relativo al tirocinio in oggetto.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          },
          {
            "titolo": "Altro",
            "id": 4,
            "type": "doc_generico",
            "descr": "Se vuoi condividere un documento rapidamente puoi scegliere questa opzione.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          }]
      }
      case 8: {
        return [
          {
            "titolo": "Piano formativo",
            "id": 1,
            "type": "piano_formativo",
            "descr": "Il piano formativo è il documento che descrive le specificità del percorso relativo al tirocinio in oggetto.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          },
          {
            "titolo": "Valutazione studente",
            "id": 2,
            "type": "valutazione_studente",
            "descr": "Il documento di valutazione compilato dall’ente sullo studente coinvolto. Se l’ente non ha un profilo attivo, è possibile per la scuola caricarlo qui.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          },
          {
            "titolo": "Valutazione esperienza",
            "id": 3,
            "type": "valutazione_esperienza",
            "descr": "Il documento di valutazione compilato dallo studente rispetto all’esperienza svolta presso l’ente. Se lo studente non ha un profilo attivo, è possibile per la scuola caricarlo qui.",
            "visibile": ['Scuola', 'Studente']
          },
          {
            "titolo": "Altro",
            "id": 4,
            "type": "doc_generico",
            "descr": "Se vuoi condividere un documento rapidamente puoi scegliere questa opzione.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          }
        ]
      }
      case 9: {
        return [
          {
            "titolo": "Piano formativo",
            "id": 1,
            "type": "piano_formativo",
            "descr": "Il piano formativo è il documento che descrive le specificità del percorso relativo al tirocinio in oggetto.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          },
          {
            "titolo": "Valutazione studente",
            "id": 2,
            "type": "valutazione_studente",
            "descr": "Il documento di valutazione compilato dall’ente sullo studente coinvolto. Se l’ente non ha un profilo attivo, è possibile per la scuola caricarlo qui.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          },
          {
            "titolo": "Valutazione esperienza",
            "id": 3,
            "type": "valutazione_esperienza",
            "descr": "Il documento di valutazione compilato dallo studente rispetto all’esperienza svolta presso l’ente. Se lo studente non ha un profilo attivo, è possibile per la scuola caricarlo qui.",
            "visibile": ['Scuola', 'Studente']
          },
          {
            "titolo": "Altro",
            "id": 4,
            "type": "doc_generico",
            "descr": "Se vuoi condividere un documento rapidamente puoi scegliere questa opzione.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          }
        ]
      }
      case 10: {
        return [
          {
            "titolo": "Piano formativo",
            "id": 1,
            "type": "piano_formativo",
            "descr": "Il piano formativo è il documento che descrive le specificità del percorso relativo al tirocinio in oggetto.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          },
          {
            "titolo": "Valutazione studente",
            "id": 2,
            "type": "valutazione_studente",
            "descr": "Il documento di valutazione compilato dall’ente sullo studente coinvolto. Se l’ente non ha un profilo attivo, è possibile per la scuola caricarlo qui.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          },
          {
            "titolo": "Valutazione esperienza",
            "id": 3,
            "type": "valutazione_esperienza",
            "descr": "Il documento di valutazione compilato dallo studente rispetto all’esperienza svolta presso l’ente. Se lo studente non ha un profilo attivo, è possibile per la scuola caricarlo qui.",
            "visibile": ['Scuola', 'Studente']
          },
          {
            "titolo": "Altro",
            "id": 4,
            "type": "doc_generico",
            "descr": "Se vuoi condividere un documento rapidamente puoi scegliere questa opzione.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          }
        ]
      }
      case 11: {
        return [
          {
            "titolo": "Piano formativo",
            "id": 1,
            "type": "piano_formativo",
            "descr": "Il piano formativo è il documento che descrive le specificità del percorso relativo al tirocinio in oggetto.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          },
          {
            "titolo": "Valutazione studente",
            "id": 2,
            "type": "valutazione_studente",
            "descr": "Il documento di valutazione compilato dall’ente sullo studente coinvolto. Se l’ente non ha un profilo attivo, è possibile per la scuola caricarlo qui.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          },
          {
            "titolo": "Valutazione esperienza",
            "id": 3,
            "type": "valutazione_esperienza",
            "descr": "Il documento di valutazione compilato dallo studente rispetto all’esperienza svolta presso l’ente. Se lo studente non ha un profilo attivo, è possibile per la scuola caricarlo qui.",
            "visibile": ['Scuola', 'Studente']
          },
          {
            "titolo": "Altro",
            "id": 4,
            "type": "doc_generico",
            "descr": "Se vuoi condividere un documento rapidamente puoi scegliere questa opzione.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          }
        ]
      }
      case 12: {
        return [
 {
            "titolo": "Piano formativo",
            "id": 1,
            "type": "piano_formativo",
            "descr": "Il piano formativo è il documento che descrive le specificità del percorso relativo al tirocinio in oggetto.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          },
          {
            "titolo": "Valutazione studente",
            "id": 2,
            "type": "valutazione_studente",
            "descr": "Il documento di valutazione compilato dall’ente sullo studente coinvolto. Se l’ente non ha un profilo attivo, è possibile per la scuola caricarlo qui.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          },
          {
            "titolo": "Valutazione esperienza",
            "id": 3,
            "type": "valutazione_esperienza",
            "descr": "Il documento di valutazione compilato dallo studente rispetto all’esperienza svolta presso l’ente. Se lo studente non ha un profilo attivo, è possibile per la scuola caricarlo qui.",
            "visibile": ['Scuola', 'Studente']
          },
          {
            "titolo": "Altro",
            "id": 4,
            "type": "doc_generico",
            "descr": "Se vuoi condividere un documento rapidamente puoi scegliere questa opzione.",
            "visibile": ['Scuola', 'Ente', 'Studente']
          }
        ]
      }
    }
  }

}