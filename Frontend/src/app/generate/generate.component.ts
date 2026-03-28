import {Component, OnInit, ViewChild} from '@angular/core';
import {MatSidenavModule} from "@angular/material/sidenav";
import {SidenavUserComponent} from "../commons/sidenav-user/sidenav-user.component";
import {ExitButtonComponent} from "../commons/exit-button/exit-button.component";
import {TemplateSelectorComponent} from '../commons/template-selector/template-selector.component';
import {CommonModule} from '@angular/common';
import {MatIconModule} from "@angular/material/icon";
import {GenerateTemplate, TemplateApprovers} from '../model/GenerateTemplate';
import {GenerateService} from '../services/generate.service';
import {Field} from '../model/Field';
import {FieldTemplate} from '../model/FieldTemplate';
import {Template} from '../model/Template';
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {SnackBarService} from '../services/snackBar.service';
import {MatDialog, MatDialogModule} from '@angular/material/dialog';
import {forkJoin, timestamp} from 'rxjs';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-generate',
  templateUrl: './generate.component.html',
  styleUrls: ['./generate.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, ExitButtonComponent, TemplateSelectorComponent, CommonModule, MatIconModule, MatProgressSpinner, MatDialogModule, FormsModule]
})
export class GenerateComponent implements OnInit {

  templates: GenerateTemplate[] = [];
  templateFields: FieldTemplate[] = [];
  templateApprovers: TemplateApprovers[] = [];
  selectedTemplate: GenerateTemplate | null = null;
  modalStage: string = 'presentation'; // presentation | loading
  dataProfile: { category: string, value: number }[] = [];


  fromDate: string = '';
  toDate: string = '';
  whenDate: string = '';
  currentDate: string = '';

  selectedCategory: string = '';

  private importanceMap: { [key: string]: number } = {
    'First Name': 1,
    'Last Name': 2,
    'Personal Number ': 3,
    'Date of Birth': 4,
    'Sex': 5,
    'Address': 6,
    'Nationality': 7,
    'Document Number': 8,
    'Document Discriminator': 9,
    'Document Type': 10,
    'Document Issue Date ': 11,
    'Document Expiration Date': 12,
    'Place Of Issue': 13,
    'Issuing Authority': 14,
    'Place of Birth': 15,
    'Issued By': 16
  };

  sources: string[] = [];


  constructor(private service: GenerateService, private snackBar: SnackBarService, private dialog: MatDialog) {
  }

  @ViewChild('templateDialog') generateDialog: any;

  ngOnInit() {
    this.loadTemplates();
    this.loadProfileData();
  }

  loadProfileData(): void {
    const user = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}');
    const userID = user.id;
    this.service.getDataProfile(userID).subscribe({
      next: (data) => {
        this.dataProfile = data.filter(item => item.category != "UNKNOWN").sort((a, b) => b.value - a.value);
      },
      error: (error) => {
        console.error("Error loading data profile:", error);
      }
    });
  }

  loadTemplates(): void {
    const user = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}');
    const userID = user.id;
    this.service.getTemplates(userID).subscribe({
      next: (data) => {
        data.sort((a, b) => a.canGenerate === b.canGenerate ? 0 : a.canGenerate ? -1 : 1);
        this.templates = data;
        console.log(data)
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
      fields: this.service.getTemplateValue(userID, templateID,this.selectedCategory),
      approvers: this.service.getTemplateApprovers(templateID, userID)
    }).subscribe({
      next: ({fields, approvers}) => {
        this.templateFields = fields;
        this.sortFieldsByImportance()
        this.templateApprovers = approvers;
        this.modalStage = 'presentation';
      },
      error: (error) => {
        console.error("Error loading template data:", error);
      }
    });

  }


  handleGenerateEvent($event: GenerateTemplate): void {
    this.selectedTemplate = $event;
    const userID = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}').id;

    this.service.getTemplateSources(this.selectedTemplate.template.id, userID).subscribe({
      next: (sources) => {
        this.sources = sources;

        if (this.sources && this.sources.length > 0) {
          this.selectedCategory = this.sources[0];
        }

        // @ts-ignore
        this.loadTemplateData(this.selectedTemplate.template.id);
      },
      error: (error) => console.error("Error loading template sources:", error)
    });
    this.dialog.open(this.generateDialog, {
      width: '900px',
      maxWidth: '95vw'
    });
    this.currentDate = new Date().toISOString().split('T')[0];

  }

  handleLeave(): void {
    this.templateFields = [];
    this.selectedTemplate = null;
    this.dialog.closeAll();
    this.modalStage = 'presentation';
  }

  generateDocument(): void {
    const user = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}');
    const userID = user.id;
    this.modalStage = 'loading';

    const dateValues: { [key: string]: string } = {};


    if (this.containsElement(this.selectedTemplate, 'Date Interval')) {
      dateValues['Date Interval'] = this.fromDate + " - " + this.toDate;
    }

    if (this.containsElement(this.selectedTemplate, 'Specific Date')) {
      dateValues['Specific Date'] = this.whenDate;
    }

    if (this.containsElement(this.selectedTemplate, 'Today\'s Date')) {
      dateValues['Today\'s Date'] = new Date().toISOString().split('T')[0];
    }


    this.service.generateDocument(this.selectedTemplate?.template.id!, userID,  dateValues,this.selectedCategory).subscribe({
      next: () => {
        this.snackBar.showMessage("Document generated successfully!", "success");
        this.handleLeave();
      },
      error: (error) => {
        console.error("Error generating document:", error);
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

  private sortFieldsByImportance(): void {
    this.templateFields.sort((a, b) => {
      const rankA = this.importanceMap[a.name] || 99;
      const rankB = this.importanceMap[b.name] || 99;

      return rankA - rankB;
    });
  }

  public containsElement(template: GenerateTemplate | null, value: string): boolean {
    if (template == null) {
      return false;
    }
    template.dateFields.forEach(field => {
      console.log(field)
    })
    return template.dateFields.some(field => field == value);
  }

  protected readonly timestamp = timestamp;

  handleChange() : void{
    this.service.getTemplateValue(JSON.parse(sessionStorage.getItem('loggedInUser') || '{}').id, this.selectedTemplate?.template.id!, this.selectedCategory).subscribe({
      next: (fields) => {
        this.templateFields = fields;
        this.sortFieldsByImportance();
      },
      error: (error) => {
        console.error("Error loading template data:", error);
      }
    });
  }
}
