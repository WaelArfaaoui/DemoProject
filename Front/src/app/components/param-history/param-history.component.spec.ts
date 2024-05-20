import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ParamHistoryComponent } from './param-history.component';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { TableService } from '../../services/table/table.service';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import {ParamAudit} from "../../model/param-audit";

describe('ParamHistoryComponent', () => {
  let component: ParamHistoryComponent;
  let fixture: ComponentFixture<ParamHistoryComponent>;
  let tableServiceSpy: jasmine.SpyObj<TableService>;
  let messageServiceSpy: jasmine.SpyObj<MessageService>;
  let ref: jasmine.SpyObj<DynamicDialogRef>;

  beforeEach(async () => {
    tableServiceSpy = jasmine.createSpyObj('TableService', ['paramHistory']);
    messageServiceSpy = jasmine.createSpyObj('MessageService', ['add']);
    ref = jasmine.createSpyObj('DynamicDialogRef', ['close']);
    await TestBed.configureTestingModule({
      declarations: [ParamHistoryComponent],
      providers: [
        { provide: DynamicDialogRef, useValue: ref},
        { provide: DynamicDialogConfig, useValue: { data: { tableName: 'TestTable' } } },
        { provide: TableService, useValue: tableServiceSpy },
        { provide: MessageService, useValue: messageServiceSpy },
        { provide: DynamicDialogRef, useValue: ref }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ParamHistoryComponent);
    component = fixture.componentInstance;
    const testData: ParamAudit[] = [
      {
        id: 1,
        tableName: 'TestTable',
        action: 'DELETED',
        version: 1,
        rowId: '123',
        oldRow: 'Old data',
        newRow: '',
        createdBy: 'User1',
        createdAt: '2022-05-10',
        lastModifiedBy: '',
        lastModifiedAt: ''
      },
      {
        id: 2,
        tableName: 'TestTable',
        action: 'EDITED',
        version: 1,
        rowId: '456',
        oldRow: 'Old data',
        newRow: 'New data',
        createdBy: 'User2',
        createdAt: '2022-05-11',
        lastModifiedBy: 'User2',
        lastModifiedAt: '2022-05-11'
      }
    ];
    // Mock the paramHistory method to return an observable with test data
    tableServiceSpy.paramHistory.and.returnValue(of(testData));

    // Call ngOnInit to trigger the paramHistory method
    component.ngOnInit();
    await fixture.whenStable();

    // Assertions
    expect(component.tableName).toBe('TestTable');
    expect(tableServiceSpy.paramHistory).toHaveBeenCalledWith('TestTable');
    expect(component.paramAuditHistory).toEqual(testData);
    expect(messageServiceSpy.add).toHaveBeenCalledWith(jasmine.objectContaining({
      severity: 'success',
      summary: 'history',
      detail: 'History loaded for TestTable'
    }));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load param history on init', async () => {

  });

  it('should close the dialog', () => {
    component.closeDialog();
    expect(component.ref.close).toHaveBeenCalled();
  });
});
