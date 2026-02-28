import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSidenavModule } from "@angular/material/sidenav";
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";
import { TemplateSelectorComponent } from '../commons/template-selector/template-selector.component';
import { CommonModule } from '@angular/common';
import { MatIconModule } from "@angular/material/icon";
import { GenerateTemplate, TemplateApprovers } from '../model/GenerateTemplate';
import { GenerateService } from '../services/generate.service';
import { Field } from '../model/Field';
import { FieldTemplate } from '../model/FieldTemplate';
import { Template } from '../model/Template';
import { MatProgressSpinner } from "@angular/material/progress-spinner";
import { SnackBarService } from '../services/snackBar.service';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-generate',
  templateUrl: './generate.component.html',
  styleUrls: ['./generate.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, ExitButtonComponent, TemplateSelectorComponent, CommonModule, MatIconModule, MatProgressSpinner, MatDialogModule]
})
export class GenerateComponent implements OnInit {

  templates: GenerateTemplate[] = [];
  templateFields: FieldTemplate[] = [];
  templateApprovers: TemplateApprovers[] = [];
  selectedTemplateId: number | null = null;
  modalStage: string = 'presentation'; // presentation | loading 


  constructor(private service: GenerateService, private snackBar: SnackBarService, private dialog: MatDialog) { }

  @ViewChild('templateDialog') generateDialog: any;

  ngOnInit() {
    this.loadTemplates();
  }

  loadTemplates(): void {
    const user = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}');
    const userID = user.id;
    this.service.getTemplates(userID).subscribe({
      next: (data) => {
        data.sort((a, b) => a.canGenerate === b.canGenerate ? 0 : a.canGenerate ? -1 : 1);
        this.templates = data;
        console.log("Templates loaded:", this.templates);
      },
      error: (error) => {
        console.error("Error loading templates:", error);
      }
    });
  }

  loadTemplateData(templateID: number): void {
    const user = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}');
    const userID = user.id;
    this.modalStage = 'loading';
    forkJoin({
      fields: this.service.getTemplateValue(userID, templateID),
      approvers: this.service.getTemplateApprovers(templateID, userID)
    }).subscribe({
      next: ({ fields, approvers }) => {
        this.templateFields = fields;
        this.templateApprovers = approvers;
        this.modalStage = 'presentation';
        console.log("Template data loaded:", { fields, approvers });
      },
      error: (error) => {
        console.error("Error loading template data:", error);
      }
    });

  }


  handleGenerateEvent($event: Template): void {
    this.selectedTemplateId = $event.id;
    this.loadTemplateData(this.selectedTemplateId);
    this.dialog.open(this.generateDialog);
  }
  handleLeave(): void {
    this.templateFields = [];
    this.selectedTemplateId = null;
    this.dialog.closeAll();
    this.modalStage = 'presentation';
  }

  generateDocument(): void {
    const user = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}');
    const userID = user.id;
    this.modalStage = 'loading';
    this.service.generateDocument(this.selectedTemplateId!, userID).subscribe({
      next: () => {
        this.handleLeave();
        this.snackBar.showMessage("Document generated successfully!", "success");

      },
      error: (error) => {
        this.snackBar.showMessage("Error generating document. Please try again.", "error");
        this.handleLeave();
      }

    });
  }
  getRoleAbbreviation(role: string): string {
    const words = role.split(' ');
    if (words.length === 1) {
      return role.substring(0, 1).toUpperCase();
    } else {
      return words.map(word => word.charAt(0).toUpperCase()).join('');
    }
  }
  formatSourceOfData(source: string): string {
    switch (source) {
      case ("NATIONAL_IDENTITY_CARD"):
        return "ID Card";
      case ("PASSPORT"):
        return "Passport";
      case ("DRIVER_LICENSE"):
        return "Driving License";
      case ("RESIDENCE_PERMIT"):
        return "Residence Permit";
      case ("SOCIAL_SECURITY_CARD"):
        return "US Social Security Card";
      case ("MANUAL_ENTRY"):
        return "Manual Entry";

      default:
        return "Unknown";
    }
  }
}
