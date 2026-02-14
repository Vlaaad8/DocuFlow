import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatIconModule } from "@angular/material/icon";
import { Approval } from '../../model/Approval';

@Component({
  selector: 'app-document-approval',
  templateUrl: './document-approval.component.html',
  styleUrls: ['./document-approval.component.css'],
  imports: [MatIconModule]
})
export class DocumentApprovalComponent implements OnInit {

  @Input({ required: true }) approval!: Approval
  @Output() approvalAction = new EventEmitter<{ approvalId: number, action: string }>();

  constructor() { }

  ngOnInit() {
  }

  formatStatus(status: string): string {
    switch (status) {
      case "IN_PROGRESS":
        return 'In Progress';
      case "APPROVED":
        return 'Approved';
      case "REJECTED":
        return 'Rejected';
      default:
        return 'Pending';

    }
  }
  approve(): void {
    this.approvalAction.emit({ approvalId: this.approval.id, action: 'ACCEPTED' });
  }

  reject(): void {
    this.approvalAction.emit({ approvalId: this.approval.id, action: 'REJECTED' });
  }

  preview(): void {
    console.log('Previewing document for approval ID:', this.approval.id);
  }
  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString();


  }

}
