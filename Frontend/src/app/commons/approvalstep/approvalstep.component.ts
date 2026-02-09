import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-approvalstep',
  templateUrl: './approvalstep.component.html',
  styleUrls: ['./approvalstep.component.css'],
  imports: [MatIconModule, CommonModule,ReactiveFormsModule]
})
export class ApprovalstepComponent implements OnInit {

  @Input() stepNumber!: number;
  @Input() roleOptions!: string[];
  @Input({ required: true }) group!: FormGroup
  @Output() stepNumberChange = new EventEmitter<number>();

  constructor() { }

  ngOnInit() {
  }
  handleDelete(): void {
    this.stepNumberChange.emit(this.stepNumber);
  }

  get role(): string {
    return this.group.get('role')?.value;
  }


}
