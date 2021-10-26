import {Injectable} from '@angular/core';
import {HttpEvent, HttpInterceptor, HttpHandler, HttpRequest} from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { AuthService } from '../auth/auth.service';
import { environment } from '../../../environments/environment';
 
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}
  
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.url.includes(environment.serverAPIURL)) {
        const token = this.authService.getAuthorizationHeaderValue();
      if (token) {
        req = req.clone({
          setHeaders: {
            Authorization: token
          }
        });
      }
      return next.handle(req);
    } else {
      return next.handle(req);
    }
      
  }
}