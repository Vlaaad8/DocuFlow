import { C } from '@angular/cdk/keycodes';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ExtractedField } from '../model/ExtractedField';


const URL = 'http://localhost:8080/ocr';

@Injectable({
  providedIn: 'root'
})
export class UploadService {

  constructor(private http: HttpClient) {

  }

  public extractData(file: File): Observable<ExtractedField[]> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ExtractedField[]>(`${URL}`, formData);
  }

  public saveExtractedData(data: ExtractedField[]): void {
    this.http.post<void>(`${URL}/extracted-fields`, data).subscribe({
      next: () => {
        console.log('Extracted data saved successfully.');
      },
      error: (error) => {
        console.error('Error saving extracted data:', error);
      }
    });
  }
}