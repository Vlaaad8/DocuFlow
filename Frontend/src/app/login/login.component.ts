import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../services/login.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, ɵInternalFormsSharedModule } from '@angular/forms';
import { MatIcon } from "@angular/material/icon";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  imports: [ɵInternalFormsSharedModule, ReactiveFormsModule, MatIcon]
})
export class LoginComponent implements OnInit {

  constructor(private router: Router,private service: LoginService,private formBuilder: FormBuilder) { }

  formGroup!: FormGroup

  ngOnInit() {
    this.formGroup = this.formBuilder.group({
      username: [''],
      password: ['']
    });
  }
  onSignUp(): void{
    const formData = this.formGroup.value 
    this.service.login(formData.username, formData.password).subscribe({
      next: (user) => {
        if (user != null) {
        console.log('Login successful:', user);
        // You can store the user data in a service or local storage as needed
        sessionStorage.setItem('loggedInUser', JSON.stringify(user));
        this.router.navigate(['upload']);
      }
      else{
        console.error('Invalid credentials');
      }
    },
      error: (error) => {
        console.error('Login failed:', error);
  }
});
  }
}
