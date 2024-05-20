import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllRulesComponent } from './all-rules.component';

describe('AllRulesComponent', () => {
  let component: AllRulesComponent;
  let fixture: ComponentFixture<AllRulesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AllRulesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllRulesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
