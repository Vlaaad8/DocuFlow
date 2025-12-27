import { Component, Inject, OnInit } from '@angular/core';
import { MAT_SNACK_BAR_DATA, MatSnackBarLabel, MatSnackBarAction, MatSnackBarRef } from '@angular/material/snack-bar';
import { SnackBarConfig } from '../../services/snackBar.service';
import { CommonModule } from '@angular/common';
import { MatIcon } from "@angular/material/icon";

@Component({
  selector: 'customSnackBar',
  templateUrl: './customSnackBar.component.html',
  styleUrls: ['./customSnackBar.component.css'],
  imports: [MatSnackBarAction, CommonModule, MatIcon]
})
export class CustomSnackBarComponent  {

  constructor(@Inject(MAT_SNACK_BAR_DATA) public data: SnackBarConfig, public snackBarRef: MatSnackBarRef<CustomSnackBarComponent>) { }

  onClose() : void{
    this.snackBarRef.dismiss();
  }

}
