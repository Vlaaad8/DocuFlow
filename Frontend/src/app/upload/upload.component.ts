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
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { C } from '@angular/cdk/keycodes';

@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, MatChipsModule, MatIcon, CommonModule, InputExtractedDataComponent, MatProgressSpinnerModule, ExitButtonComponent, ReactiveFormsModule]
})
export class UploadComponent implements OnInit {
  isDragOver: boolean = false;
  selectedFile: File | null = null;
  selectedCategory: string | null = null;
  extractedFields: ExtractedField[] = [];
  currentStage: String = 'results'; // upload, processing, results
  errorMessage: string | null = null;
  formGroup!: FormGroup;
  extractedFieldsMock: ExtractedField[] = [
    {
      label: "vendor_name",
      value: "Dedeman SRL",
      confidence: 98
    },
    {
      label: "invoice_number",
      value: "F-2024001293",
      confidence: 95
    },
    {
      label: "invoice_date",
      value: "2024-05-15",
      confidence: 0.96
    },
    {
      label: "total_amount",
      value: "450.00",
      confidence: 2
    },
    {
      label: "currency",
      value: "RON",
      confidence: 79
    }
  ];
  constructor(private service: UploadService, private formBuilder: FormBuilder) { }

  get inputFormFields(): FormArray<FormGroup> {
    return this.formGroup.get('fields') as FormArray;
  }

  private buildFormsForFields(list: ExtractedField[]) {
    this.inputFormFields.clear();
    for (const field of list) {
      this.inputFormFields.push(this.formBuilder.group({
        label: [field.label],
        confidence: [field.confidence],
        value: [field.value]
      }));
    }
  }

  ngOnInit() {  this.formGroup = this.formBuilder.group({
    fields: this.formBuilder.array([])
  });

  // doar pentru test cât timp folosești mock:
  this.buildFormsForFields(this.extractedFieldsMock);}

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
        next: (data: ExtractedField[]) => {
          this.extractedFields = data;
          this.currentStage = 'results';
        },
        error: (error) => {
          this.errorMessage = 'An error occurred while extracting data. Please try again.';
          this.currentStage = 'upload';
          this.selectedCategory = null;
          console.error('Error extracting data:', error);
        }
      });
    }
    else if (!this.selectedCategory) {
      this.errorMessage = 'Please select a document category before uploading.';
      this.selectedFile = null;
    }
  }
  public openFile(): void {
    if (this.selectedFile) {
      const fileURL = URL.createObjectURL(this.selectedFile);
      window.open(fileURL);
    }
  }
  public handleBack(): void {
    this.currentStage = 'upload';
    this.selectedFile = null;
    this.selectedCategory = null;
    this.extractedFields = [];
  }

  public onSaveData(): void {
    const extractedData : ExtractedField[] = this.inputFormFields.value;
    console.log('Saved extracted data:', extractedData);
    // Here you can add logic to send the extractedData to a backend or process it further
  }
}