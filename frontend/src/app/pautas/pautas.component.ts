import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { VotingService } from '../service/voting.service';
import { PautaResponse } from '../dto/PautaResponse';
import { CriarPautaRequest } from '../dto/CriarPautaRequest';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-pautas',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, DatePipe],
  templateUrl: './pautas.component.html',
  styleUrl: './pautas.component.css'
})
export class PautasComponent implements OnInit {
  novoTitulo: string = '';
  novaDescricao: string = '';
  pautas: PautaResponse[] = [];

  constructor(private votingService: VotingService) {}

  ngOnInit(): void {
    this.carregarPautas();
  }

  carregarPautas(): void {
    this.votingService.listarPautas().subscribe({
      next: (pautas) => {
        this.pautas = pautas;
      },
      error: (err) => {
        console.error('Erro ao carregar pautas', err);
        this.pautas = [];
      }
    });
  }

  criarPauta(): void {
    if (!this.novoTitulo.trim()) {
      alert('Por favor, informe o título da pauta');
      return;
    }

    const request: CriarPautaRequest = {
      titulo: this.novoTitulo,
      descricao: this.novaDescricao || ''
    };

    this.votingService.criarPauta(request).subscribe({
      next: (response) => {
        this.pautas.unshift(response);
        this.novoTitulo = '';
        this.novaDescricao = '';
        alert('Pauta criada com sucesso!');
      },
      error: (err) => {
        console.error('Erro ao criar pauta', err);
        alert('Erro ao criar pauta: ' + (err.error?.message || err.message));
      }
    });
  }
}