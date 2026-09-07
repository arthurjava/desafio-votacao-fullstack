export interface ResultadoResponse {
  pautaId: number;
  sim: number;
  nao: number;
  total: number;
  resultado: 'APROVADA' | 'REPROVADA' | 'EMPATE' | 'INDEFINIDA';
}