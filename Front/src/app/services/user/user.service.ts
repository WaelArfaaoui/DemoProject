import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Router} from "@angular/router";
import {jwtDecode} from "jwt-decode";
import {catchError, Observable} from "rxjs";
import {
  AuthenticationService,
  AuthenticationRequest,
  UserControllerService,
  UserDto,
  RegisterDto
} from "../../../open-api";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  token!: any;
  email!: string;
  name!: string;
  id!: number;
  constructor(private authenticationService: AuthenticationService, private http: HttpClient,
              private router: Router, private userService: UserControllerService
  ) {
  }
  login(authenticationRequest: AuthenticationRequest) {
    return this.authenticationService.authenticate(authenticationRequest);
  }
  addUser(dto: any, file: File): Observable<UserDto> {
    const formData: FormData = new FormData();
    formData.append('file', file);
    Object.keys(dto).forEach(key => formData.append(key, dto[key]));
    return this.http.post<any>('http://localhost:8090/api/users', formData);
  }
  getUserDetails() {
    this.token = localStorage.getItem('accessToken')
    let decodedJwt: any = jwtDecode(this.token);
    this.email = decodedJwt.sub;
    return {
      'email': this.email,
    }
  }

  isUserLoggedAndAccessTokenValid(): boolean {
    const accessToken = localStorage.getItem('accessToken');
    if (accessToken) {
      if (this.isValidAccessToken(accessToken)) {
        return true;
      }
    }
    localStorage.clear();
    this.router.navigate(['/signIn']);
    return false;
  }
  isValidAccessToken(token: string): boolean {
    try {
      const tokenData = JSON.parse(atob(token.split('.')[1]));
      const currentTimeInSeconds = Math.floor(Date.now() / 1000);
      if (tokenData.exp && tokenData.exp > currentTimeInSeconds) {
        return true;
      }
    } catch (error) {
    }
    return false;
  }

  setToken(data: any) {
    localStorage.setItem('accessToken', data['access_token']);
  }
  getAllUsers() {
    return this.userService.getusers();
  }
  updateUser(id: number, registerDto: RegisterDto, file: File): Observable<UserDto> {
    const formData = new FormData();
    formData.append('dto', JSON.stringify(registerDto));
    formData.append('file', file);
    // Set headers explicitly
    const headers = new HttpHeaders();
    headers.append('Content-Type', 'multipart/form-data');
    return this.http.put<UserDto>(`http://localhost:8090/api/users/${id}`, formData, { headers: headers })
      .pipe(
        catchError((error: any) => {
          console.error('An error occurred:', error);
          throw error;
        })
      );
  }

}
