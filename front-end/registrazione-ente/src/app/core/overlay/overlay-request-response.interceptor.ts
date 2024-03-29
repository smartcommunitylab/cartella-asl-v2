import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { NgbModal, NgbModalOptions } from '@ng-bootstrap/ng-bootstrap';
import { ReloginModal } from './modal/login/relogin-modal.component';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/delay';
import { EventBusService, EmitEvent, Events } from '../services/event-bus.service';
import { AuthService } from '../auth/auth.service';

@Injectable()
export class OverlayRequestResponseInterceptor implements HttpInterceptor {

  constructor(private eventBus: EventBusService, private modalService: NgbModal, private authService: AuthService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // const randomTime = this.getRandomIntInclusive(0, 1500);
    const started = Date.now();
    this.eventBus.emit(new EmitEvent(Events.httpRequest));
    return next
      .handle(req)
      // .delay(randomTime)  //Simulate random Http call delays
      .do(event => {
        if (event instanceof HttpResponse) {
          const elapsed = Date.now() - started;
          //console.log('Http response elapsed time: ' + elapsed);
          this.eventBus.emit(new EmitEvent(Events.httpResponse));
        }
      }, err => {
        // console.log(err);
        if (err instanceof HttpErrorResponse) {
          const elapsed = Date.now() - started;
          this.eventBus.emit(new EmitEvent(Events.httpResponse));

          if ((err.status == 401) || (err.status == 403)) {
            let ngbModalOptions: NgbModalOptions = {
              backdrop : 'static',
              keyboard : false          
            };
            // display toast and redirect to logout.
            const modalRef = this.modalService.open(ReloginModal, ngbModalOptions);
            modalRef.componentInstance.text = "Per favore accedi di nuovo.";
            if (err.error.ex) {
              modalRef.componentInstance.text = err.error.ex;
            }
            modalRef.result.then((result) => {
              if (result == 'LOGIN') {
                this.authService.logout();
              }
            });
          }
          
        }
      });
  }

  getRandomIntInclusive(min, max) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min + 1)) + min; //The maximum is inclusive and the minimum is inclusive 
  } 

}