import {Injectable} from '@angular/core';
import {HttpEvent, HttpInterceptor, HttpHandler, HttpRequest} from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
 
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor() {}
 
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.url.indexOf('/cartella-asl/api') > 0) {
      const authHeader = `Bearer ${sessionStorage.getItem('access_token')}`;
      const authReq = req.clone({ headers: req.headers.set('Authorization', authHeader) });
      return next.handle(authReq);
    } else {
      return next.handle(req);
    }
      
  }
}