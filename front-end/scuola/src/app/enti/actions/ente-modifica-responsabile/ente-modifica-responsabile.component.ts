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
  nome: string = '';
  cognome: string = '';
  cf: string = '';
  email: string = '';
  telefono: string = '';
  forceErrorDisplay: boolean = false;
  forceNameErrorDisplay: boolean = false;
  forceCognomeErrorDisplay: boolean = false;
  forceCFErrorDisplay: boolean = false;
  forceEmailErrorDisplay: boolean = false;
  
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
            this.breadcrumbItems[1].title = "Modifica responsabile"
            this.nome = this.enteResponsabile.nomeReferente;
            this.cognome = this.enteResponsabile.cognomeReferente;
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
      this.dataService.creaRichiestaRegistrazione(this.ente, this.cf, this.email, this.nome, this.cognome, this.telefono).subscribe((res) => {
        this.router.navigate(['../../'], { relativeTo: this.route });
      },
        (err: any) => console.log(err),
        () => console.log('modifica enteResponsabile'));

    }
  }

  allValidated() {
    return ((this.nome && this.nome != '' && this.nome.trim().length > 0)
      && (this.cognome && this.cognome != '' && this.cognome.trim().length > 0)
      && (this.cf && this.cf != '' && this.cf.trim().length > 0)
      && (this.email && this.email != '' && this.email.trim().length > 0)
    );
  }

  trimValue(event, type) {
    if (type == 'nome') {
      (event.target.value.trim().length == 0) ? this.forceNameErrorDisplay = true : this.forceNameErrorDisplay = false;
    } else if (type == 'cognome') {
      (event.target.value.trim().length == 0) ? this.forceCognomeErrorDisplay = true : this.forceCognomeErrorDisplay = false;
    } else if (type == 'trim') {
      event.target.value = event.target.value.trim();
    }
  }


}
