import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CompetenzaDetailModalComponent } from './competenza-detail-modal.component';

describe('CompetenzaDetailModalComponent', () => {
  let component: CompetenzaDetailModalComponent;
  let fixture: ComponentFixture<CompetenzaDetailModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CompetenzaDetailModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CompetenzaDetailModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
