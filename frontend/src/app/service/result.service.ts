import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResultadoResponse } from '../dto/ResultadoResponse';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ResultService {
  private apiUrl = 'http://localhost:8080/api/v1/pautas';

  constructor(private http: HttpClient) {}

  obterResultado(id: number): Observable<ResultadoResponse> {
    return this.http.get<ResultadoResponse>(`${this.apiUrl}/${id}/resultado`).pipe(
      catchError(error => {
        console.error('Erro ao obter resultado', error);
        throw error;
      })
    );
  }
}