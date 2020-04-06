import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchAttivitaComponent } from './search-attivita.component';

describe('SearchAttivitaComponent', () => {
  let component: SearchAttivitaComponent;
  let fixture: ComponentFixture<SearchAttivitaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SearchAttivitaComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchAttivitaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
