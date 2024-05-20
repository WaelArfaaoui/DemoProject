import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { UserService } from '../../services/user/user.service';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import {UserControllerService} from "../../../open-api";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  providers: [MessageService]
})
export class LoginComponent implements OnInit {

  formLogin!: FormGroup;
  errorFound: boolean = false;

  constructor(
    private userService: UserService,
    private router: Router,
    private fb: FormBuilder,
    private messageService: MessageService , private userControllerService:UserControllerService
  ) { }

  ngOnInit(): void {
    this.formLogin = this.fb.group({
      email: this.fb.control(''),
      password: this.fb.control('')
    });
  }

  handleLogin() {
    this.userService.login(this.formLogin.value).subscribe({
      next: data => {
        this.errorFound = false;
        console.log(this.errorFound);
        console.log(data);
        this.userService.setToken(data);
        this.connectUser(data);
      }, error: error => {
        this.errorFound = true;
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Wrong email or password' });
      }
    });
  }

  connectUser(data: any) {
    this.router.navigate(['/']);
  }
}
