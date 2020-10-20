import { Injectable } from '@angular/core';

import { ICustomer, IOrder, IOffer } from '../../shared/interfaces';

@Injectable()
export class TrackByService {
  
  customer(index:number, customer: ICustomer) {
    return customer.id;
  }

  offer(index:number, offer: IOffer) {
    return offer.id;
  }

  order(index:number, order: IOrder) {
    return index;
  }


  
}