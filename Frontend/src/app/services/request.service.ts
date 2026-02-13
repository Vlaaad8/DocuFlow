import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Approval, ApprovalRequest } from '../model/Approval';


const URL = 'http://localhost:8080/requests';

@Injectable({
  providedIn: 'root'
})
export class RequestService {

  constructor(private http: HttpClient) { }


  getReqestsTOApprove(userId: number): Observable<Approval[]> {
    return this.http.get<Approval[]>(`${URL}/approve/${userId}`);

  }

  handleResponseAction(requestId: number, approverId: number, response: string): Observable<void> {
    const body = { requestId: requestId, approverId: approverId, response: response };
    console.log('Sending approval response:', body);
    return this.http.put<void>(
      `${URL}/answer?requestId=${requestId}&approverId=${approverId}&response=${response}`,
      {}
    );

  }
  getMyRequests(userId: number): Observable<ApprovalRequest[]> {
    return this.http.get<ApprovalRequest[]>(`${URL}?userId=${userId}`);
  }
}