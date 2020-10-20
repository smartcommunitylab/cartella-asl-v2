import { Component, OnInit, Input } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { GrowlerService, GrowlerMessageType } from '../../core/growler/growler.service';
import { IPagedResults, EsperienzaSvolta, Valutazione } from '../../shared/interfaces';
import { DataService } from '../../core/services/data.service';

@Component({
  selector: 'activity-evaluation-edit',
  templateUrl: './activity-evaluation-edit.component.html',
  styleUrls: ['./activity-evaluation-edit.component.scss']
})
export class ActivityEvalutationEditComponent implements OnInit {

  @Input() espId: any;

  eval: any;

  fileList: FileList;

  constructor(private growler:GrowlerService, public activeModal: NgbActiveModal, private router: Router, private route: ActivatedRoute, private dataService: DataService) {
  }

  ngOnInit() {}



  submit(event: Event) {
     

    event.preventDefault();
   

    if (this.espId) {

      this.dataService.uploadDocument(this.fileList[0], this.espId).subscribe((val: Valutazione) => {
        if (val) {
          //success
          this.growler.growl('Successo.', GrowlerMessageType.Success);
          this.activeModal.close("update");
          
        } else {
          //failure.
          this.growler.growl('Errore.', GrowlerMessageType.Warning);
          this.activeModal.close("update");
        }
      });

    }
  }

  fileChange(event) {
    this.fileList = event.target.files;

  }

  cancel(event: Event) {
     
    event.preventDefault();
    this.activeModal.close("cancel");
  }

}


