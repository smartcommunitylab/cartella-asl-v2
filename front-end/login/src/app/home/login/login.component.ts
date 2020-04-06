import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { LoginService } from '../../core/services/login.service';
import { DataService } from '../../core/services/data.service';

@Component({
    selector: 'cm-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
    app: string = null;
    message: string = null;
    image: string = null;
    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private loginService: LoginService,
        private dataService: DataService
    ) { }

    ngOnInit() {
        this.route.params.subscribe(params => {
            this.app = params['app'];
            //set text
            this.message = this.loginService.getLoginMessage(this.app);
            //set image
            this.image = this.loginService.getImage(this.app);
            //set function login

        });
    }

    login() {
        this.dataService.login('a','a').subscribe(returnValues => {

        })
    }

}
