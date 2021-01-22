import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService } from '../core/services/data.service';
import { environment } from '../../environments/environment';
import Stepper from 'bs-stepper'
import { AuthenticationService } from '../core/services/authentication.service';
import { ModificaAccountModalComponent } from './actions/modifica-account-modal/modifica-account-modal.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ModificaEnteModalComponent } from './actions/modifica-ente-modal/modifica-ente-modal.component';

@Component({
  selector: 'cm-registrazione-container',
  templateUrl: './registrazione.component.html',
  styleUrls: ['./registrazione.component.scss']
})
export class Registrazioneomponent implements OnInit {

  title: string;
  env = environment;
  profile;
  aziende;
  actualAzienda;
  profileDropdownVisible;
  codiceAteco: string = '';
  descAteco: string = '';
  letto: boolean = false;
  attachedAteco = [];

  constructor(
    private dataService: DataService,
    private authService: AuthenticationService,
    private route: ActivatedRoute,
    private router: Router,
    private modalService: NgbModal
  ) {

    // let params = this.route.snapshot.fragment;

    // if (params) {
    //   const data = JSON.parse(
    //     '{"' +
    //     decodeURI(params)
    //       .replace(/"/g, '\\"')
    //       .replace(/&/g, '","')
    //       .replace(/=/g, '":"') +
    //     '"}'
    //   );

    //   console.log(data); // { json: "with your properties"}

    //   if (data.access_token) {
    //     sessionStorage.access_token = data.access_token;
    //     sessionStorage.access_token_expires_in = new Date().getTime() + parseInt(data.expires_in, 10) * 1000;
    //   }

    // }

  }

  private stepper: Stepper;

  next() {
    this.stepper.next();
  }

  move(id) {
    if (id == 1)
      this.stepper.to(id);
    else if (this.letto) {
      this.stepper.to(id);
    }
  }

  onSubmit() {
    return false;
  }

  ngOnInit() {
    this.dataService.getProfile().subscribe(profile => {
      if (profile && profile.aziende) {
        this.profile = profile;
        // var aziendaId = Object.keys(profile.aziende)[0];
        var aziendaId = sessionStorage.getItem('aziendaId');
        this.dataService.setAziendaId(aziendaId);
        this.dataService.setAziendaName(profile.aziende[aziendaId].nome);
        this.actualAzienda = profile.aziende[aziendaId];
        this.updateAtecoCodiceList();
        this.stepper = new Stepper(document.querySelector('#stepper1'), {
          linear: true,
          animation: true
        })
      }
    }, err => {
    });
  }

  logout() {
    this.authService.logout();
  }

  updateAtecoCodiceList() {
    this.attachedAteco = [];
    if (this.actualAzienda.atecoCode && this.actualAzienda.atecoDesc) {
      for (var i = 0; i < this.actualAzienda.atecoCode.length; i++) {
        let atecoEntry = {codice: this.actualAzienda.atecoCode[i], descrizione: this.actualAzienda.atecoDesc[i]};
        this.attachedAteco.push(atecoEntry);
      }
    }
  }

  modificaDatiEnte() {
    const modalRef = this.modalService.open(ModificaEnteModalComponent, { windowClass: "creaAttivitaModalClass" });
    modalRef.componentInstance.ente = this.actualAzienda;
    modalRef.componentInstance.updatedEnteListener.subscribe((enteUpdated) => {
      this.dataService.addAzienda(enteUpdated).subscribe((res) => {
        this.dataService.getProfile().subscribe(profile => {
          if (profile && profile.aziende) {
            this.profile = profile;
            var aziendaId = this.dataService.aziendaId;
            this.dataService.setAziendaName(profile.aziende[aziendaId].nome);
            this.actualAzienda = profile.aziende[aziendaId];
            this.updateAtecoCodiceList();
            this.stepper.to(2);
          }
        }, err => {
        });
      },
        (err: any) => console.log(err),
        () => console.log('updateAzienda'));
    });
  }

  modificaDatiAccount() {
    const modalRef = this.modalService.open(ModificaAccountModalComponent, { windowClass: "creaAttivitaModalClass" });
    modalRef.componentInstance.profile = this.profile;
    modalRef.componentInstance.newOffertaListener.subscribe((profileUpdated) => {
      profileUpdated.enteId = this .actualAzienda.id;
      profileUpdated.ownerId = this.profile.id;
      this.dataService.aggiornaDatiOwnerAzienda(profileUpdated).subscribe((res) => {
        this.profile = res;
      },
        (err: any) => console.log(err),
        () => console.log('aggiornaDatiOwnerAzienda'));

    });

  }


}
