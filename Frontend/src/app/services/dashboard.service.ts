import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DashboardData } from '../model/dashboardData';


const URL = 'http://localhost:8080/dashboard';
@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  constructor(private http: HttpClient) { }


  public getDashboardData(userID: number): Observable<DashboardData> {
    return this.http.get<DashboardData>(`${URL}?userID=${userID}`);
  }

  public markNotificationsAsRead(notifications: any[]): Observable<void> {
    const url = `${URL}/notification`;
    return this.http.post<void>(url, notifications);
  }
}
