import { Component, OnInit, ViewChild } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DataService } from '../../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';
import * as moment from 'moment';
import { DatePickerComponent } from 'ng2-date-picker';
import { DocumentoCancellaModal } from '../documento-cancella-modal/documento-cancella-modal.component';
import { ConvenzioneCancellaModal } from '../cancella-convenzione-modal/conv-cancella-modal.component';

@Component({
  selector: 'cm-ente-modifica-convenzione',
  templateUrl: './ente-modifica-convenzione.component.html',
  styleUrls: ['./ente-modifica-convenzione.component.scss']
})
export class EnteConvenzioneModificaComponent implements OnInit {

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService,
    private modalService: NgbModal) { }

  ente;
  convId;  
  convenzione;
  forceErrorDisplay: boolean = false;
  forceTitoloErrorDisplay: boolean = false;
  forceAddressDisplay: boolean = false;;
  forceErrorDisplayMail: boolean = false;
  forceFileErrorDisplay: boolean = false;
  showContent: boolean = false;
  menuContent = "In questa pagina trovi tutte le informazioni relative all’attività che stai visualizzando.";
 
  breadcrumbItems = [
    {
      title: "Dettaglio ente",
      location: "../../../",
    },
    {
      title: "Modifica convenzione"
    }
  ];

  @ViewChild('calendarStart') calendarStart: DatePickerComponent;
  @ViewChild('calendarEnd') calendarEnd: DatePickerComponent;
  
  openStart() {
    this.calendarStart.api.open();
  }

  closeStart() {
    this.calendarStart.api.close();
  }

  openEnd() {
    this.calendarEnd.api.open();
  }

  closeEnd() {
    this.calendarEnd.api.close();
  }

  date: {
    dataInizio: moment.Moment,
    dataFine: moment.Moment,
    maxFine: moment.Moment
  } = {
    dataInizio: null,
    dataFine: null,
    maxFine: null
  };
 
  datePickerConfig = {
    locale: 'it',
    firstDayOfWeek: 'mo',
    min: moment().subtract(60, 'months'),
    max: moment().add(36,'months')
  };


  ngOnInit() {

    this.route.params.subscribe(params => {
      let id = params['id'];
      this.convId = params['idConv'];
      this.dataService.getAzienda(id).subscribe((res) => {
        this.ente = res;
        this.dataService.getConvenzioneDettaglio(this.convId).subscribe((res) => {
          this.convenzione = res;
          this.date.dataInizio = moment(this.convenzione.dataInizio).startOf('day');
          this.date.dataFine = moment(this.convenzione.dataFine).startOf('day');
        })
       },
        (err: any) => console.log(err),
        () => console.log('getAzienda'));
    });1

  }

  cancel() {
    this.router.navigate(['../../../'], { relativeTo: this.route });
  }

  save() {

    if (this.allValidated()) {
      this.convenzione.dataInizio = moment(this.date.dataInizio, 'YYYY-MM-DD').valueOf();
      this.convenzione.dataFine = moment(this.date.dataFine, 'YYYY-MM-DD').valueOf();
      this.dataService.addConvenzione(this.convenzione).subscribe((res) => {
        this.router.navigate(['../../../'], { relativeTo: this.route });
      },
        (err: any) => console.log(err),
        () => console.log('modifica convenzione'));

    } else {
      this.forceErrorDisplay = true;
      if (this.convenzione.nome && this.convenzione.nome != '' && this.convenzione.nome.trim().length > 0) {
        this.forceTitoloErrorDisplay = false;
      } else {
        this.forceTitoloErrorDisplay = true;
      }     
    }
  }

  allValidated() {
    return ( (this.convenzione.nome && this.convenzione.nome != '' && this.convenzione.nome.trim().length > 0)
    && (this.date.dataInizio && this.date.dataFine && (this.date.dataInizio <= this.date.dataFine))
    );
  }
  
  menuContentShow() {
    this.showContent = !this.showContent;
  }

  trimValue(event, type) {
    if (type == 'titolo') {
      (event.target.value.trim().length == 0) ? this.forceTitoloErrorDisplay = true : this.forceTitoloErrorDisplay = false;
    }
  }

  delete() {
    if (this.convenzione.stato == 'attiva') {
      const modalRef = this.modalService.open(ConvenzioneCancellaModal, { windowClass: "abilitaEnteModalClass" });
      modalRef.componentInstance.ente = this.ente;
      modalRef.componentInstance.convenzione = this.convenzione;
      modalRef.componentInstance.onDelete.subscribe((res) => {
        if (res == 'deleted') {
          this.deleteConvenzione();
        }        
      });
    } else {
      this.deleteConvenzione();
    }
  }

  deleteConvenzione() {
    this.dataService.annullaConvenzione(this.convenzione.id).subscribe((res) => {
      this.router.navigate(['../../../'], { relativeTo: this.route });
    })
  }

  saveFileObj = { type: null, file: null };
  documenti = [];

  uploadDocument(fileInput) {
    if (fileInput.target.files && fileInput.target.files[0]) {
      if(fileInput.target.files[0].size > 10240000) {
        this.forceFileErrorDisplay = true;
      } else {
        this.forceFileErrorDisplay = false;
        this.saveFileObj.file = fileInput.target.files[0];
        this.saveFileObj.type = 'convenzione';
  
        this.dataService.uploadDocumentConvenzione(this.saveFileObj, this.convenzione.uuid + '').subscribe((doc) => {
          this.dataService.getConvenzioneDettaglio(this.convId).subscribe((res) => {
            this.convenzione = res;
            this.date.dataInizio = moment(this.convenzione.dataInizio).startOf('day');
            this.date.dataFine = moment(this.convenzione.dataFine).startOf('day');
          })
        });  
      }
    }
  }

  downloadDoc(doc) {
    this.dataService.downloadDocumentConvenzioneBlob(doc).subscribe((url) => {
      const downloadLink = document.createElement("a");
      downloadLink.href = url;
      downloadLink.download = doc.nomeFile;
      document.body.appendChild(downloadLink);
      downloadLink.click();
      document.body.removeChild(downloadLink);
    });
  }

  deleteConvenzioneDocument(conv) {
      const modalRef = this.modalService.open(DocumentoCancellaModal);
      modalRef.componentInstance.documento = conv;
      modalRef.result.then((result) => {
        if (result == 'deleted') {
          this.dataService.getConvenzioneDettaglio(this.convId).subscribe((res) => {
            this.convenzione = res;
            this.date.dataInizio = moment(this.convenzione.dataInizio).startOf('day');
            this.date.dataFine = moment(this.convenzione.dataFine).startOf('day');
          })        }
      });
  }


}
