import {Injectable} from '@angular/core';
import {HttpEvent, HttpInterceptor, HttpHandler, HttpRequest} from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
 
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor() {}
 
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authHeader = `Bearer ${sessionStorage.getItem('access_token')}`; 
    // let authHeader =  "Bearer 8f09a44d-86be-42b9-9830-686a13b6d361";
    const authReq = req.clone({headers: req.headers.set('Authorization', authHeader)});
    return next.handle(authReq);
  }
}