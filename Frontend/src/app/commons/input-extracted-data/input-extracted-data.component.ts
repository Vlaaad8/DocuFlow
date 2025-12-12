import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-input-extracted-data',
  templateUrl: './input-extracted-data.component.html',
  styleUrls: ['./input-extracted-data.component.css'],
  imports: [CommonModule,ReactiveFormsModule]
})
export class InputExtractedDataComponent implements OnInit {
    @Input({required: true}) group!: FormGroup

    get label(){
      return this.group.get('label')?.value;
    }
    get confidenceScore(){
      return this.group.get('confidence')?.value;
    }
    get extractedValue(){
      return this.group.get('value')?.value;
    }

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
