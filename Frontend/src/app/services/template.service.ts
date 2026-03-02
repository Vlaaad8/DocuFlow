import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApprovalFlowTemplate, Template } from '../model/Template';

export interface HtmlRequest {
  content: string;
  fileName: string;
}

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
  public uploadTemplate(file: File, name: string, category: string, description: string, approvalFlowId: number): Observable<Template> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('name', name);
    formData.append('category', category);
    formData.append('description', description);
    formData.append('approvalFlow', approvalFlowId.toString());
    return this.http.post<Template>(`${URL}`, formData);
  }
  public getTemplateCategories(): Observable<string[]> {
    return this.http.get<string[]>(`${URL}/category`);
  }
  //TODO to redo


  public getTemplateHTMLById(templateId: number): Observable<HtmlRequest> {
    return this.http.get<HtmlRequest>(`${URL}/html`, {
      params: { id: templateId }
    });
  }

  public editTemplateHTML(htmlContent: string, fileName: string): Observable<void> {
    return this.http.put<void>(`${URL}`, { html: htmlContent, fileName: fileName });

  }

  public getApprovalFlows(): Observable<ApprovalFlowTemplate[]> {
    return this.http.get<ApprovalFlowTemplate[]>(`${URL}/approvalFlows`);
  }
  public validateHTMLTemplate(html: string): Observable<void> {
    const formData = new FormData();
    formData.append('html', html);
    return this.http.post<void>(URL + '/validate/html', formData);
  }
}