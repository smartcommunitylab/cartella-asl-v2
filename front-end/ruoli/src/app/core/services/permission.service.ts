import { Injectable } from '@angular/core';

@Injectable()
export class PermissionService {

  private cachedProfile;
  private myPermissions: UserPermission;

  constructor() { }

  setProfileForPermissions(profile) {
    this.cachedProfile = profile;
    this.myPermissions = new UserPermission();

    // if (this.cachedProfile.roles.indexOf('ADMIN') > -1) {
    //   console.log("User is ADMIN");
    //   Object.keys(this.myPermissions.addRole).forEach(key => {
    //     this.myPermissions.addRole[key] = true;
    //   });
    //   Object.keys(this.myPermissions.viewUsersList).forEach(key => {
    //     this.myPermissions.viewUsersList[key] = true;
    //   });
    // }
    // if (this.cachedProfile.roles.indexOf('LEGALE_RAPPRESENTANTE_AZIENDA') > -1) {
    //   console.log("User is LEGALE_RAPPRESENTANTE_AZIENDA");
    //   this.myPermissions.viewUsersList.referenti_azienda = true;
    //   this.myPermissions.addRole.referenti_azienda = true;
    // }
    // if (this.cachedProfile.roles.indexOf('DIRIGENTE_SCOLASTICO') > -1) {
    //   console.log("User is DIRIGENTE_SCOLASTICO");
    //   this.myPermissions.viewUsersList.funzione_strumentale = true;
    //   this.myPermissions.addRole.funzione_strumentale = true;
    // }

    for (var key in this.cachedProfile.roles) {

      var value = this.cachedProfile.roles[key];

      if (value.role == 'ADMIN') {
        console.log("User is ADMIN");
        Object.keys(this.myPermissions.addRole).forEach(key => {
          this.myPermissions.addRole[key] = true;
        });
        Object.keys(this.myPermissions.viewUsersList).forEach(key => {
          this.myPermissions.viewUsersList[key] = true;
        });

      }
      if (value.role == 'LEGALE_RAPPRESENTANTE_AZIENDA') {
        console.log("User is LEGALE_RAPPRESENTANTE_AZIENDA");
        this.myPermissions.viewUsersList.referenti_azienda = true;
        this.myPermissions.addRole.referenti_azienda = true;

      }
      if (value.role == 'DIRIGENTE_SCOLASTICO') {
        console.log("User is DIRIGENTE_SCOLASTICO");
        this.myPermissions.viewUsersList.funzione_strumentale = true;
        this.myPermissions.addRole.funzione_strumentale = true;
      }
    }



    console.log(this.myPermissions);
  }

  getMyPermissions(): UserPermission {
    return this.myPermissions;
  }


}

export class UserPermission {
  viewUsersList: UserPermissionRead;
  addRole: UserPermissionWrite;
  
  constructor() {
    this.viewUsersList = new UserPermissionRead();
    this.addRole = new UserPermissionWrite();
  }
}
export class UserPermissionRead {
  all: boolean;
  studenti: boolean;
  legali_rappresentati_azienda: boolean;
  referenti_azienda: boolean;
  dirigenti_scolastici: boolean;
  funzione_strumentale: boolean;

  constructor() {
    this.all = false;
    this.studenti = false;
    this.legali_rappresentati_azienda = false;
    this.referenti_azienda = false;
    this.dirigenti_scolastici = false;
    this.funzione_strumentale = false;
  }
}
export class UserPermissionWrite {
  admin: boolean = false;
  studenti: boolean = false;
  legali_rappresentati_azienda: boolean = false;
  referenti_azienda: boolean = false;
  dirigenti_scolastici: boolean = false;
  funzione_strumentale: boolean = false;
  from_all_entities: boolean = false;

  constructor() {
    this.admin = false;
    this.studenti = false;
    this.legali_rappresentati_azienda = false;
    this.referenti_azienda = false;
    this.dirigenti_scolastici = false;
    this.funzione_strumentale = false;
    this.from_all_entities = false;
  }
}