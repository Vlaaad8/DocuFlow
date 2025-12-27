import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CustomSnackBarComponent } from '../commons/customSnackBar/customSnackBar.component';
import { S } from '@angular/cdk/keycodes';

export interface SnackBarConfig {
  message: string;
  type: 'success' | 'error' | 'info';
}

@Injectable({
  providedIn: 'root'
})
export class SnackBarService {

  constructor(private snackBar: MatSnackBar) { }


  showMessage(message: string, type: string): void {
    this.snackBar.openFromComponent(CustomSnackBarComponent, {
      data: {
        message: message,
        type: type
      },
      duration: 3000,
      panelClass: [`${type}-snackbar`]
    });
  }
}