import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GenerateTemplate } from '../model/GenerateTemplate';
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
}