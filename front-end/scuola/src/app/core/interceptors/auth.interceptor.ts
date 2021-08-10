import {Injectable} from '@angular/core';
import {HttpEvent, HttpInterceptor, HttpHandler, HttpRequest} from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { SERVER_API_URL } from '../../app.constants';
import { AuthService } from '../auth/auth.service';
 
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}
  
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    // if (!request || !request.url || (request.url.startsWith('http') && !(SERVER_API_URL && request.url.startsWith(SERVER_API_URL)))) {
    //   return next.handle(request);
    // }

    const token = this.authService.getAuthorizationHeaderValue();
    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: token
        }
      });
    }
    
    return next.handle(request);
  }

}