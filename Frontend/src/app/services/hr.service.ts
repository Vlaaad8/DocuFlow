import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Relation } from '../model/Relation';
import { User } from '../model/User';


const URL = 'http://localhost:8080/hr';
@Injectable({
  providedIn: 'root'
})
export class HrService {

  constructor(private http: HttpClient) { }


  public getRelations(): Observable<Relation[]> {
    return this.http.get<Relation[]>(URL + '/relations');

  }
  public getUsers(): Observable<User[]> {
    return this.http.get<User[]>(URL + '/users');
  }
  public addRelation(bossID: number, subordinateID: number): Observable<void> {
    return this.http.post<void>(`${URL}/relation?bossID=${bossID}&subordinateID=${subordinateID}`, {});
  }
}