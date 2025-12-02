import { I } from '@angular/cdk/keycodes';
import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { MatIconModule } from "@angular/material/icon";
import { Template } from '../../model/Template';

@Component({
  selector: 'app-template-container',
  templateUrl: './template-container.component.html',
  styleUrls: ['./template-container.component.css'],
  imports: [MatIconModule,CommonModule]
})
export class TemplateContainerComponent implements OnInit {
  @Input({required: true}) template!: Template;
 
  constructor() { }

  ngOnInit() {
  }

}
