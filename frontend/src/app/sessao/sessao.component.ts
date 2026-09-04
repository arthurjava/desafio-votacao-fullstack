import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VotingService } from '../service/voting.service';
import { SessaoResponse } from '../dto/SessaoResponse';

@Component({
  selector: 'app-sessao',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sessao.component.html',
  styleUrl: './sessao.component.css'
})
export class SessaoComponent {
  duracaoSegundos: number = 60;
  sessao: SessaoResponse | null = null;

  constructor(private votingService: VotingService) {}

  ngOnInit(): void {}

  abrirSessao(): void {
    const request = {
      duracaoEmSegundos: this.duracaoSegundos
    };

    this.votingService.abrirSessao(1, request).subscribe({
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