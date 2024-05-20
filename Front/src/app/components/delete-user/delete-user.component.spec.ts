import { ComponentFixture, TestBed, tick, fakeAsync } from '@angular/core/testing';
import { DeleteUserComponent } from './delete-user.component';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { ToastModule } from "primeng/toast";
import { MessageService } from "primeng/api";
import { UserControllerService } from "../../../open-api";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { of } from 'rxjs';
import any = jasmine.any;
import anything = jasmine.anything;

describe('DeleteUserComponent', () => {
  let component: DeleteUserComponent;
  let fixture: ComponentFixture<DeleteUserComponent>;
  let messageService: jasmine.SpyObj<MessageService>;
  let userServiceController: jasmine.SpyObj<UserControllerService>;
  let dialogRefSpyObj = jasmine.createSpyObj('DynamicDialogRef', ['close']);

  beforeEach(async () => {
    messageService = jasmine.createSpyObj('MessageService', ['add']);
    userServiceController = jasmine.createSpyObj('UserControllerService', ['_delete']);

    await TestBed.configureTestingModule({
      declarations: [DeleteUserComponent],
      imports: [ToastModule, HttpClientTestingModule],
      providers: [
        DynamicDialogConfig,
        { provide: DynamicDialogRef, useValue: dialogRefSpyObj },
        { provide: MessageService, useValue: messageService },
        { provide: UserControllerService, useValue: userServiceController }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DeleteUserComponent);
    component = fixture.componentInstance;
    component.config.data = { id: 123 };

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should close dialog when closeDialog method is called', () => {
    component.closeDialog();
    expect(dialogRefSpyObj.close).toHaveBeenCalled();
  });

  it('should delete user successfully', fakeAsync(() => {
    const userId = 123; // Example user id

    component.deleteUser();

    expect(userServiceController._delete).toHaveBeenCalledWith(userId);

    // Simulate response from userServiceController._delete
    tick();

    expect(dialogRefSpyObj.close).toHaveBeenCalledWith(true);
    expect(messageService.add).toHaveBeenCalledWith({
      severity: 'success',
      summary: 'User deleted !',
      detail: 'User deleted successfully'
    });
  }));

/*  it('should handle error when deleting user', async () => {
    const errorMessage = 'Error deleting user';

    deletespy = userServiceController._delete.and.returnValue(of(errorMessage));

    component.deleteUser();
await  fixture.whenStable()
    expect(deletespy).toHaveBeenCalledWith(component.config.data.id);
    tick();
    expect(dialogRefSpyObj.close).not.toHaveBeenCalled();
    expect(messageService.add).toHaveBeenCalledWith({
      severity: 'error',
      summary: 'Error',
      detail: errorMessage
    });
  })*/
});
