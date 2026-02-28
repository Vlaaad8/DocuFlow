import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { GenerateTemplate, TemplateApprovers } from '../model/GenerateTemplate';
import { FieldTemplate } from '../model/FieldTemplate';


const URL = 'http://localhost:8080/generate';
@Injectable({
  providedIn: 'root'
})
export class GenerateService {

  constructor(private http: HttpClient) { }

  public getTemplates(userID: number): Observable<GenerateTemplate[]> {
    return this.http.get<GenerateTemplate[]>(URL + '/' + userID);

  }
  public getTemplateValue(userID: number, templateID: number): Observable<FieldTemplate[]> {
    return this.http.get<FieldTemplate[]>(URL + '/' + templateID + '/' + userID);
  }

  public generateDocument(templateID: number, userID: number): Observable<void> {
    const formData = new FormData();
    formData.append('templateID', templateID.toString());
    formData.append('userID', userID.toString());
    return this.http.post<void>(URL, formData);
  }

  public getTemplateApprovers(templateId: number, userId: number): Observable<TemplateApprovers[]> {
    return this.http.get<TemplateApprovers[]>(`${URL}/approver/${templateId}/${userId}`);
  }

public getDataProfile(userId: number): Observable<{ category: string, value: number }[]> {
  return this.http
    .get<Record<string, boolean>>(`${URL}/profile/${userId}`)
    .pipe(
      map(response =>
        Object.entries(response).map(([key, value]) => ({
          category: key,
          value: value ? 1 : 0
        }))
      )
    );
}
}