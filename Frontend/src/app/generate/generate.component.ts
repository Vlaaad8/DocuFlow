import { Component, OnInit } from '@angular/core';
import { MatSidenavModule } from "@angular/material/sidenav";
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";
import { TemplateSelectorComponent } from '../commons/template-selector/template-selector.component';
import { CommonModule } from '@angular/common';
import { MatIconModule } from "@angular/material/icon";
import { GenerateTemplate } from '../model/GenerateTemplate';
import { GenerateService } from '../services/generate.service';
import { Field } from '../model/Field';
import { FieldTemplate } from '../model/FieldTemplate';
import { Template } from '../model/Template';

@Component({
  selector: 'app-generate',
  templateUrl: './generate.component.html',
  styleUrls: ['./generate.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, ExitButtonComponent, TemplateSelectorComponent, CommonModule, MatIconModule]
})
export class GenerateComponent implements OnInit {
  isModalOpen: boolean = false;
  templates: GenerateTemplate[] = [];
  templateFields: FieldTemplate[] = [];
  selectedTemplateId: number | null = null;

  constructor(private service: GenerateService) { }

  ngOnInit() {
    this.loadTemplates();
  }

  loadTemplates(): void {
    const user = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}');
    const userID = user.id;
    this.service.getTemplates(userID).subscribe({
      next: (data) => {
        this.templates = data;
        console.log("Templates loaded:", this.templates);
      },
      error: (error) => {
        console.error("Error loading templates:", error);
      }
    });
  }

  loadTemplateFields(templateID: number): void {
    const user = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}');
    const userID = user.id;
    this.service.getTemplateValue(userID, templateID).subscribe({
      next: (data) => {
        this.templateFields = data;
        console.log("Template fields loaded:", this.templateFields);
      },
      error: (error) => {
        console.error("Error loading template fields:", error);
      }
    });
  }


  handleGenerateEvent($event: Template): void {
    this.selectedTemplateId = $event.id;
    this.loadTemplateFields($event.id);
    this.isModalOpen = true;
  }
  handleLeave(): void {
    this.isModalOpen = false;
  }

  generateDocument(): void {
    console.log("Generating document with fields:", this.templateFields);
     const user = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}');
    const userID = user.id;
    this.service.generateDocument(this.selectedTemplateId!,userID).subscribe({
      next: () => {
        this.templateFields = [];
        this.isModalOpen = false;
        this.selectedTemplateId = null;
        
      },
      error: (error) => console.error("Error generating document:", error)
    });
  }

}
