import { Component, OnInit } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatNavList } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule, MatToolbar } from '@angular/material/toolbar';
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  imports: [MatSidenavModule, MatIconModule, SidenavUserComponent]
})
export class DashboardComponent implements OnInit {
  
  constructor() { }

  ngOnInit() {
  }

}
