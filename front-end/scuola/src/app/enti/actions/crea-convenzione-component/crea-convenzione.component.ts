import { Component, OnInit, ViewChild } from '@angular/core';
import { DataService } from '../../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';
import * as moment from 'moment';
import { DatePickerComponent } from 'ng2-date-picker';

@Component({
  selector: 'cm-crea-convenzione',
  templateUrl: './crea-convenzione.component.html',
  styleUrls: ['./crea-convenzione.component.scss']
})
export class CreaConvenzioneComponent implements OnInit {

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

  ente;
  nome;
  titolo: string;
  fieldsError: string;
  date: {
    dataInizio: moment.Moment,
    dataFine: moment.Moment,
    maxFine: moment.Moment
  } = {
    dataInizio: null,
    dataFine: null,
    maxFine: null
  };
  pageSize = 20;
  forceErrorDisplay: boolean;
  forceTitoloErrorDisplay: boolean = false;
  showContent:boolean = false;
  menuContent = "In questa pagina puoi immettere i dati relativi alla convenzione tra l’ente e il tuo istituto. La convenzione è necessaria per permettere ad un ente di accedere ai dati degli studenti di questo istituto che svolgono attività di tirocinio presso l’ente stesso. In assenza di una convenzione valida, l’ente non potrà visualizzare nessun dato degli studenti di questo istituto, né creare offerte indirizzate a questo istituto." 
  datePickerConfig = {
    locale: 'it',
    firstDayOfWeek: 'mo',
    min: moment().subtract(60, 'months'),
    max: moment().add(36,'months')
  };

  breadcrumbItems = [
    {
      title: "Dettaglio ente",
      location: "../../",
    },
    {
      title: "Aggiungi convenzione"
    }
  ];

  constructor(private router: Router, private route: ActivatedRoute, private dataService: DataService) { }

  ngOnInit() {
    this.date.dataInizio = moment();
    this.date.dataFine = moment();
    this.route.params.subscribe(params => {
      let id = params['id'];
      this.dataService.getAzienda(id).subscribe((res) => {
        this.ente = res;
      },
        (err: any) => console.log(err),
        () => console.log('getAzienda'));
    });

  }

  cancel() {
    this.router.navigate(['../../'], { relativeTo: this.route });
  }

  save() {
    if (this.allValidated()) {
      let convenzione = {
        nome: this.nome,
        enteId: this.ente.id,
        nomeEnte: this.ente.nome,
        dataInizio: moment(this.date.dataInizio, 'YYYY-MM-DD').valueOf(),
        dataFine: moment(this.date.dataFine, 'YYYY-MM-DD').valueOf(),
        istitutoId: this.dataService.istitutoId,
        nomeIstituto: this.dataService.getIstitutoName()
      }
      this.dataService.addConvenzione(convenzione).subscribe((res) => {
        this.router.navigate(['../../'], { relativeTo: this.route });
      },
        (err: any) => console.log(err),
        () => console.log('get piani attivi'));
   } else {
      this.forceErrorDisplay = true;
      if (this.nome && this.nome != '' && this.nome.trim().length > 0) {
        this.forceTitoloErrorDisplay = false;
      } else {
        this.forceTitoloErrorDisplay = true;
      }
    }
  }

  allValidated() {
    return ( (this.nome && this.nome != '' && this.nome.trim().length > 0)
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
