import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService } from '../core/services/data.service';
import { environment } from '../../environments/environment';
import Stepper from 'bs-stepper'
import { AuthenticationService } from '../core/services/authentication.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

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
        var ids = [];
        for (var k in profile.aziende) {
          ids.push(k);
        }
        this.dataService.getListaAziendeByIds(ids).subscribe((aziende: Array<any>) => {
          if (aziende) {
            this.aziende = aziende;
            this.dataService.setAziendaId(this.aziende[0].id);
            this.dataService.setAziendaName(this.aziende[0].nome);
            this.actualAzienda = this.aziende[0];
           }
        }
          , err => {
            console.log('error, no azienda')
          });
        this.dataService.setAziendaId(ids[0]);
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




}
