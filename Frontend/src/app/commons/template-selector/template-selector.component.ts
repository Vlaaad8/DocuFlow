import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatIconModule } from "@angular/material/icon";
import { Template } from '../../model/Template';
import { GenerateTemplate } from '../../model/GenerateTemplate';

@Component({
  selector: 'app-template-selector',
  templateUrl: './template-selector.component.html',
  styleUrls: ['./template-selector.component.css'],
  imports: [MatIconModule,CommonModule]
})
export class TemplateSelectorComponent implements OnInit {

  @Input() schema! : GenerateTemplate;
  @Output() event = new EventEmitter<Template>();

  constructor() { }

  ngOnInit() {
  }
  handleGenerate(): void {
    this.event.emit(this.schema.template);
  }

}
