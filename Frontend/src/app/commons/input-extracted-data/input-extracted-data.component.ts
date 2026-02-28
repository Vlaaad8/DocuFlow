import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-input-extracted-data',
  templateUrl: './input-extracted-data.component.html',
  styleUrls: ['./input-extracted-data.component.css'],
  imports: [CommonModule, ReactiveFormsModule]
})
export class InputExtractedDataComponent implements OnInit {
  @Input({ required: true }) group!: FormGroup

  get label() {
    return this.group.get('label')?.value;
  }
  get confidenceScore() {
    return this.group.get('confidence')?.value;
  }
  get extractedValue() {
    return this.group.get('value')?.value;
  }
  get sourceOfData() {
    return this.group.get('sourceOfData')?.value;
  }

  constructor() { }

  ngOnInit() {
    this.group.get('value')?.valueChanges.subscribe(() => {
      this.group.get('sourceOfData')?.setValue('manualEntry');
    });
  }

  public getConfidenceClass(): string {
    if (this.confidenceScore >= 80) {
      return 'valid';
    }
    else if (this.confidenceScore >= 50) {
      return 'warning';
    }
    else {
      return 'invalid';
    }
  }
  getInputConfidenceClass(): string {
    if (this.confidenceScore >= 80) {
      return 'input-valid';
    }
    else if (this.confidenceScore >= 50) {
      return 'warning-input';
    }
    else {
      return 'invalid-input';
    }
  }

  formatSourceOfData(source: string): string {
    switch (source) {
      case ("idDocument.nationalIdentityCard"):
        return "ID Card";
      case ("idDocument.passport"):
        return "Passport";
      case ("idDocument.drivingLicense"):
        return "Driving License";
      case ("idDocument.residencePermit"):
        return "Residence Permit";
      case ("idDocument.usSocialSecurityCard"):
        return "US Social Security Card";
      case ("manualEntry"):
        return "Manual Entry";

      default:
        return "Unknown";
    }
  }
}
