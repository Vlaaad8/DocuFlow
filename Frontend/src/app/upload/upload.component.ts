import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSidenavModule } from '@angular/material/sidenav';
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { MatIcon } from "@angular/material/icon";
import { CommonModule } from '@angular/common';
import { InputExtractedDataComponent } from "../commons/input-extracted-data/input-extracted-data.component";
import { UploadService } from '../services/upload.service';
import { ExtractedField } from '../model/ExtractedField';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { SnackBarService } from '../services/snackBar.service';
import { ConvertMemoryPipe } from "../pipes/convertMemory.pipe";
import {LoadingComponent} from '../commons/loading/loading.component';


@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, MatIcon, CommonModule, InputExtractedDataComponent, MatProgressSpinnerModule, ExitButtonComponent, ReactiveFormsModule, ConvertMemoryPipe, LoadingComponent]
})
export class UploadComponent implements OnInit {
  isDragOver: boolean = false;
  selectedFile: File | null = null;
  extractedFields: ExtractedField[] = [];
  currentStage: String = 'upload'; // upload, processing, results
  errorMessage: string | null = null;
  formGroup!: FormGroup;

  loggedUser = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}');

  constructor(private service: UploadService, private formBuilder: FormBuilder, private snackBar: SnackBarService) { }

  private importanceMap: { [key: string]: number } = {
    'FirstName': 1,
    'LastName': 2,
    'PersonalNumber': 3,
    'DateOfBirth': 4,
    'Sex': 5,
    'Address': 6,
    'Nationality': 7,
    'DocumentNumber': 8,
    'DocumentDiscriminator': 9,
    'DocumentType': 10,
    'DateOfIssue': 11,
    'DateOfExpiration': 12,
    'PlaceOfIssue': 13,
    'IssuingAuthority': 14,
    'PlaceOfBirth': 15,
    'IssuedBy': 16
  };

  get inputFormFields(): FormArray<FormGroup> {
    return this.formGroup.get('fields') as FormArray;
  }

  private buildFormsForFields(list: ExtractedField[]) {
    this.inputFormFields.clear();
    for (const field of list) {
      this.inputFormFields.push(this.formBuilder.group({
        label: [field.label],
        confidence: [field.confidence],
        value: [field.value],
        sourceOfData: [field.sourceOfData]
      }));
    }
  }

  ngOnInit() {
    this.formGroup = this.formBuilder.group({
      fields: this.formBuilder.array([])
    });

    this.buildFormsForFields(this.extractedFields);
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

  public extractData(): void {
    if (this.selectedFile) {
      this.currentStage = 'processing';
      this.service.extractData(this.selectedFile).subscribe({
        next: (data: ExtractedField[]) => {
          console.log(data);
          this.extractedFields = data;
          this.sortFieldsByImportance();
          this.buildFormsForFields(this.extractedFields);
          this.currentStage = 'results';
        },
        error: (error) => {
          this.errorMessage = 'An error occurred while extracting data. Please try again.';
          this.currentStage = 'upload';
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
  public handleBack(): void {
    this.clear();
  }

  public onSaveData(): void {
    const extractedData: ExtractedField[] = this.inputFormFields.value;
    console.log('Saved extracted data:', extractedData);
    this.service.saveExtractedData(extractedData,this.loggedUser.id);
    this.snackBar.showMessage("Extracted data saved successfully!", "success");
    this.clear();
  }

  public clear(): void {
    this.errorMessage = null;
    this.selectedFile = null;
    this.extractedFields = [];
    this.currentStage = 'upload';
  }

  private sortFieldsByImportance(): void {
    this.extractedFields.sort((a, b) => {

      const rankA = this.importanceMap[a.label] || 99;
      const rankB = this.importanceMap[b.label] || 99;

      return rankA - rankB;
    });
  }
}
