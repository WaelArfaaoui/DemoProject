import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UserService } from './user.service';
import { AuthenticationService, UserControllerService} from '../../../open-api';


describe('UserService', () => {
  let service: UserService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UserService, AuthenticationService, UserControllerService]
    });

    service = TestBed.inject(UserService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  /*it('should login successfully', () => {
    const authenticationRequest: AuthenticationRequest = { /!* Your authentication request data here *!/ };
    const expectedResponse = { /!* Your expected response data here *!/ };

    service.login(authenticationRequest).subscribe(response => {
      expect(response).toEqual(expectedResponse);
    });

    const req = httpTestingController.expectOne();
    expect(req.request.method).toEqual('/!* Expected HTTP method *!/');

    req.flush(expectedResponse);
  });*/

 /* it('should add a new user', () => {
    const dto = { /!* Your user data here *!/ };
    const file = new File(['file'], 'filename', { type: 'text/plain' });
    const expectedResponse: Observable<UserDto> = { /!* Your expected response data here *!/ };

    service.addUser(dto, file).subscribe(response => {
      expect(response).toEqual(expectedResponse);
    });

    const req = httpTestingController.expectOne(/!* Expected URL for adding user *!/);
    expect(req.request.method).toEqual('POST');

    req.flush(expectedResponse);
  });
*/
  it('should get user details', () => {
    // Mock localStorage.getItem() and jwtDecode() if needed
    // Then test the getUserDetails() method
  });

});

