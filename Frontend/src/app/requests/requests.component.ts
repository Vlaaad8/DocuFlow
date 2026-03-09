import { Component, OnInit } from '@angular/core';
import { MatSidenavModule } from "@angular/material/sidenav";
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";
import { MatTabsModule } from '@angular/material/tabs';
import { DocumentApprovalComponent } from "../commons/document-approval/document-approval.component";
import { RequestService } from '../services/request.service';
import { Approval, ApprovalRequest } from '../model/Approval';
import { User } from '../model/User';
import { CommonModule } from '@angular/common';
import { MyRequestComponent } from "../commons/my-request/my-request.component";
import { SnackBarService } from '../services/snackBar.service';

@Component({
  selector: 'app-requests',
  templateUrl: './requests.component.html',
  styleUrls: ['./requests.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, ExitButtonComponent, MatTabsModule, DocumentApprovalComponent, CommonModule, MyRequestComponent]
})
export class RequestsComponent implements OnInit {


  approvals: Approval[] = [];
  requests: ApprovalRequest[] = [];

  public user!: User;

  constructor(private service: RequestService,private snackBar: SnackBarService) {
  }

  ngOnInit() {
    this.user = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}');
    this.loadApprovals();
    this.loadMyRequests();
  }

  loadApprovals() {

    this.service.getReqestsTOApprove(this.user.id).subscribe(
      (data: Approval[]) => {
        this.approvals = data;
      },
      (error) => {
        this.snackBar.showMessage("Error fetching approvals", "error");
      }
    );
  }

  loadMyRequests() {

    this.service.getMyRequests(this.user.id).subscribe(
      (data: ApprovalRequest[]) => {
        this.requests = data;
      },
      (error) => {
        this.snackBar.showMessage("Error fetching my requests", "error");
      }
    );
  }

  reloadData() {
    this.approvals = this.approvals.filter(r => r.status === "IN_PROGRESS");
  }

  handleDecision(event: { approvalId: number, action: string }) {
    console.log('Received decision for approval ID:', event.approvalId, 'Action:', event.action);

    let decision: string = '';
    if (event.action === 'ACCEPTED') {
      decision = 'ACCEPTED';
    }
    else if (event.action === 'REJECTED') {
      decision = 'REJECTED';
    }
    this.service.handleResponseAction(event.approvalId, this.user.id, decision).subscribe({
      next: () => {
        this.modifyApprovalStatus(event.approvalId, decision);
        this.reloadData();
        this.snackBar.showMessage("Action handled successfully!", "success");
      },
      error: (error) => {
        this.snackBar.showMessage("Error handling action", "error");
      }
    });


  }

  private modifyApprovalStatus(requestId: number, newStatus: string): void {
    const requestIndex = this.approvals.findIndex(r => r.id === requestId);
    if (requestIndex !== -1) {
      this.approvals[requestIndex].status = newStatus;
    }
  }
}
