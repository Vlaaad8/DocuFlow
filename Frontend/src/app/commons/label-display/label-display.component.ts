import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatIcon } from "@angular/material/icon";
import { Field } from '../../model/Field';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-label-display',
  templateUrl: './label-display.component.html',
  styleUrls: ['./label-display.component.css'],
  imports: [MatIcon, CommonModule]
})
export class LabelDisplayComponent implements OnInit {

  @Input({ required: true }) field!: Field;
  @Output() dragStarted = new EventEmitter<string>();

  constructor() { }

  ngOnInit() {

  }
  handleDrag(e: DragEvent): void {
    e.dataTransfer?.setData('text/plain', " "+this.field.representation+" ");
    if (e.dataTransfer) {
      e.dataTransfer.effectAllowed = 'copy';
    }

    console.log('dragstart token:', this.field.representation);
  }

}
