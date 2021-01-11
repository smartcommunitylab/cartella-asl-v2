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
        this.title = 'Template email link';
        // // alert(this.route);

        // this.route.params.subscribe(params => {
        //     let id = params['id'];
        // });

        this.route.queryParamMap.subscribe(queryParams => {
            // http://localhost:4400/#/home?id=test
            console.log(queryParams.get("id"));
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