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

  public validateTemplate(file: File): Observable<Template> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<Template>(`${URL}/validate`, formData);
  }
  public getTemplates(): Observable<Template[]> {
    return this.http.get<Template[]>(`${URL}`);
  }
  public deleteTemplate(templateId: number): Observable<void> {
    return this.http.delete<void>(`${URL}`, { params: { id: templateId } });
  }
  public uploadTemplate(file: File, name: string, category: string, description: string): Observable<Template> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('name', name);
    formData.append('category', category);
    formData.append('description', description);
    return this.http.post<Template>(`${URL}`, formData);
  }
  public getTemplateCategories(): Observable<string[]> {
    return this.http.get<string[]>(`${URL}/category`);
  }
}
