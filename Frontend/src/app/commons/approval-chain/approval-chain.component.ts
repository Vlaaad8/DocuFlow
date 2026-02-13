import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { ApprovalChain } from '../../model/Approval';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-approval-chain',
  templateUrl: './approval-chain.component.html',
  styleUrls: ['./approval-chain.component.css'],
  imports: [MatIconModule,CommonModule]
})
export class ApprovalChainComponent implements OnInit {

  @Output() remove = new EventEmitter<number>();
  @Input({required: true}) approvalChain!: ApprovalChain

  constructor() { }

  ngOnInit() {
  }

  getRoleAbbreviation(role: string): string {
    const words = role.split(' ');
    if (words.length === 1) {
      return role.substring(0, 1).toUpperCase();
    } else {
      return words.map(word => word.charAt(0).toUpperCase()).join('');
    }
  }
  handleDelete() : void{
    this.remove.emit(this.approvalChain.id);
  }

}
