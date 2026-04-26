import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../services/login.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, ɵInternalFormsSharedModule } from '@angular/forms';
import { MatIcon } from "@angular/material/icon";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { CommonModule } from '@angular/common';
import {WebSocketService} from '../services/notifications.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  imports: [ɵInternalFormsSharedModule, ReactiveFormsModule, MatIcon, MatProgressSpinnerModule,CommonModule]
})
export class LoginComponent implements OnInit {

  constructor(private router: Router,private service: LoginService,private formBuilder: FormBuilder,private notificationsService: WebSocketService) { }
  public loading: boolean = false;
  formGroup!: FormGroup
   errorMessage: string | null = null;

  ngOnInit() {
    this.formGroup = this.formBuilder.group({
      username: [''],
      password: ['']
    });
  }
  onSignUp(): void{
    const formData = this.formGroup.value
    this.loading = true;
    this.service.login(formData.username, formData.password).subscribe({
      next: (user) => {
        if (user != null) {
        sessionStorage.setItem('loggedInUser', JSON.stringify(user));
        this.loading = false;
        this.notificationsService.connect(user.id.toString())
        this.router.navigate(['dashboard']);
      }
      else{
        this.errorMessage = 'Invalid username or password.';
        this.loading = false;
      }
    },
      error: (error) => {
        this.errorMessage = 'An error occurred during login.';
        this.loading = false;
      }
});
  }

}
