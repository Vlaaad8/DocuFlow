import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '../model/User';
import { Observable } from 'rxjs';


const URL = 'http://localhost:8080/login';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  constructor(private http: HttpClient) { }

  public login(username: string, password: string): Observable<User> {
    const body = { username, password };
    return this.http.post<User>(URL, body);
  }
}
