import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatIconModule } from "@angular/material/icon";

@Component({
  selector: 'app-template-container',
  templateUrl: './template-container.component.html',
  styleUrls: ['./template-container.component.css'],
  imports: [MatIconModule,CommonModule]
})
export class TemplateContainerComponent implements OnInit {

  fields: String[] = ['Field 1', 'Field 2', 'Field 3', 'Field 4', 'Field 5', 'Field 6', 'Field 7', 'Field 8', 'Field 9', 'Field 10', 'Field 11', 'Field 12'];
  constructor() { }

  ngOnInit() {
  }

}
