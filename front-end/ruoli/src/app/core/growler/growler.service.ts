import { Injectable } from '@angular/core';

@Injectable()
export class GrowlerService {

  constructor() { }

  growl: (message: string, growlType: GrowlerMessageType, timeout?:number) => number;

}

export enum GrowlerMessageType {
  Success,
  Danger,
  Warning,
  Info
}