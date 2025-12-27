import { Component, OnInit } from '@angular/core';
import { MatIcon } from "@angular/material/icon";
import { DragDropModule } from '@angular/cdk/drag-drop';
@Component({
  selector: 'app-label-display',
  templateUrl: './label-display.component.html',
  styleUrls: ['./label-display.component.css'],
  imports: [MatIcon,DragDropModule]
})
export class LabelDisplayComponent implements OnInit {


  constructor() { }

  ngOnInit() {
  }

}
