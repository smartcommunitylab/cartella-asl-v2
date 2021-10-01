import { Component, OnInit, OnDestroy, ElementRef, ViewChild, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { DataService } from '../services/data.service';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../core/auth/auth.service';

@Component({
    selector: 'cm-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit, OnDestroy {

    isCollapsed: boolean;
    loginLogoutText: string = 'Login';
    sub: Subscription;
    isSticky: boolean = false;
    profile;
    aziende;
    actualAzienda;
    profileDropdownVisible;
  
    @Output() isStickyListener = new EventEmitter<boolean>();

    constructor(private router: Router, public dataService: DataService, private authService: AuthService, private _eref: ElementRef) { }

    ngOnInit() {

        this.dataService.getProfile().subscribe(profile => {

            if (profile) {
                this.profile = profile;
                if (profile && profile.aziende) {
                    // get azienda ids(keys of map).
                    var ids = [];
                    for (var k in profile.aziende) {
                        ids.push(k);
                    }
                    this.dataService.getListaAziendeByIds(ids).subscribe((aziende: Array<any>) => {
                        if (aziende) {
                            this.aziende = aziende;
                            this.dataService.setAziendaId(this.aziende[0].id);
                            this.dataService.setAziendaName(this.aziende[0].nome);
                            this.dataService.setOwnerId(this.profile.id)
                            if (this.aziende[0].coordinate && this.aziende[0].coordinate.latitude && this.aziende[0].coordinate.longitude) {
                                this.dataService.setAziendaPosition(this.aziende[0].coordinate);
                            } else {
                                this.dataService.setAziendaPosition(environment.defaultPosition);
                            }
                            this.actualAzienda = this.aziende[0];
                            this.dataService.setListId(this.aziende);
                        }
                    }
                        , err => {
                            console.log('error, no azienda')
                        });

                }
            }
        })
    }

    onAziendaChange(azienda) {
        this.actualAzienda = azienda;
        this.dataService.setAziendaId(azienda.id);
        this.dataService.setAziendaName(azienda.nome);
        if (azienda.coordinate && azienda.coordinate.latitude && azienda.coordinate.longitude) {
            this.dataService.setAziendaPosition(azienda.coordinate);
        } else {
            this.dataService.setAziendaPosition(environment.defaultPosition);
        }
        this.router.navigate(['/attivita']);

    }

    ngOnDestroy() {
        this.sub.unsubscribe();
    }

    loginOrOut() {

    }

    logout() {
        this.authService.logout();
    }

    redirectToLogin() {
        this.router.navigate(['/login']);
    }

    setLoginLogoutText() {
    }

    isActive(location) {
        if (this.router.url.includes(location)) {
            return true;
        }
    }

}