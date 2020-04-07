import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { DataService } from '../core/services/data.service';

@Component({
  selector: 'cm-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  loginSelected: boolean = false;
  username: string = '';
  password: string = '';
  type: string = '';
  errMsg: string = '';
  actualOption;
  options = [{ "id": 1, "name": "Scuola", "prefix": "asl-scuola-v2", "disabled": false }, { "id": 2, "name": "Studente", "prefix": "asl-studente-v2", "disabled": false }, { "id": 3, "name": "Ente Ospitante", "prefix": "asl-azienda-v2", "disabled": true }];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService
  ) { }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      if (params['errMsg']) {
        this.errMsg = 'Attenzione:' + params['errMsg'];
      }
    });
  }

  login() {
    if (this.loginSelected) {
      this.type = this.actualOption.prefix;
      this.entra();
    }    
  }

  entra() {
    var getUrl = window.location;
    var baseUrl = getUrl.protocol + "//" + getUrl.host + "/"; //+ getUrl.pathname.split('/')[1]
    window.location.href = baseUrl + '/' + this.type + '/';
  }

  onChange(option) {
    this.actualOption = option;
    this.loginSelected = true;
  }
}
