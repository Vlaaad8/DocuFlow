import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSidenavModule } from "@angular/material/sidenav";
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";
import { MatIconModule } from "@angular/material/icon";
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from "@angular/material/expansion";
import { ApprovalstepComponent } from "../commons/approvalstep/approvalstep.component";
import { CommonModule } from '@angular/common';
import { ApprovalFlowService } from '../services/approvalFlow.service';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ApprovalChainStep } from '../model/Approval';
import { ApprovalChainComponent } from "../commons/approval-chain/approval-chain.component";

@Component({
  selector: 'app-approvalFlow',
  templateUrl: './approvalFlow.component.html',
  styleUrls: ['./approvalFlow.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, ExitButtonComponent, MatIconModule, MatDialogModule, MatExpansionModule, ApprovalstepComponent, CommonModule, ReactiveFormsModule, ApprovalChainComponent]
})
export class ApprovalFlowComponent implements OnInit {

  @ViewChild('createFlowDialog') createFlowDialog: any;

  formGroup!: FormGroup;
  constructor(private dialog: MatDialog, private service: ApprovalFlowService, private formBuilder: FormBuilder) { }
  public roles!: string[];

  ngOnInit() {
    this.service.getRoles().subscribe({
      next: (response) => {
        this.roles = response;
      },
      error: (error) => {
        console.log(error);
      }
    })
    this.formGroup = this.formBuilder.group({
      name: [''],
      steps: this.formBuilder.array([])
    });

    this.createFormsForSteps([]);

  }
  get inputFormSteps(): FormArray<FormGroup> {
    return this.formGroup.get('steps') as FormArray;
  }

  createFormsForSteps(list: ApprovalChainStep[]): void {
    this.inputFormSteps.clear();
    for (const step of list) {
      this.inputFormSteps.push(this.formBuilder.group({
        id: [0],
        role: [step.role],
        order: [step.order],
        approvalChain: [0]
      }));
    }
  }

  openCreateModal(): void {
    this.dialog.open(this.createFlowDialog);
  }
  closeCreateModal(): void {
    this.dialog.closeAll();
  }
  addStep(): void {
    this.inputFormSteps.push(this.formBuilder.group({
      role: [null],
    }));
  }
  handleStepNumberChange(stepNumber: number): void {
    this.inputFormSteps.removeAt(stepNumber - 1);
    for (let i = stepNumber - 1; i < this.inputFormSteps.length; i++) {
      const currentOrder = this.inputFormSteps.at(i).get('order')?.value;
      this.inputFormSteps.at(i).get('order')?.setValue(currentOrder - 1);
    }

  }

  saveFlow(): void {
    //console.log(this.formGroup.value);
    this.service.saveFlow(this.formGroup.value).subscribe({
      next: () => {
        console.log('Flow saved successfully');
        this.closeCreateModal();
      },
      error: (error) => {
        console.error('Error saving flow:', error);
      }
    });
  }
}
