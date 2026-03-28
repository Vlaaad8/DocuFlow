import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {map, Observable} from 'rxjs';
import {GenerateTemplate, TemplateApprovers} from '../model/GenerateTemplate';
import {FieldTemplate} from '../model/FieldTemplate';


const URL = 'http://localhost:8080/generate';

@Injectable({
  providedIn: 'root'
})
export class GenerateService {

  constructor(private http: HttpClient) {
  }

  public getTemplates(userID: number): Observable<GenerateTemplate[]> {
    return this.http.get<GenerateTemplate[]>(URL + '/' + userID);

  }

  public getTemplateValue(userID: number, templateID: number, source: string): Observable<FieldTemplate[]> {
    const params = new HttpParams().set('source', source);
    return this.http.get<FieldTemplate[]>(`${URL}/${templateID}/${userID}`, { params: params });
  }

  public generateDocument(templateID: number, userID: number, data: { [key: string]: string },source: string): Observable<void> {
    const params = new HttpParams()
      .set('templateID', templateID.toString())
      .set('userID', userID.toString())
      .set('source', source);
    return this.http.post<void>(URL, data, {params: params});
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

  public getTemplateSources(templateId: number, userId: number): Observable<string[]> {
    const params = new HttpParams()
      .set('templateID', templateId.toString())
      .set('userID', userId.toString());

    return this.http.get<string[]>(`http://localhost:8080/template-sources`, {params: params});
  }
}
