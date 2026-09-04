import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PautaResponse } from '../dto/PautaResponse';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class PautaService {
  private apiUrl = 'http://localhost:8080/api/v1/pautas';

  constructor(private http: HttpClient) {}

  buscarPauta(id: number): Observable<PautaResponse> {
    return this.http.get<PautaResponse>(`${this.apiUrl}/${id}`).pipe(
      catchError(error => {
        console.error('Erro ao buscar pauta', error);
        throw error;
      })
    );
  }
}