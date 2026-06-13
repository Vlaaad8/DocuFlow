import {I} from '@angular/cdk/keycodes';
import {CommonModule} from '@angular/common';
import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {Template} from '../../model/Template';
import {ApprovalChain} from '../../model/Approval';
import {TemplateService} from '../../services/template.service';

@Component({
  selector: 'app-template-container',
  templateUrl: './template-container.component.html',
  styleUrls: ['./template-container.component.css'],
  imports: [MatIcon, CommonModule]
})
export class TemplateContainerComponent implements OnInit {
  @Input({required: true}) template!: Template;
  @Output() event = new EventEmitter<String>();
  @Output() preview = new EventEmitter<number>();
  approvalChain!: ApprovalChain
  isLoadingPreview: boolean = false;

  constructor(private service: TemplateService) {
  }

  ngOnInit() {
    this.service.getTemplateChain(this.template.id).subscribe({
      next: (chain) => {
        this.approvalChain = chain;
      },
      error: (err) => {
        console.error('Error fetching approval chain:', err);
      }
    })
  }

  handleClick(action: String): void {
    this.event.emit(action);
  }

  getRoleAbbreviation(role: string): string {
    const words = role.split(' ');
    if (words.length === 1) {
      return role.substring(0, 1).toUpperCase();
    } else {
      return words.map(word => word.charAt(0).toUpperCase()).join('');
    }
  }
  handlePreview(): void {
    this.isLoadingPreview = true;
    this.preview.emit(this.template.id);
    // Reseta loading-ul după 3 secunde (sau puteți reseta din componenta parent)
    setTimeout(() => {
      this.isLoadingPreview = false;
    }, 3000);
  }

}
