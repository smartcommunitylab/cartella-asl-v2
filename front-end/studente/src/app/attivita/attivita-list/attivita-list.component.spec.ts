import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AttivitaListComponent } from './attivita-list.component';

describe('AttivitaListComponent', () => {
  let component: AttivitaListComponent;
  let fixture: ComponentFixture<AttivitaListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AttivitaListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AttivitaListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
