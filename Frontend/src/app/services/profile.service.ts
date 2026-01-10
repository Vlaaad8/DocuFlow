import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserCertificate } from '../model/user-certificate';
import { Observable } from 'rxjs';


const URL = 'http://localhost:8080/profile';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

constructor(private http: HttpClient) { }


public getUserCertificate(userID: number) : Observable<UserCertificate> {
  return this.http.get<UserCertificate>(`${URL}/certificate?userID=${userID}`);
}
}
