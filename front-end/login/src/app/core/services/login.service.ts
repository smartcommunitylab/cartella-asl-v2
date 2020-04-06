import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { catchError, map } from 'rxjs/operators';
import { IApiResponse } from '../../shared/classes/IApiResponse.class';


@Injectable()
export class LoginService {
    map: Map<string, any> = new Map<string, any>();

    constructor() {
        this.map.set('studente', {
            message: `Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus interdum lacinia nulla, sed aliquet quam viverra non. Ut porta eget metus in vehicula. Aenean ultricies, mauris faucibus tristique volutpat, odio elit elementum arcu, et tincidunt odio mauris non risus. Ut at viverra massa. Proin faucibus diam ac enim mattis, ut egestas velit lacinia. Praesent hendrerit justo ipsum, ac venenatis lorem tempor quis. Praesent lobortis massa et sem viverra convallis.
        
        Sed vitae purus porta, porttitor purus aliquet, tincidunt neque. Sed tempus libero dui, non dignissim tellus vulputate non. Etiam tincidunt condimentum mauris, ac fermentum risus pulvinar quis. Suspendisse pulvinar tristique pretium. Suspendisse quis magna purus. Nam vehicula ex sed nisl auctor, commodo lacinia dolor maximus. Curabitur semper libero ante.`, image: 'assets/images/student.jpg'
        });
        this.map.set('azienda', {
            message: `Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus interdum lacinia nulla, sed aliquet quam viverra non. Ut porta eget metus in vehicula. Aenean ultricies, mauris faucibus tristique volutpat, odio elit elementum arcu, et tincidunt odio mauris non risus. Ut at viverra massa. Proin faucibus diam ac enim mattis, ut egestas velit lacinia. Praesent hendrerit justo ipsum, ac venenatis lorem tempor quis. Praesent lobortis massa et sem viverra convallis.
        
        Sed vitae purus porta, porttitor purus aliquet, tincidunt neque. Sed tempus libero dui, non dignissim tellus vulputate non. Etiam tincidunt condimentum mauris, ac fermentum risus pulvinar quis. Suspendisse pulvinar tristique pretium. Suspendisse quis magna purus. Nam vehicula ex sed nisl auctor, commodo lacinia dolor maximus. Curabitur semper libero ante.`, image: 'assets/images/company.png'
        });
        this.map.set('scuola', {
            message: `Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus interdum lacinia nulla, sed aliquet quam viverra non. Ut porta eget metus in vehicula. Aenean ultricies, mauris faucibus tristique volutpat, odio elit elementum arcu, et tincidunt odio mauris non risus. Ut at viverra massa. Proin faucibus diam ac enim mattis, ut egestas velit lacinia. Praesent hendrerit justo ipsum, ac venenatis lorem tempor quis. Praesent lobortis massa et sem viverra convallis.
        
        Sed vitae purus porta, porttitor purus aliquet, tincidunt neque. Sed tempus libero dui, non dignissim tellus vulputate non. Etiam tincidunt condimentum mauris, ac fermentum risus pulvinar quis. Suspendisse pulvinar tristique pretium. Suspendisse quis magna purus. Nam vehicula ex sed nisl auctor, commodo lacinia dolor maximus. Curabitur semper libero ante.`, image: 'assets/images/school.jpg'
        });
    }
    getLoginMessage(app) {
        return this.map.get(app).message;
    };
    getImage(app) {
        return this.map.get(app).image;
    }

}