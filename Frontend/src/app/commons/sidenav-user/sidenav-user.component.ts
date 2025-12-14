import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbar, MatToolbarModule } from '@angular/material/toolbar';
import { Router } from '@angular/router';
import { User } from '../../model/User';

@Component({
  selector: 'app-sidenav-user',
  templateUrl: './sidenav-user.component.html',
  styleUrls: ['./sidenav-user.component.css'],
  imports: [MatToolbarModule,MatIconModule,CommonModule]
})
export class SidenavUserComponent implements OnInit {

  @Input() selectedUploadTab: boolean = false;
  @Input() selectedGenerateTab: boolean = false;
  @Input() selectedDashboardTab: boolean = false;
  @Input() selectedTemplateTab: boolean = false;

  user!: User;
  
  constructor(private router: Router) { }

  ngOnInit() {
    sessionStorage.getItem('loggedInUser')
    this.user = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}');
  }
  handleGenerate() : void { 
      this.router.navigate(['generate']);
  }
  handleDashboard() : void {  
      this.router.navigate(['dashboard']);
  }
  handleUpload() : void {
      this.router.navigate(['upload']);
  }
  handleTemplate() : void {
      this.router.navigate(['template']);
  }

}
