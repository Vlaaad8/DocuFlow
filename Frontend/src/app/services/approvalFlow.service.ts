import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApprovalChain } from '../model/Approval';

const URL = 'http://localhost:8080/approvalChain';

@Injectable({
  providedIn: 'root'
})
export class ApprovalFlowService {

  constructor(private http: HttpClient) { }


  public getRoles(): Observable<string[]> {
    return this.http.get<string[]>(`${URL}/roles`);
  }

  public saveFlow(flowData: any): Observable<void> {
    const body = {
        name: flowData.name,
        roles: flowData.steps.map((s: any) => s.role)
      
    };
    console.log(body);

    return this.http.post<void>(`${URL}`, body);
  }

  public getFlows(): Observable<ApprovalChain[]> {
    return this.http.get<ApprovalChain[]>(`${URL}`);
  }
}
