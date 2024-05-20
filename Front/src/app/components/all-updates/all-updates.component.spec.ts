import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllUpdatesComponent } from './all-updates.component';

describe('AllUpdatesComponent', () => {
  let component: AllUpdatesComponent;
  let fixture: ComponentFixture<AllUpdatesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AllUpdatesComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllUpdatesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
