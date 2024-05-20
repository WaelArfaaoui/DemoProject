import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LockUserComponent } from './lock-user.component';
import { DynamicDialogRef, DynamicDialogConfig } from 'primeng/dynamicdialog';
import { MessageService } from 'primeng/api';
import { UserControllerService } from '../../../open-api';
import { of } from 'rxjs';

describe('LockUserComponent', () => {
  let component: LockUserComponent;
  let fixture: ComponentFixture<LockUserComponent>;
  let mockRef: jasmine.SpyObj<DynamicDialogRef>;
  let mockConfig: DynamicDialogConfig;
  let mockMessageService: jasmine.SpyObj<MessageService>;
  let mockUserService: jasmine.SpyObj<UserControllerService>;

  beforeEach(async () => {
    mockRef = jasmine.createSpyObj('DynamicDialogRef', ['close']);
    mockConfig = new DynamicDialogConfig();
    mockMessageService = jasmine.createSpyObj('MessageService', ['add']);
    mockUserService = jasmine.createSpyObj('UserControllerService', ['_delete']);

    await TestBed.configureTestingModule({
      declarations: [LockUserComponent],
      providers: [
        { provide: DynamicDialogRef, useValue: mockRef },
        { provide: DynamicDialogConfig, useValue: mockConfig },
        { provide: MessageService, useValue: mockMessageService },
        { provide: UserControllerService, useValue: mockUserService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LockUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should close dialog', () => {
    // Act
    component.closeDialog();

    // Assert
    expect(mockRef.close).toHaveBeenCalled();
  });

  it('should lock user', () => {
    // Arrange
    const userId = 1;
    component.user = { id: userId };

    // Act
    component.lockUser();

    // Assert
    expect(mockUserService._delete).toHaveBeenCalledWith(userId);
    expect(mockRef.close).toHaveBeenCalled();
    expect(mockMessageService.add).toHaveBeenCalledWith({
      severity: 'success',
      summary: 'User locked !',
      detail: 'User locked successfully'
    });
  });
});
