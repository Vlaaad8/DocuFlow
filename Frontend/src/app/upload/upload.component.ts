import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSidenavModule } from '@angular/material/sidenav';
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { MatChip, MatChipsModule, MatChipSelectionChange } from '@angular/material/chips';
import { MatIcon } from "@angular/material/icon";
import { CommonModule } from '@angular/common';
import { InputExtractedDataComponent } from "../commons/input-extracted-data/input-extracted-data.component";
import { UploadService } from '../services/upload.service';
import { ExtractedField } from '../model/ExtractedField';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";

@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, MatChipsModule, MatIcon, CommonModule, InputExtractedDataComponent, MatProgressSpinnerModule, ExitButtonComponent]
})
export class UploadComponent implements OnInit {
  isDragOver: boolean = false;
  selectedFile: File | null = null;
  selectedCategory: string | null = null;
  extractedFields: ExtractedField[] = [];
  currentStage : String = 'upload'; // upload, processing, results

  constructor(private service: UploadService) { }

  ngOnInit() {
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
  onChipSelect($event: MatChipSelectionChange) {
    this.selectedCategory = $event.source.value;
  }

  public extractData(): void {
    if (this.selectedFile && this.selectedCategory) {
      this.currentStage = 'processing';
      this.service.extractData(this.selectedFile, this.selectedCategory).subscribe({
        next: (data : ExtractedField[]) => {
          this.extractedFields = data;
          this.currentStage = 'results';
        },
        error: (error) => {
          console.error('Error extracting data:', error);
        }
      });
    }
  }
  public openFile(): void {
    if (this.selectedFile) {
      const fileURL = URL.createObjectURL(this.selectedFile);
      window.open(fileURL);
    }
  }
}