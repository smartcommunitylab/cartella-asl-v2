import { Component, ViewEncapsulation} from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { GrowlerService, GrowlerMessageType } from '../core/growler/growler.service';
import { HttpClient } from '@angular/common/http';
import { DataService } from '../core/services/data.service';

@Component({
    selector: 'terms-component',
    templateUrl: './terms-component.html',
    styleUrls: ['./terms-component.scss'],
    encapsulation: ViewEncapsulation.None,
})

export class TermsComponent {
   
    termsFile: any;
    accepted: Boolean = false;
  
    constructor(private route: ActivatedRoute, public dataService: DataService, private growler: GrowlerService, private http: HttpClient) {

        // load html file.
        var url = 'assets/terms/terms.html';
        this.http.get(url, {responseType: 'text'} ).subscribe(data => {
            this.termsFile = data;
         });
        // set flag (to show accept refuse button)
        this.route.params.subscribe((params: Params) => {
            if (params['authorized'] == 'true') {
                this.accepted = true;
            }        
        });
    }

    goToMainPage = function () {
        var getUrl = window.location;
        var baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];
        var aziendaUrl = baseUrl + '/asl-azienda/';
        window.location.href = aziendaUrl;

    }

    acceptPrivacy = function () {
        this.dataService.addConsent().subscribe(consenso => {
            this.accepted = consenso.authorized;
            this.goToMainPage();
        }
        )
    };

    refusePrivacy = function () {

        this.growler.growl('Termini rifiutati.', GrowlerMessageType.Warning);
        var getUrl = window.location;
        var baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];
        var logoutUrl = baseUrl + '/logout?target=' + baseUrl + '/asl-login/';
        window.location.href = logoutUrl;
        console.log('App closed');
    };

}
