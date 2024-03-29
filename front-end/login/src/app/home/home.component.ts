import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { DataService } from '../core/services/data.service';
import { environment } from '../../environments/environment';

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
  env = environment;

  options = [
    { "id": 1, "name": "Scuola", "prefix": this.env.prefixScuola, "disabled": false }, 
    { "id": 2, "name": "Studente", "prefix": this.env.prefixStudente, "disabled": false }, 
    { "id": 3, "name": "Ente Ospitante", "prefix": this.env.prefixEnte, "disabled": false }
  ];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService
  ) { }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      if (params['errMsg']) {
        let errMsg = params['errMsg'].replace(/(^'|'$)/g, '');
        errMsg = errMsg.replace(/(^"|"$)/g, '');
        this.errMsg = 'Attenzione: ' + errMsg;
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
    window.location.href = baseUrl + this.type + '/';
  }

  onChange(option) {
    this.actualOption = option;
    this.loginSelected = true;
  }
}
