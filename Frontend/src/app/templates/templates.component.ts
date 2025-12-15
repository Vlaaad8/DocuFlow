import { Component, OnInit } from '@angular/core';
import { MatSidenavModule } from "@angular/material/sidenav";
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { MatIcon } from "@angular/material/icon";
import { CommonModule } from '@angular/common';
import { TemplateContainerComponent } from "../commons/template-container/template-container.component";
import { TemplateService } from '../services/template.service';
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";
import { Template } from '../model/Template';
import { MatProgressSpinner } from "@angular/material/progress-spinner";
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CdkObserveContent } from "@angular/cdk/observers";

@Component({
  selector: 'app-templates',
  templateUrl: './templates.component.html',
  styleUrls: ['./templates.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, MatIcon, CommonModule, TemplateContainerComponent, ExitButtonComponent, MatProgressSpinner, ReactiveFormsModule]
})
export class TemplatesComponent implements OnInit {
  selectedFile: File | null = null;
  isModalOpen: boolean = false;
  isDragOver: boolean = false;
  isTemplateValid: boolean = false;
  templates: Template[] = [];
  templateCategories!: string[];
  formGroup!: FormGroup
  errorMessage: string | null = null;
  constructor(private service: TemplateService,private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.loadTemplates();
    this.initializeForm();
  }

  openUploadModal() {
    this.isModalOpen = true
}

onDrop($event: DragEvent) {
    $event.preventDefault();
    $event.stopPropagation();
    this.isDragOver = false;
    const files = $event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.selectedFile = files[0];
    }
  }
  onDragLeave($event: DragEvent) {
    $event.preventDefault();
    $event.stopPropagation();
    this.isDragOver = false;
  }
  onDragOver($event: DragEvent) {
    $event.preventDefault();
    $event.stopPropagation();
    this.isDragOver = true;
  }

    handleFileSelected($event: Event) {
    const input = $event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.validateData();
    } else {
      this.selectedFile = null;
    }
  }

  validateData(): void {

    this.service.validateTemplate(this.selectedFile!).subscribe({
      next: (response : Template) => {
        this.isTemplateValid = true;
      },
      error: (error) => {
        this.errorMessage = error.error;
        this.isTemplateValid = false;
        this.selectedFile = null;
        console.error('Error validating template:', error);
      }
    });
  }

  public loadTemplates(): void {
    this.service.getTemplates().subscribe({
      next: (response) => {
        this.templates = response;
        console.log('Templates loaded successfully:', response);
      },
      error: (error) => {
        console.error('Error loading templates:', error);
      }
    });
    this.loadTemplateCategories();
  }
  handleTemplateEvenet(action: String, template: Template): void{
    if(action === 'Delete'){
      console.log("Deleting template: ", template);
      this.service.deleteTemplate(template.id).subscribe({next: () => {
        this.templates = this.templates.filter(t => t.id !== template.id);
      },
      error: (error) => {
        console.error('Error deleting template:', error);
      }});
    }
    else if(action === 'Search'){
      console.log("Previewing template: ", template);
    }
    else{
      console.log("Unknown action: ", action);
    }
  }

  loadTemplateCategories(): void {
    this.service.getTemplateCategories().subscribe({
      next: (response) => {
        this.templateCategories = response;
        console.log('Template categories loaded successfully:', response);
      },
      error: (error) => {
        console.error('Error loading template categories:', error);
      }
    });
  }
  initializeForm(): void {
    this.formGroup = this.formBuilder.group({
      name: [''],
      category: [''],
      description: ['']
    });
  }

  submitTemplate(): void {
    const formData = this.formGroup.value;
    console.log('Submitting template with data:', formData);
   this.service.uploadTemplate(this.selectedFile!, formData.name, formData.category, formData.description).subscribe({next: (response) => {
      this.isModalOpen = false;
      this.selectedFile = null;
      this.isTemplateValid = false;
      this.errorMessage = null;
      this.loadTemplates();
    },
    error: (error) => {
      this.errorMessage = 'An error occurred while uploading the template. Please try again.';
      console.error('Error uploading template:', error);
    }});
  }

  public handleLeave(): void {
    this.isModalOpen = false;
    this.selectedFile = null;
    this.isTemplateValid = false;
    this.errorMessage = null;
  }
}
