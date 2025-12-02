import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Template } from '../model/Template';


const URL = 'http://localhost:8080/template';

@Injectable({
  providedIn: 'root'
})
export class TemplateService {

constructor(private http: HttpClient) { }

public uploadTemplate(file: File, templateName: string) : Observable<Template> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<Template>(`${URL}/validate`, formData);
  }
public getTemplates(): Observable<Template[]> {
    return this.http.get<Template[]>(`${URL}`);
  }
}
