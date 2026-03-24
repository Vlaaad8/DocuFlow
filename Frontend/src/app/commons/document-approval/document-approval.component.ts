import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {CommonModule, NgClass} from '@angular/common';
import { MatIconModule } from "@angular/material/icon";
import { Approval } from '../../model/Approval';
import {MatProgressBar} from '@angular/material/progress-bar';

@Component({
  selector: 'app-document-approval',
  templateUrl: './document-approval.component.html',
  styleUrls: ['./document-approval.component.css'],
  imports: [MatIconModule, NgClass, MatProgressBar,CommonModule]
})
export class DocumentApprovalComponent implements OnInit ,OnChanges {

  @Input({ required: true }) approval!: Approval
  @Input({required: true}) loading!: boolean;
  @Output() approvalAction = new EventEmitter<{ approvalId: number, action: string }>();
  @Output() previewDocument = new EventEmitter<string>();
  internalLock : boolean = false;
  constructor() { }

  ngOnInit() {
  }
  ngOnChanges(changes: SimpleChanges) {
    console.log('Changes detected in DocumentApprovalComponent:', changes);
   if (changes['loading'].currentValue == false) {
      this.internalLock = false;
   }
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
    this.internalLock = true;
    this.loading = true;
    this.approvalAction.emit({ approvalId: this.approval.id, action: 'ACCEPTED' });
  }

  reject(): void {
    this.internalLock = true;
    this.loading = true;
    this.approvalAction.emit({ approvalId: this.approval.id, action: 'REJECTED' });
  }

  preview(): void {
    this.previewDocument.emit(this.approval.documentPath);
  }
  formatDate(dateString: string): string {
    const date : string  = dateString.split('T')[0];
    const hour : string = dateString.split('T')[1].split('.')[0];
    return date+ ' ' + hour;


  }

}
