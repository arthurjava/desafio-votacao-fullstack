import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VotingService } from '../service/voting.service';
import { VotarRequest } from '../dto/VotarRequest';
import { VotoVoteOpcao } from '../entity/Voto';
import { PautaService } from '../service/pauta.service';
import { ResultService } from '../service/result.service';

@Component({
  selector: 'app-votacao',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './votacao.component.html',
  styleUrl: './votacao.component.css'
})
export class VotacaoComponent {
  associadoId: string = '';
  votoOpcao: 'SIM' | 'NAO' = 'SIM';
  pautaId: number = 1;
  resultado: any = null;
  loading = false;

  constructor(
    private votingService: VotingService,
    private pautaService: PautaService,
    private resultService: ResultService
  ) {}

  ngOnInit(): void {
    this.carregarPauta();
  }

  carregarPauta(): void {
    this.pautaService.buscarPauta(this.pautaId).subscribe({
      next: (pauta) => {
        this.pautaId = pauta.id;
      },
      error: (err) => {
        console.error('Erro ao carregar pauta', err);
      }
    });
  }

  registrarVoto(): void {
    this.loading = true;
    const request: VotarRequest = {
      associadoId: this.associadoId,
      voto: this.votoOpcao
    };

    this.votingService.votar(this.pautaId, request).subscribe({
      next: (response) => {
        this.loading = false;
        this.resultado = response;
        alert('Voto registrado com sucesso!');
      },
      error: (err) => {
        this.loading = false;
        console.error('Erro ao votar', err);
        alert('Erro ao registrar voto: ' + (err.error?.message || err.message));
      }
    });
  }
}