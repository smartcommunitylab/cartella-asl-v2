import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

import { GrowlerService, GrowlerMessageType } from './growler.service';

@Component({
  selector: 'cm-growler',
  template: `
    <div [ngClass]="position" class="growler">
      <div *ngFor="let growl of growls" [ngClass]="{active: growl.enabled}" 
          class="growl alert {{ growl.messageType }}">
          <div class="row alert-message">
            <div class="col-2 bg-image">
            </div>
            <div class="col-10"><span class="growl-message" [innerHtml]="growl.message"></span></div>
          </div>
      </div>
    </div>
  `,
  styleUrls: ['growler.component.css']
})
export class GrowlerComponent implements OnInit {
   
  private growlCount: number = 0;  
  growls: Growl[] = [];
  
  @Input() position: string = 'bottom-right'; 
  @Input() timeout: number = 3000;
  
  constructor(private growlerService: GrowlerService) {
    growlerService.growl = this.growl.bind(this);
  }

  ngOnInit() { }
   
  /**
  * Displays a growl message.
  *
  * @param {string} message - The message to display.
  * @param {GrowlMessageType} growlType - The type of message to display (a GrowlMessageType enumeration)
  * @return {number} id - Returns the ID for the generated growl
  */
  growl(message: string, growlType: GrowlerMessageType, timeout?:number) : number {  
     this.growlCount++;
     const bootstrapAlertType = GrowlerMessageType[growlType].toLowerCase();
     const messageType = `alert-${ bootstrapAlertType }`;     
     
     if (!timeout) timeout = this.timeout;
     const growl = new Growl(this.growlCount, message, messageType, timeout, this);
     this.growls.push(growl);
     return growl.id;
  }
  
  removeGrowl(id: number) {
    this.growls.forEach((growl: Growl, index: number) => {
      if (growl.id === id) {
        this.growls.splice(index, 1);
        this.growlCount--;
        console.log('removed ' + id)
      }
    });
  }
}

class Growl {
  
  enabled: boolean;
  timeoutId: number;  
  
  constructor(public id: number, 
              public message: string, 
              public messageType: string, 
              private timeout: number, 
              private growlerContainer: GrowlerComponent) { 
    this.show();
  }
  
  show() {
    window.setTimeout(() => {
      this.enabled = true;
      this.setTimeout();
    }, 0);
  }
    
  setTimeout() {
    window.setTimeout(() => {
      this.hide();
    }, this.timeout);
  }  
  
  hide() {
    this.enabled = false;
    window.setTimeout(() => {
      this.growlerContainer.removeGrowl(this.id);
    }, 0);
  }
  
}