import { Component, OnInit, ViewChild } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule, MatToolbar } from '@angular/material/toolbar';
import { SidenavUserComponent } from "../commons/sidenav-user/sidenav-user.component";
import { ExitButtonComponent } from "../commons/exit-button/exit-button.component";
import { DashboardService } from '../services/dashboard.service';
import { User } from '../model/User';
import Chart, { Legend } from 'chart.js/auto';
import { DashboardData, Notification } from '../model/dashboardData';
import { MatBadgeModule } from '@angular/material/badge';
import { LoadingComponent } from "../commons/loading/loading.component";
import { CommonModule } from '@angular/common';
import { MatMenuModule } from '@angular/material/menu';
import { WebSocketService } from '../services/notifications.service';
import { ConvertDatePipe } from '../pipes/convertDate.pipe';


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  imports: [MatSidenavModule, MatIconModule, SidenavUserComponent, ExitButtonComponent, MatBadgeModule, LoadingComponent, CommonModule, MatMenuModule, ConvertDatePipe]
})
export class DashboardComponent implements OnInit {

  public chart!: Chart;
  public dailyChart!: Chart;
  public user!: User;
  public dashboardData!: DashboardData;
  public notiifications:any[] = [];

  public status: string = 'loading' // 'loading' , 'present'

  constructor(private service: DashboardService,private notificationsService: WebSocketService) {
    sessionStorage.getItem('loggedInUser')
    this.user = JSON.parse(sessionStorage.getItem('loggedInUser') || '{}');

  }

  ngOnInit() {
    this.service.getDashboardData(this.user.id).subscribe({
      next: (data) => {
        this.dashboardData = data;
        this.status = 'present';
        setTimeout(() => {
          this.createCharts();
        }, 2);
      },
      error: (err) => {
        console.error('Error fetching dashboard data:', err);
      }
    });
    this.notificationsService.connect(this.user.id.toString());
    this.notificationsService.getNotifications().subscribe({
      next: (notification) => {
        console.log('Received notification:', notification);
        this.notiifications.push(notification);
      },
      error: (err) => {
        console.error('Error receiving notifications:', err);
      }
    });
  }
  private createCharts() {
    // Only initialize if they haven't been created yet
    if (!this.chart) {
      this.chart = new Chart('activityChart', this.getChartConfigRequests(this.dashboardData.chartData));
    }
    if (!this.dailyChart) {
      this.dailyChart = new Chart('dailyChart', this.getChartConfigSourceDistribution(this.dashboardData.sourceDistribution));
    }
  }

  getChartConfigRequests(requests: any[]): any {

    const labels = requests.map(req => req.title);

    const dataValues = requests.map(req => {
      switch (req.status?.toUpperCase()) {
        case 'ACCEPTED': return 3;
        case 'PENDING': return 2;
        case 'REJECTED': return 1;
        default: return 0;
      }
    });


    const backgroundColors = requests.map(req => {
      switch (req.status?.toUpperCase()) {
        case 'ACCEPTED': return 'rgba(75, 192, 192, 0.6)';
        case 'PENDING': return 'rgba(255, 206, 86, 0.6)';
        case 'REJECTED': return 'rgba(255, 99, 132, 0.6)';
        default: return 'rgba(201, 203, 207, 0.6)';
      }
    });

    const borderColors = requests.map(req => {
      switch (req.status?.toUpperCase()) {
        case 'ACCEPTED': return 'rgba(75, 192, 192, 1)';
        case 'PENDING': return 'rgba(255, 206, 86, 1)';
        case 'REJECTED': return 'rgba(255, 99, 132, 1)';
        default: return 'rgba(201, 203, 207, 1)';
      }
    });

    return {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Status',
          data: dataValues,
          backgroundColor: backgroundColors,
          borderColor: borderColors,
          borderWidth: 1,
          borderRadius: 4
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false
          },
          tooltip: {
            callbacks: {

              label: (context: any) => {
                const index = context.dataIndex;
                const req = requests[index];
                const date = new Date(req.timestamp).toLocaleDateString();
                return `Status: ${req.status} (Modified: ${date})`;
              }
            }
          }
        },
        scales: {
          y: {
            min: 0,
            max: 4,
            ticks: {
              stepSize: 1,

              callback: (value: number) => {
                if (value === 3) return 'ACCEPTED';
                if (value === 2) return 'PENDING';
                if (value === 1) return 'REJECTED';
                return '';
              }
            }
          }
        }
      }
    };
  }



  getChartConfigSourceDistribution(sourceData: {source: string, value: number}[]): any {

    const labels = sourceData.map(req => this.formatSourceOfData(req.source));
    const dataValues = sourceData.map(req => req.value);


    const backgroundColors = [
      'rgba(54, 162, 235, 0.6)',
      'rgba(255, 159, 64, 0.6)',
      'rgba(153, 102, 255, 0.6)',
      'rgba(201, 203, 207, 0.6)',
      'rgba(255, 205, 86, 0.6)'
    ];

    const borderColors = [
      'rgba(54, 162, 235, 1)',
      'rgba(255, 159, 64, 1)',
      'rgba(153, 102, 255, 1)',
      'rgba(201, 203, 207, 1)',
      'rgba(255, 205, 86, 1)'
    ];

    return {
      type: 'doughnut',
      data: {
        labels: labels,
        datasets: [{
          label: 'Number of values',
          data: dataValues,

          backgroundColor: backgroundColors.slice(0, labels.length),
          borderColor: borderColors.slice(0, labels.length),
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: true,
            position: 'right'
          },
          tooltip: {
            callbacks: {
              label: (context: any) => {
                const label = context.source || '';
                const value = context.parsed || 0;
                return `${label}: ${value}`;
              }
            }
          }
        }
      }
    };
  }
  formatSourceOfData(source: string): string {
    switch (source) {
      case ("NATIONAL_IDENTITY_CARD"):
        return "ID Card";
      case ("PASSPORT"):
        return "Passport";
      case ("DRIVER_LICENSE"):
        return "Driving License";
      case ("RESIDENCE_PERMIT"):
        return "Residence Permit";
      case ("SOCIAL_SECURITY_CARD"):
        return "US Social Security Card";
      case ("MANUAL_ENTRY"):
        return "Manual Entry";

      default:
        return "Unknown";
    }
  }
}
