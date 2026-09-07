import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { VotingService } from '../service/voting.service';
import { VotarRequest } from '../dto/VotarRequest';
import { PautaService } from '../service/pauta.service';
import { ResultService } from '../service/result.service';
import { PautaResponse } from '../dto/PautaResponse';
import { ResultadoResponse } from '../dto/ResultadoResponse';

@Component({
  selector: 'app-votacao',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './votacao.component.html',
  styleUrl: './votacao.component.css'
})
export class VotacaoComponent implements OnInit {
  associadoId: string = '';
  votoOpcao: 'SIM' | 'NAO' = 'SIM';
  pautaId: number | null = null;
  pautas: PautaResponse[] = [];
  resultado: ResultadoResponse | null = null;
  loading = false;

  constructor(
    private votingService: VotingService,
    private pautaService: PautaService,
    private resultService: ResultService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.carregarPautas();
    
    // Verificar se veio pautaId pela rota
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.pautaId = +params['id'];
      }
    });
  }

  carregarPautas(): void {
    this.votingService.listarPautas().subscribe({
      next: (pautas) => {
        this.pautas = pautas;
        // Se não tem pauta selecionada e tem pautas, seleciona a primeira
        if (!this.pautaId && pautas.length > 0) {
          this.pautaId = pautas[0].id;
        }
      },
      error: (err) => {
        console.error('Erro ao carregar pautas', err);
      }
    });
  }

  registrarVoto(): void {
    if (!this.pautaId) {
      alert('Por favor, selecione uma pauta');
      return;
    }

    if (!this.associadoId.trim()) {
      alert('Por favor, informe o ID do associado');
      return;
    }

    this.loading = true;
    const request: VotarRequest = {
      associadoId: this.associadoId,
      voto: this.votoOpcao
    };

    this.votingService.votar(this.pautaId, request).subscribe({
      next: (response) => {
        this.loading = false;
        alert('Voto registrado com sucesso!');
        // Buscar resultado atualizado
        this.carregarResultado();
      },
      error: (err) => {
        this.loading = false;
        console.error('Erro ao votar', err);
        alert('Erro ao registrar voto: ' + (err.error?.message || err.message));
      }
    });
  }

  carregarResultado(): void {
    if (!this.pautaId) return;
    
    this.resultService.obterResultado(this.pautaId).subscribe({
      next: (resultado) => {
        this.resultado = resultado;
      },
      error: (err) => {
        console.error('Erro ao carregar resultado', err);
      }
    });
  }
}