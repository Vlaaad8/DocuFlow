import {HttpClient, HttpParams} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SignatureInfo, UserCertificate } from '../model/user-certificate';
import { Observable } from 'rxjs';
import {UserStoredValue} from '../model/ExtractedField';


const URL = 'http://localhost:8080/profile';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

constructor(private http: HttpClient) { }


public getUserCertificate(userID: number) : Observable<UserCertificate> {
  return this.http.get<UserCertificate>(`${URL}/certificate?userID=${userID}`);
}
 public extractData(file: File): Observable<SignatureInfo[]> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<SignatureInfo[]>(`${URL}/verify`, formData);
  }


  public getStoredData(userId: number) : Observable<UserStoredValue[]> {
    return this.http.get<UserStoredValue[]>(`${URL}/savedData?userID=${userId}`);
  }

  public updateUserField(fieldID: number, value: string): Observable<void> {

    const params = new HttpParams()
      .set('fieldID', fieldID)
      .set('value', value);

    return this.http.put<void>(`${URL}/savedData`, null, { params: params });
  }
}
