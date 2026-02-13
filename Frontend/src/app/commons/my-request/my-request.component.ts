import { Component, Input, OnInit } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatChipEdit } from "@angular/material/chips";
import { CommonModule } from '@angular/common';
import { ApprovalRequest } from '../../model/Approval';

@Component({
  selector: 'app-my-request',
  templateUrl: './my-request.component.html',
  styleUrls: ['./my-request.component.css'],
  imports: [MatIconModule, CommonModule]
})
export class MyRequestComponent implements OnInit {

  @Input({ required: true }) request!: ApprovalRequest;

  constructor() { }

  ngOnInit() {
  }
  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString();


  }
}
