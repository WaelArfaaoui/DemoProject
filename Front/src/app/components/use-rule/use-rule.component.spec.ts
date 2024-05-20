import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UseRuleComponent } from './use-rule.component';

describe('UseRuleComponent', () => {
  let component: UseRuleComponent;
  let fixture: ComponentFixture<UseRuleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UseRuleComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UseRuleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
