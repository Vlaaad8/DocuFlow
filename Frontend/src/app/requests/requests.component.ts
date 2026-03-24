import {Component, OnInit} from '@angular/core';
import {MatSidenavModule} from "@angular/material/sidenav";
import {SidenavUserComponent} from "../commons/sidenav-user/sidenav-user.component";
import {ExitButtonComponent} from "../commons/exit-button/exit-button.component";
import {MatTabsModule} from '@angular/material/tabs';
import {DocumentApprovalComponent} from "../commons/document-approval/document-approval.component";
import {RequestService} from '../services/request.service';
import {Approval, ApprovalRequest} from '../model/Approval';
import {User} from '../model/User';
import {CommonModule} from '@angular/common';
import {MyRequestComponent} from "../commons/my-request/my-request.component";
import {SnackBarService} from '../services/snackBar.service';
import {PdfViewer} from '../commons/pdf-viewer/pdf-viewer';
import {DocumentLoader} from '../services/document-loader';

@Component({
  selector: 'app-requests',
  templateUrl: './requests.component.html',
  styleUrls: ['./requests.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, ExitButtonComponent, MatTabsModule, DocumentApprovalComponent, CommonModule, MyRequestComponent, PdfViewer]
})
export class RequestsComponent implements OnInit {


  approvals: Approval[] = [];
  requests: ApprovalRequest[] = [];
  requestsToDisplay: ApprovalRequest[] = [];
  loading: boolean = false;
  currentFilter: string | null = null;
  documentURL: string = "";
  public user!: User;

  constructor(private service: RequestService, private snackBar: SnackBarService, private documentView: DocumentLoader) {
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
        console.log(data);
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
        this.requestsToDisplay = data;
        this.sortRequests();
      },
      (error) => {
        this.snackBar.showMessage("Error fetching my requests", "error");
      }
    );
  }

  reloadData() {
    this.approvals = this.approvals.filter(r => r.status === "IN_PROGRESS");
  }

  async handleDecision(event: { approvalId: number, action: string }) {
    console.log('Received decision for approval ID:', event.approvalId, 'Action:', event.action);
    this.loading = true;
    let decision: string = '';
    if (event.action === 'ACCEPTED') {
      decision = 'ACCEPTED';
    } else if (event.action === 'REJECTED') {
      decision = 'REJECTED';
    }

    this.service.handleResponseAction(event.approvalId, this.user.id, decision).subscribe({
      next: () => {
        this.modifyApprovalStatus(event.approvalId, decision);
        this.reloadData();
        this.loading = false;
        this.snackBar.showMessage("Action handled successfully!", "success");
      },
      error: (error) => {
        this.loading = false;
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

  private sortRequests() {
    const order: Record<string, number> = {
      PENDING: 0,
      ACCEPTED: 1,
      REJECTED: 2
    };

    this.requestsToDisplay.sort((a, b) => order[a.status] - order[b.status]);
  }

  public handleFilter(status: string | null): void {
    this.currentFilter = status;
    if (status) {
      this.requestsToDisplay = this.requests.filter(request => request.status === status);
    } else {
      this.requestsToDisplay = this.requests;
    }
  }

  public handlePreview(documentPath: string): void {
    this.documentView.loadDocument(documentPath).subscribe({
      next: (blob) => {
        this.documentURL = URL.createObjectURL(blob);
      },
      error: (error) => {
        this.snackBar.showMessage("Error loading document", "error");
      }
    })
  }

  handleViewerClose(): void {
    this.documentURL = "";
  }

}
