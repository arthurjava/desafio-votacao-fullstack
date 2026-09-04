import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VotingService } from '../service/voting.service';
import { PautaResponse } from '../dto/PautaResponse';

@Component({
  selector: 'app-pautas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pautas.component.html',
  styleUrl: './pautas.component.css'
})
export class PautasComponent {
  novoTitulo: string = '';
  novaDescricao: string = '';
  pautas: PautaResponse[] = [];

  constructor(private votingService: VotingService) {}

  ngOnInit(): void {
    this.carregarPautas();
  }

  carregarPautas(): void {
    // Buscar todas as pautas - usaremos o endpoint de listagem ou consultar uma a uma
    // Por enquanto, vamos buscar pauta de ID 1
    this.votingService.pesquisarPauta(1).subscribe({
      next: (pauta) => {
        this.pautas = [pauta];
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

    const request = {
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