import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import {FormatLabelPipe} from '../../pipes/format-label-pipe';

@Component({
  selector: 'app-input-extracted-data',
  templateUrl: './input-extracted-data.component.html',
  styleUrls: ['./input-extracted-data.component.css'],
  imports: [CommonModule, ReactiveFormsModule, FormatLabelPipe],
  providers: [FormatLabelPipe]
})
export class InputExtractedDataComponent implements OnInit {
  @Input({ required: true }) group!: FormGroup


  private  staticInformation : string[] = ["First Name", "Date of Birth", "Place of Birth", "Personal Number","Sex", "Vehicle Classifications"];
  private  volatileInformation: string[] = ["Address", "Nationality", "Last Name"];
  private  documentInformation: string[] = ["Document Number", "Date Of Expiration", "Issuing Authority", "Document Type", "Date Of Issue", "Document Discriminator","Issued By"];


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

  constructor(private formatLabelPipe: FormatLabelPipe) { }

  ngOnInit() {
    this.group.get('value')?.valueChanges.subscribe(() => {
      const label = this.formatLabelPipe.transform(this.label);
      console.log(label)
      if(!this.documentInformation.includes(label)) {
        this.group.get('sourceOfData')?.setValue('manualEntry',{ emitEvent: false });
      }
    });
  }

  public getConfidenceClass(): string {
    if (this.confidenceScore >= 70) {
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
    if (this.confidenceScore >= 70) {
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
      case ("idDocument.driverLicense"):
        return "Driving License";
      case ("idDocument.residencePermit"):
        return "Residence Permit";
      case ("idDocument.usSocialSecurityCard"):
        return "US Social Security Card";
      case ("manualEntry"):
        return "Manual Entry";

      default:
      { console.log(source)
        return "Unknown";}
    }
  }
}
