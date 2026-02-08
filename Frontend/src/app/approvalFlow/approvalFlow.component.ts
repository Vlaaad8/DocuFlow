import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSidenavModule } from "@angular/material/sidenav";
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";
import { MatIconModule } from "@angular/material/icon";
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from "@angular/material/expansion";

@Component({
  selector: 'app-approvalFlow',
  templateUrl: './approvalFlow.component.html',
  styleUrls: ['./approvalFlow.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, ExitButtonComponent, MatIconModule, MatDialogModule, MatExpansionModule]
})
export class ApprovalFlowComponent implements OnInit {

  @ViewChild('createFlowDialog') createFlowDialog: any;

  constructor(private dialog: MatDialog) { }

  ngOnInit() {
  }
  openCreateModal() : void{
    this.dialog.open(this.createFlowDialog);
  }
  closeCreateModal() : void{
    this.dialog.closeAll();
  }
  

}
