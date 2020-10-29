import { Component, OnInit, OnDestroy, HostListener, ElementRef, ViewChild, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';
import { GrowlerService, GrowlerMessageType } from '../growler/growler.service';
import { DataService } from '../services/data.service';
import { config } from '../../config';
import { AuthenticationService } from '../services/authentication.service';

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
    @ViewChild('navbar') private navbarToStick;
    @ViewChild('navbarHeaderTop') private navbarHeaderTop;

    @Output() isStickyListener = new EventEmitter<boolean>();

    constructor(private router: Router, private growler: GrowlerService, public dataService: DataService, private authService: AuthenticationService, private _eref: ElementRef) { }
    
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
                            if (this.aziende[0].coordinate && this.aziende[0].coordinate.latitude && this.aziende[0].coordinate.longitude) {
                                this.dataService.setAziendaPosition(this.aziende[0].coordinate);
                            } else {
                                this.dataService.setAziendaPosition(config.defaultPosition);
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
            this.dataService.setAziendaPosition(config.defaultPosition);
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