import { I } from '@angular/cdk/keycodes';
import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatIcon } from "@angular/material/icon";
import { Template } from '../../model/Template';

@Component({
  selector: 'app-template-container',
  templateUrl: './template-container.component.html',
  styleUrls: ['./template-container.component.css'],
  imports: [MatIcon,CommonModule]
})
export class TemplateContainerComponent implements OnInit {
  @Input({required: true}) template!: Template;
  @Output() event = new EventEmitter<String>();
 
  constructor() { }

  ngOnInit() {
  }
  handleClick(action: String): void{
    this.event.emit(action);
  }

}
