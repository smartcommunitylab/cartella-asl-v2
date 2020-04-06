import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AttivitaDetailComponent } from './attivita-detail.component';

describe('AttivitaDetailComponent', () => {
  let component: AttivitaDetailComponent;
  let fixture: ComponentFixture<AttivitaDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AttivitaDetailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AttivitaDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
