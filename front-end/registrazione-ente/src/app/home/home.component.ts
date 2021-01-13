import { Component, OnInit } from '@angular/core';
import { DataService } from '../core/services/data.service'
import { ActivatedRoute, Router } from '@angular/router';
import { AuthenticationService } from '../core/services/authentication.service';
import { environment} from '../../environments/environment';

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
    ) {

        // this.linkExpired = true;
    }

    ngOnInit(): void {
        this.env.profileFlag = false;
        this.route.queryParamMap.subscribe(queryParams => {
            //http://localhost:4400/#/home?token=5ea06c66-5c01-477b-a91a-e8d695523571
            let token = queryParams.get("token");
            this.dataService.getRegistrazioneByToken(token).subscribe(res=>{
                this.nomeIstituto = res.nomeIstituto;
                this.nomeEnte = res.nomeEnte;
                this.emailEnte = res.email;
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