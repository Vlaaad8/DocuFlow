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

@Component({
  selector: 'app-templates',
  templateUrl: './templates.component.html',
  styleUrls: ['./templates.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, MatIcon, CommonModule, TemplateContainerComponent, ExitButtonComponent, MatProgressSpinner]
})
export class TemplatesComponent implements OnInit {
  selectedFile: File | null = null;
  isModalOpen: boolean = false;
  isDragOver: boolean = false;
  templates: Template[] = [];
  constructor(private service: TemplateService) { }

  ngOnInit() {
    this.loadTemplates();
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
      this.extractData();
    } else {
      this.selectedFile = null;
    }
  }

  extractData(): void {

    this.service.uploadTemplate(this.selectedFile!, 'templateName').subscribe({
      next: (response : Template) => {
        this.isModalOpen = false;
        this.templates.push(response);
      },
      error: (error) => {
        console.error('Error uploading template:', error);
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
}
