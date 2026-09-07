export enum VotoVoteOpcao {
  SIM = 'SIM',
  NAO = 'NAO'
}

export interface Voto {
  id: number;
  pautaId: number;
  associadoId: string;
  voto: VotoVoteOpcao;
  criadoEm: string;
}