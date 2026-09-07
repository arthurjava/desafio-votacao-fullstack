import { Routes } from '@angular/router';
import { PautasComponent } from './pautas/pautas.component';
import { SessaoComponent } from './sessao/sessao.component';
import { VotacaoComponent } from './votacao/votacao.component';

export const routes: Routes = [
  { path: '', redirectTo: '/pautas', pathMatch: 'full' },
  { path: 'pautas', component: PautasComponent },
  { path: 'pautas/:id/sessao', component: SessaoComponent },
  { path: 'pautas/:id/votar', component: VotacaoComponent }
];
