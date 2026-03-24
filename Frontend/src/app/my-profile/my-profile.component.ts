import { Component, OnInit } from '@angular/core';
import { MatSidenavModule } from "@angular/material/sidenav";
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";
import { MatTabsModule } from '@angular/material/tabs';
import { MatIconModule } from "@angular/material/icon";
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { ProfileService } from '../services/profile.service';
import { SignatureInfo, UserCertificate } from '../model/user-certificate';
import { User } from '../model/User';
import { CommonModule } from '@angular/common';
import {LoadingComponent} from '../commons/loading/loading.component';
import {PdfViewer} from '../commons/pdf-viewer/pdf-viewer';

@Component({
  selector: 'app-my-profile',
  templateUrl: './my-profile.component.html',
  styleUrls: ['./my-profile.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, ExitButtonComponent, MatTabsModule, MatIconModule, MatProgressBarModule, CommonModule, LoadingComponent, PdfViewer]
})
export class MyProfileComponent implements OnInit {

  public certificate: UserCertificate | null = null;
  private userID: number = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}').id;
  public user: User = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}');

  isDragOver: boolean = false;
  selectedFile: File | null = null;
  errorMessage: string | null = null;


  stage: 'none' | 'loading' = 'none';

  signatures: SignatureInfo[] = [];

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
      this.setSelectedFile(files[0]);
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
      this.setSelectedFile(input.files[0]);
      // allow re-selecting the same file again
      input.value = '';
    }
  }

  private setSelectedFile(file: File) {
    this.errorMessage = null;

    // basic validation (PDF only)
    const isPdf = file.type === 'application/pdf' || file.name.toLowerCase().endsWith('.pdf');
    if (!isPdf) {
      this.selectedFile = null;
      this.signatures = [];
      this.errorMessage = 'Please select a PDF document.';
      return;
    }

    this.selectedFile = file;
    this.signatures = [];

    this.stage = 'loading';
    this.extractData();
  }

  resetSignatureCheck() {
    this.isDragOver = false;
    this.selectedFile = null;
    this.signatures = [];
    this.errorMessage = null;
    this.stage = 'none';
  }

  retrySignatureCheck() {
    if (!this.selectedFile) return;
    this.errorMessage = null;
    this.signatures = [];
    this.stage = 'loading';
    this.extractData();
  }

  extractData() {
    if (this.selectedFile) {
      this.service.extractData(this.selectedFile).subscribe({
        next: (signatures) => {
          this.signatures = signatures ?? [];
          this.stage = 'none';
        },
        error: (error) => {
          console.error('Error extracting signatures:', error);
          this.errorMessage = 'We couldn\'t verify signatures for this document. Please try again.';
          this.stage = 'none';
        }
      });
    }
  }

  get hasResults(): boolean {
    return (this.signatures?.length ?? 0) > 0;
  }

  get validCount(): number {
    return (this.signatures ?? []).filter(s => !!s.isValid).length;
  }

  get invalidCount(): number {
    return (this.signatures ?? []).filter(s => !s.isValid).length;
  }

  formatDate(dateString: string): string {
    return dateString?.includes('T') ? dateString.split('T')[0] : dateString;
  }

  formatStatus(isValid: boolean): string {
    return isValid ? 'Valid' : 'Invalid';
  }

  statusClass(isValid: boolean): string {
    return isValid ? 'status status--valid' : 'status status--invalid';
  }
}
