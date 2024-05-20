import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DisableRuleComponent } from './disable-rule.component';
import {DynamicDialogRef} from "primeng/dynamicdialog";

describe('DisableRuleComponent', () => {
  let component: DisableRuleComponent;
  let fixture: ComponentFixture<DisableRuleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DisableRuleComponent ],
      imports :[DynamicDialogRef]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DisableRuleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
