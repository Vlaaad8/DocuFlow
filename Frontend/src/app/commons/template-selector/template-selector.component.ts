import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatIconModule } from "@angular/material/icon";
import { Template } from '../../model/Template';

@Component({
  selector: 'app-template-selector',
  templateUrl: './template-selector.component.html',
  styleUrls: ['./template-selector.component.css'],
  imports: [MatIconModule,CommonModule]
})
export class TemplateSelectorComponent implements OnInit {

  // @Input({required: true}) template! : Template
  @Output() event = new EventEmitter<Template>();

  constructor() { }

  ngOnInit() {
  }

}
