import { Component, OnInit } from '@angular/core';
import { MatSidenavModule } from "@angular/material/sidenav";
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";
import { MatIconModule } from "@angular/material/icon";

@Component({
  selector: 'app-approvalFlow',
  templateUrl: './approvalFlow.component.html',
  styleUrls: ['./approvalFlow.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, ExitButtonComponent, MatIconModule]
})
export class ApprovalFlowComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }
  openCreateModal() : void{
    
  }
  

}
