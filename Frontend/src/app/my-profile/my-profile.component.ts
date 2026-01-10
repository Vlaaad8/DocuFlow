import { Component, OnInit } from '@angular/core';
import { MatSidenavModule } from "@angular/material/sidenav";
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";
import {MatTabsModule} from '@angular/material/tabs';
import { MatIconModule } from "@angular/material/icon";
import {MatProgressBarModule} from '@angular/material/progress-bar';
import { ProfileService } from '../services/profile.service';
import { UserCertificate } from '../model/user-certificate';
import { User } from '../model/User';

@Component({
  selector: 'app-my-profile',
  templateUrl: './my-profile.component.html',
  styleUrls: ['./my-profile.component.css'],
  imports: [MatSidenavModule, SidenavUserComponent, ExitButtonComponent, MatTabsModule, MatIconModule,MatProgressBarModule]
})
export class MyProfileComponent implements OnInit {

  public certificate: UserCertificate | null = null;
  private userID: number = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}').id;
  public user: User = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}');
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
}
