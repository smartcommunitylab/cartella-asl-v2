import { Component, OnInit, OnDestroy, HostListener, ElementRef, ViewChild, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';

import { Subscription } from 'rxjs/Subscription';

// import { AuthService } from '../services/auth.service';
import { GrowlerService, GrowlerMessageType } from '../growler/growler.service';
import { DataService } from '../services/data.service';
import { config } from '../../config';
import { AuthenticationService } from '../services/authentication.service';

@Component({
    selector: 'cm-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.scss'],
    host: {
        '(document:click)': 'onClick($event)',
      }
})
export class NavbarComponent implements OnInit, OnDestroy {

    isCollapsed: boolean;
    loginLogoutText: string = 'Login';
    sub: Subscription;
    actualIstituto;
    isSticky: boolean = false;
    annoScolastico: string = '';
    titleAnnoScolastico: string = '';
    profileDropdownVisible = false;
    annoScolasticoDropdownVisible = false;
    profile;
    istitutes = [];
    active;
    @ViewChild('navbar') private navbarToStick;
    @ViewChild('navbarHeaderTop') private navbarHeaderTop;

    @Output() isStickyListener = new EventEmitter<boolean>();

    constructor(private router: Router, private growler: GrowlerService, private dataService: DataService, private authService: AuthenticationService, private _eref: ElementRef) { 
        this.titleAnnoScolastico = dataService.schoolYear;
    }

    ngOnInit() {
        this.dataService.getProfile().subscribe(profile => {
            //set profile ID
            if (profile) {
                this.profile = profile;
                if (profile && profile.istituti) {
                    // get ids(keys of map).
                    var ids = [];
                    for (var k in profile.istituti) {
                        ids.push(k);
                    } 
                    this.dataService.getListaIstitutiByIds(ids).subscribe((istitutes: Array<any>) => {
                        if (istitutes) {
                            this.istitutes = istitutes;
                            this.dataService.setIstitutoId(this.istitutes[0].id);
                            this.dataService.setIstitutoName(this.istitutes[0].name);
                            if (this.istitutes[0].coordinate && this.istitutes[0].coordinate.latitude && this.istitutes[0].coordinate.longitude) {
                                this.dataService.setIstitutoPosition(this.istitutes[0].coordinate);
                            } else {
                                this.dataService.setIstitutoPosition(config.defaultPosition);
                            }                            
                            this.actualIstituto = this.istitutes[0];
                            this.dataService.setListId(this.istitutes);
                        }
                    }
                        , err => {
                            console.log('error, no institute')
                        });

                }
            }
        })
    }
    onClick(event) {
        if (!this._eref.nativeElement.contains(event.target)){
            this.profileDropdownVisible = false;
        }
    }
    
    onIstitutoChange(istituto) {
        this.actualIstituto = istituto;
        this.dataService.setIstitutoId(istituto.id);
        this.dataService.setIstitutoName(istituto.name);
        if (istituto.coordinate && istituto.coordinate.latitude && istituto.coordinate.longitude) {
            this.dataService.setIstitutoPosition(istituto.coordinate);
        } else {
            this.dataService.setIstitutoPosition(config.defaultPosition);
        } 
        this.router.navigate(['/piani', istituto.id]);    
        // this.router.navigateByUrl('/gestione', { skipLocationChange: true }).then(() => {
        //     this.router.navigate(['/piani']);
        // }); 

    }
    
    ngOnDestroy() {
        this.sub.unsubscribe();
    }
    saveAnno() {
        this.titleAnnoScolastico = this.annoScolastico;
        this.profileDropdownVisible = false;
        this.annoScolasticoDropdownVisible = false;
    }
    loginOrOut() {

    }
    logout() {
        // var getUrl = window.location;
        // var baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split('/')[1];
        // var logoutUrl = baseUrl + '/logout?target='+baseUrl+'/asl-login/';
        // window.location.href = logoutUrl;
        this.authService.logout();

    }
    redirectToLogin() {
        this.router.navigate(['/login']);
    }

    setLoginLogoutText() {
    }

    // @HostListener("window:scroll", [])
    // onWindowScroll() {
    //     var sticky = this.navbarHeaderTop.nativeElement.clientHeight;
    //     if (this.isSticky != (window.pageYOffset >= sticky)) {
    //         this.isSticky = window.pageYOffset >= sticky;
    //         this.isStickyListener.emit(this.isSticky);
    //     }
    // }

    isActive(location) {
        if (this.router.url.includes(location)) {
            return true;
        }
    }
    

}