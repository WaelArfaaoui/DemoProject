import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RuleHistoryComponent } from './rule-history.component';
import {DynamicDialogConfig} from "primeng/dynamicdialog";

describe('RuleHistoryComponent', () => {
  let component: RuleHistoryComponent;
  let fixture: ComponentFixture<RuleHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RuleHistoryComponent ],
      providers: [ DynamicDialogConfig ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RuleHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
