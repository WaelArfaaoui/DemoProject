import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewRuleComponent } from './new-rule.component';
import {FormBuilder} from "@angular/forms";
import {ToastModule} from "primeng/toast";
import {MessageService} from "primeng/api";
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('NewRuleComponent', () => {
  let component: NewRuleComponent;
  let fixture: ComponentFixture<NewRuleComponent>;
 let messageService = jasmine.createSpyObj('MessageService', ['add']);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NewRuleComponent ],
      imports:[ToastModule,HttpClientTestingModule],

      providers:[FormBuilder,
        { provide: MessageService , usevalue: messageService}]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewRuleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
