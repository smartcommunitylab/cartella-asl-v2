import { Component, OnInit } from '@angular/core';
import { DataService } from '../../../core/services/data.service';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'cm-ente-modifica-responsabile',
  templateUrl: './ente-modifica-responsabile.component.html',
  styleUrls: ['./ente-modifica-responsabile.component.scss']
})
export class EnteResponsabileModificaComponent implements OnInit {

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService) { }

  ente;
  enteResponsabile;
  name: string = '';
  surname: string = '';
  cf: string = '';
  email: string = '';
  telefono: string = '';
  forceErrorDisplay: boolean = false;
  
  breadcrumbItems = [
    {
      title: "Dettaglio ente",
      location: "../../../",
    },
    {
      title: "Aggiungi responsabile"
    }
  ];

  ngOnInit() {
    this.forceErrorDisplay = false;
    this.route.params.subscribe(params => {
      let id = params['id'];
      this.dataService.getAzienda(id).subscribe((res) => {
        this.ente = res;
        this.dataService.getEnteResponsabile(this.ente).subscribe((res) => {
          this.enteResponsabile = res;
          if (this.enteResponsabile) {
            this.breadcrumbItems[1].title = "Modifica responsabile";
            this.name = this.enteResponsabile.nomeReferente;
            this.surname = this.enteResponsabile.cognomeReferente;
            this.cf = this.enteResponsabile.cf;
            this.email = this.enteResponsabile.email;
            this.telefono = this.enteResponsabile.telefonoReferente;
          }
        },
          (err: any) => console.log(err),
          () => console.log('getEnteRegistrazione'))
      },
        (err: any) => console.log(err),
        () => console.log('getEnte'));
    });

  }

  cancel() {
    this.router.navigate(['../../'], { relativeTo: this.route });
  }

  save() {
    if (this.allValidated()) {
    }
  }

  allValidated() {
    return ((this.name && this.name != '' && this.name.trim().length > 0)
      && (this.surname && this.surname != '' && this.surname.trim().length > 0)
      && (this.cf && this.cf != '' && this.cf.trim().length > 0)
      && (this.email && this.email != '' && this.email.trim().length > 0)
    );
  }

}
