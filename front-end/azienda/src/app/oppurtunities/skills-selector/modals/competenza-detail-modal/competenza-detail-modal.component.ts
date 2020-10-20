import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { NgbModal, NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import { Competenza } from '../../../../shared/interfaces';

@Component({
  selector: 'cm-competenza-detail-modal',
  templateUrl: './competenza-detail-modal.component.html',
  styleUrls: ['./competenza-detail-modal.component.css']
})
export class CompetenzaDetailModalComponent implements OnInit {

  @Input() competenza: Competenza;

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
  }

}
