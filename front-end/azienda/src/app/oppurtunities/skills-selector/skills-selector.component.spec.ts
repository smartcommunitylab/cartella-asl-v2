import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SkillsSelectorComponent } from './skills-selector.component';

describe('SkillsSelectorComponent', () => {
  let component: SkillsSelectorComponent;
  let fixture: ComponentFixture<SkillsSelectorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SkillsSelectorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SkillsSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
