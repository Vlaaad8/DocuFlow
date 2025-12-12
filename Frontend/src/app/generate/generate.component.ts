import { Component, OnInit } from '@angular/core';
import { MatSidenavModule } from "@angular/material/sidenav";
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";

@Component({
  selector: 'app-generate',
  templateUrl: './generate.component.html',
  styleUrls: ['./generate.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, ExitButtonComponent]
})
export class GenerateComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
