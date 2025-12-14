import { Component, OnInit } from '@angular/core';
import { MatSidenavModule } from "@angular/material/sidenav";
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";
import { TemplateSelectorComponent } from '../commons/template-selector/template-selector.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-generate',
  templateUrl: './generate.component.html',
  styleUrls: ['./generate.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, ExitButtonComponent,TemplateSelectorComponent,CommonModule]
})
export class GenerateComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
