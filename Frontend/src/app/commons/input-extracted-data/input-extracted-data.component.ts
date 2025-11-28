import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-input-extracted-data',
  templateUrl: './input-extracted-data.component.html',
  styleUrls: ['./input-extracted-data.component.css'],
  imports: [CommonModule]
})
export class InputExtractedDataComponent implements OnInit {
  @Input({ required: true }) extractedValue!: string;
  @Input({ required: true }) confidenceScore!: number;
  @Input({ required: true }) fieldName!: string;
  
  constructor() { }

  ngOnInit() {
  }

  public getConfidenceClass(): string{
    if(this.confidenceScore >= 80){
      return 'valid';
    }
    else if(this.confidenceScore >= 50){
      return 'warning';
    }
    else{
      return 'invalid';
    }
  }
  getInputConfidenceClass(): string{
    if(this.confidenceScore >= 80){
      return 'input-valid';
    }
    else if(this.confidenceScore >= 50){
      return 'warning-input';
    }
    else{
      return 'invalid-input';
    }
  }

}
