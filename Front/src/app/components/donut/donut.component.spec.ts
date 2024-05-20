import {HttpClient} from "@angular/common/http";

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DonutComponent } from './donut.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('DonutComponent', () => {
  let component: DonutComponent;
  let fixture: ComponentFixture<DonutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DonutComponent ],
      imports: [ HttpClientTestingModule ],
      providers: [ DonutComponent ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(DonutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
