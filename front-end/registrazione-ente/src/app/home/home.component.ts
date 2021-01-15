import { Component, OnInit } from '@angular/core';
import { DataService } from '../core/services/data.service'
import { ActivatedRoute, Router } from '@angular/router';
import { AuthenticationService } from '../core/services/authentication.service';
import { environment } from '../../environments/environment';

@Component({
    selector: 'home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})

export class HomeComponent implements OnInit {
    title: string;
    linkExpired: boolean = false;
    env = environment;
    nomeIstituto;
    nomeEnte;
    emailEnte;

    constructor(
        private dataService: DataService,
        private authService: AuthenticationService,
        private route: ActivatedRoute,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.env.profileFlag = false;
        this.route.queryParamMap.subscribe(queryParams => {
            //http://<host>/#/home/account/activation?key=<TOKEN>
            let token = queryParams.get("key");
            this.dataService.getRegistrazioneByToken(token).subscribe(res => {
                this.nomeIstituto = res.nomeIstituto;
                this.nomeEnte = res.nomeEnte;
                this.emailEnte = res.email;
            }, err => {
                // logic for expired token.
                this.linkExpired = true;
            });
        })
    }

    login() {
        alert('login');
        this.authService.checkLoginStatus().then(valid => {
            if (!valid) {
                console.log('come here for not valid');
                this.authService.redirectAuth();
            } else {
                // logged in.
                this.router.navigateByUrl('/registrazione')
            }

        });
    }

}