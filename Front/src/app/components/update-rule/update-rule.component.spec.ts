import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateRuleComponent } from './update-rule.component';
import {FormBuilder} from "@angular/forms";

describe('UpdateRuleComponent', () => {
  let component: UpdateRuleComponent;
  let fixture: ComponentFixture<UpdateRuleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UpdateRuleComponent ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpdateRuleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
