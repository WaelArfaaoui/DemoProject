import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AllUsersComponent } from './all-users.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
describe('AllUsersComponent', () => {
  let component: AllUsersComponent;
  let fixture: ComponentFixture<AllUsersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AllUsersComponent ],
      imports: [ HttpClientTestingModule ]
    }).compileComponents();

    fixture = TestBed.createComponent(AllUsersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });



});
