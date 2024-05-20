import {Component, OnInit} from '@angular/core';
import {UserService} from "../../services/user/user.service";
import {Router} from "@angular/router";
import {FormBuilder, FormGroup} from "@angular/forms";
import {MessageService} from 'primeng/api';
import {UserDto} from "../../../open-api";

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.scss'],
  providers: [MessageService]
})
export class AddUserComponent implements OnInit {

    errorFound: boolean = false;
    formSave!: FormGroup;
    file!: File;
    constructor(private userService: UserService,
                private router: Router,
                private fb: FormBuilder,
                private messageService: MessageService) { }
  ngOnInit(): void {
    this.formSave = this.fb.group({
      firstname: this.fb.control(''),
      lastname: this.fb.control(''),
      email: this.fb.control(''),
      phone: this.fb.control(''),
      company: this.fb.control(''),
      role: [UserDto.RoleEnum.Admin],
      password: this.fb.control(''),
    });

    // Remove the value change subscription
    // this.formSave.get('role')!.valueChanges.subscribe((role: UserDto.RoleEnum) => {
    //   switch (role) {
    //     case UserDto.RoleEnum.Admin:
    //       this.formSave.get('role')!.setValue(UserDto.RoleEnum.Admin);
    //       break;
    //     case UserDto.RoleEnum.Businessexpert:
    //       this.formSave.get('role')!.setValue(UserDto.RoleEnum.Businessexpert);
    //       break;
    //     case UserDto.RoleEnum.Consultant:
    //       this.formSave.get('role')!.setValue(UserDto.RoleEnum.Consultant);
    //       break;
    //     default:
    //       this.formSave.get('role')!.setValue(UserDto.RoleEnum.Admin);
    //   }
    // });
  }

// Update the addUser method to set the role directly
  addUser() {
    if (this.formSave.invalid) {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Please fill in all required fields' });
      return;
    }

    // Assign the selected role directly to the form control
    const selectedRole = this.formSave.get('role')!.value;
    this.userService.addUser({ ...this.formSave.value, role: selectedRole }, this.file).subscribe({
      next: data => {
        this.router.navigate(['users']);
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'User successfully added' });
      },
      error: error => {
        this.errorFound = true;
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to add user' });
      }
    });
  }


  onFileChange(event: any) {
        this.file = event.target.files[0];
    }
}
