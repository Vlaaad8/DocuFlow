import { Component, Input, OnInit } from '@angular/core';
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";

@Component({
  selector: 'app-loading',
  templateUrl: './loading.component.html',
  styleUrls: ['./loading.component.css'],
  imports: [MatProgressSpinnerModule]
})
export class LoadingComponent implements OnInit {

  @Input({required: true}) message!: string;
  @Input({required: true}) title!: string;

  constructor() { }

  ngOnInit() {
  }

}
