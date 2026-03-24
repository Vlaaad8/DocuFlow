import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';


const URL = 'http://localhost:8080/generate/pdf';
@Injectable({
  providedIn: 'root',
})

export class DocumentLoader {

  public constructor(private http: HttpClient) { }


  loadDocument(path: string) : Observable<Blob> {
    return this.http.post(URL,path,{responseType: 'blob', headers: {'Content-Type': 'application/json'}});
  }
}
