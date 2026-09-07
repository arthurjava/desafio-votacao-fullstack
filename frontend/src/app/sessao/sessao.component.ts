import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { VotingService } from '../service/voting.service';
import { SessaoResponse } from '../dto/SessaoResponse';
import { PautaResponse } from '../dto/PautaResponse';
import { SessaoRequest } from '../dto/SessaoRequest';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-sessao',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  templateUrl: './sessao.component.html',
  styleUrl: './sessao.component.css'
})
export class SessaoComponent implements OnInit {
  duracaoSegundos: number = 60;
  sessao: SessaoResponse | null = null;
  pautas: PautaResponse[] = [];
  selectedPautaId: number | null = null;

  constructor(
    private votingService: VotingService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.carregarPautas();
    
    // Verificar se veio pautaId pela rota
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.selectedPautaId = +params['id'];
      }
    });
  }

  carregarPautas(): void {
    this.votingService.listarPautas().subscribe({
      next: (pautas) => {
        this.pautas = pautas;
        // Se não tem pauta selecionada e tem pautas, seleciona a primeira
        if (!this.selectedPautaId && pautas.length > 0) {
          this.selectedPautaId = pautas[0].id;
        }
      },
      error: (err) => {
        console.error('Erro ao carregar pautas', err);
      }
    });
  }

  abrirSessao(): void {
    if (!this.selectedPautaId) {
      alert('Por favor, selecione uma pauta');
      return;
    }

    const request: SessaoRequest = {
      duracaoEmSegundos: this.duracaoSegundos
    };

    this.votingService.abrirSessao(this.selectedPautaId, request).subscribe({
      next: (response) => {
        this.sessao = response;
        alert('Sessão aberta com sucesso!');
      },
      error: (err) => {
        console.error('Erro ao abrir sessão', err);
        alert('Erro ao abrir sessão: ' + (err.error?.message || err.message));
      }
    });
  }
}