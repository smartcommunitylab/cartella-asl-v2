import { Component, OnInit } from '@angular/core';
import { DataService } from '../core/services/data.service'
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../core/auth/auth.service';
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
    email;
    cf;
    role;
    nomeReferente;
    cognomeReferente;

    constructor(
        private dataService: DataService,
        private authService: AuthService,
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
                this.email = res.email;
                this.nomeReferente = res.nomeReferente;
                this.cognomeReferente = res.cognomeReferente;
                this.cf = res.cf;
                this.role = res.role;
                sessionStorage.aziendaId = res.aziendaId;
                if (this.role == 'LEGALE_RAPPRESENTANTE_AZIENDA' && res.stato == 'inviato') { // CASE 1. MANDATA DA SCUOLA A PADRONE DI AZIENDA
                    this.dataService.confermaRichiestaRegistrazione(token).subscribe(res => { }, err => {
                        this.linkExpired = true;
                    });
                }
            }, err => {
                // logic for expired token.
                this.linkExpired = true;
            });
        })
    }

    login() {
        // alert('login');
        // this.authService.checkLoginStatus().then(valid => {
        //     if (!valid) {
        //         console.log('come here for not valid');
        //         this.authService.redirectAuth();
        //     } else {
        //         // logged in.
        //         this.router.navigateByUrl('/registrazione')
        //     }

        // });

        this.authService.init().then(() => {
            let isAuthenticated = this.authService.isLoggedIn();
            if (!isAuthenticated) {
                this.authService.startAuthentication();
            } else {
                 this.router.navigateByUrl('/registrazione')
            }

        })

        
    }

}