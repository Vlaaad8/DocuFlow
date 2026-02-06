import { Component, OnInit, ViewChild } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule, MatToolbar } from '@angular/material/toolbar';
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";
import { DashboardService } from '../services/dashboard.service';
import { User } from '../model/User';
import Chart, { Legend } from 'chart.js/auto';


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  imports: [MatSidenavModule, MatIconModule, SidenavUserComponent, ExitButtonComponent]
})
export class DashboardComponent implements OnInit {
  
  public chart! : Chart;
  public dailyChart! : Chart;
  public user!: User;

  constructor(private service: DashboardService) {
        sessionStorage.getItem('loggedInUser')
        this.user = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}');
    
   }

  ngOnInit() {
    this.chart = new Chart('activityChart', this.getChartConfig());
    this.dailyChart = new Chart('dailyChart', this.getChartConfigLine());
  }

  getChartConfig() : any {
    const data = {
      labels: [
        'Accepted',
        'Rejected',
        'Pending'
      ],
      datasets: [{
        label: 'Approval Status',
        data: [12, 19, 3],
        backgroundColor: [
          'rgba(75, 192, 192, 0.2)',
          'rgba(255, 99, 132, 0.2)',
          'rgba(255, 206, 86, 0.2)'
        ],
        borderColor: [
          'rgba(75, 192, 192, 1)',
          'rgba(255, 99, 132, 1)',
          'rgba(255, 206, 86, 1)'
        ],
        borderWidth: 1
      }]
    };

    return {
      type: 'pie',
      data: data,
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend:{
            position: 'none'
          }
        }
      },
    }
  }

  getChartConfigLine() : any {
    const data = {
      labels: [
        'Monday',
        'Tuesday',
        'Wednesday',
        'Thursday',
        'Friday',
        'Saturday',
        'Sunday'
      ],
      datasets: [{
        label: 'Daily Activity',
        data: [12, 19, 3, 5, 2, 3, 7],
        backgroundColor: 'rgba(75, 192, 192, 0.2)',
        borderColor: 'rgba(75, 192, 192, 1)',
        borderWidth: 1
      },
    {
        label: 'Daily Approvals',
        data: [10, 15, 2, 4, 1, 2, 5],
        backgroundColor: 'rgba(255, 99, 132, 0.2)',
        borderColor: 'rgba(255, 99, 132, 1)',
        borderWidth: 1
    }]
    };

    return {
      type: 'line',
      data: data,
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend:{
            position: 'none'
          }
        }
      },
    }
  }
}
