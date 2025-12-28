import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Field } from '../model/Field';

const URL = 'http://localhost:8080/creator';

@Injectable({
  providedIn: 'root'
})
export class CreatorService {

  constructor(private http: HttpClient) { }


  public getFields(): Observable<Field[]> {
    return this.http.get<Field[]>(URL + '/field');
  }
}
