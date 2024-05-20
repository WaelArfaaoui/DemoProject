import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StackedColumnsComponent } from './stacked-columns.component';

describe('StackedColumnsComponent', () => {
  let component: StackedColumnsComponent;
  let fixture: ComponentFixture<StackedColumnsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StackedColumnsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StackedColumnsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
