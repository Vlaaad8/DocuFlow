import { Component, OnInit } from '@angular/core';
import { MatSidenavModule } from "@angular/material/sidenav";
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";
import { MatTabsModule } from '@angular/material/tabs';
import { A11yModule } from "@angular/cdk/a11y";
import { DocumentApprovalComponent } from "../commons/document-approval/document-approval.component";

@Component({
  selector: 'app-requests',
  templateUrl: './requests.component.html',
  styleUrls: ['./requests.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, ExitButtonComponent, MatTabsModule, A11yModule, DocumentApprovalComponent]
})
export class RequestsComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
