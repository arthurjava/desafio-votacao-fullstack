import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { VotoVoteOpcao } from '../entity/Voto';

@Injectable({
  providedIn: 'root'
})
export class VotingService {
  private apiUrl = 'http://localhost:8080/api/v1/pautas';

  constructor(private http: HttpClient) {}

  buscarPauta(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  criarPauta(request: any): Observable<any> {
    return this.http.post(`${this.apiUrl}`, request);
  }

  abrirSessao(pautaId: number, request: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/${pautaId}/sessao`, request);
  }

  votar(pautaId: number, request: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/${pautaId}/votos`, request);
  }

  resultado(pautaId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${pautaId}/resultado`);
  }
}