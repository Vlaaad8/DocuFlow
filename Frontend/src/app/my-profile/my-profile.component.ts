import { Component, OnInit } from '@angular/core';
import { MatSidenavModule } from "@angular/material/sidenav";
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from "@angular/material/icon";
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { ProfileService } from '../services/profile.service';
import { UserCertificate } from '../model/user-certificate';
import { User } from '../model/User';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-my-profile',
  templateUrl: './my-profile.component.html',
  styleUrls: ['./my-profile.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, ExitButtonComponent, MatTabsModule, MatIconModule, MatProgressBarModule, CommonModule]
})
export class MyProfileComponent implements OnInit {

  public certificate: UserCertificate | null = null;
  private userID: number = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}').id;
  public user: User = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}');
  isDragOver: boolean = false;
  selectedFile: File | null = null;
  errorMessage: string | null = null;
  constructor(private service: ProfileService) { }

  ngOnInit() {
    this.service.getUserCertificate(this.userID).subscribe({
      next: (cert) => {
        this.certificate = cert;
        console.log('Certificate fetched successfully:', cert);
      },
      error: (error) => {
        console.error('Error fetching certificate:', error);
      }
    });
  }

  determineProcent(): number {
    if (!this.certificate) {
      return 0;
    }
    return (this.certificate.daysLeft / 730) * 100;

  }

  onDrop($event: DragEvent) {
    $event.preventDefault();
    $event.stopPropagation();
    this.isDragOver = false;
    const files = $event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.selectedFile = files[0];
    }
  }
  onDragLeave($event: DragEvent) {
    $event.preventDefault();
    $event.stopPropagation();
    this.isDragOver = false;
  }
  onDragOver($event: DragEvent) {
    $event.preventDefault();
    $event.stopPropagation();
    this.isDragOver = true;
  }
  handleFileSelected($event: Event) {
    const input = $event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.extractData();
    } else {
      this.selectedFile = null;
    }
  }

  extractData() {
    if (this.selectedFile) {
      this.service.extractData(this.selectedFile).subscribe({
        next: (signatures) => {
          console.log('Signatures extracted:', signatures);
        },
        error: (error) => {
          console.error('Error extracting signatures:', error);
          this.errorMessage = 'Failed to extract signatures from the document.';
        }
      });
    }
  }
}
