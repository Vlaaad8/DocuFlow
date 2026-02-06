import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../services/login.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, ɵInternalFormsSharedModule } from '@angular/forms';
import { MatIcon } from "@angular/material/icon";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  imports: [ɵInternalFormsSharedModule, ReactiveFormsModule, MatIcon, MatProgressSpinnerModule,CommonModule]
})
export class LoginComponent implements OnInit {

  constructor(private router: Router,private service: LoginService,private formBuilder: FormBuilder) { }
  public loading: boolean = false;
  formGroup!: FormGroup

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
        console.log('Login successful:', user);
        sessionStorage.setItem('loggedInUser', JSON.stringify(user));
        this.loading = false;
        this.router.navigate(['dashboard']);
      }
      else{
        console.error('Invalid credentials');
        this.loading = false;
      }
    },
      error: (error) => {
        console.error('Login failed:', error);
        this.loading = false;
      }
});
  }
}
