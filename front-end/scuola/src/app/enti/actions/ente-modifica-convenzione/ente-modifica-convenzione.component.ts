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

}
