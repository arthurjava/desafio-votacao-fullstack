CREATE TABLE IF NOT EXISTS voto (
    id SERIAL PRIMARY KEY,
    pauta_id INTEGER NOT NULL,
    associado_id VARCHAR(255) NOT NULL,
    voto VARCHAR(10) NOT NULL CHECK (voto IN ('SIM', 'NAO')),
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_voto_pauta FOREIGN KEY (pauta_id) REFERENCES pauta(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_voto_unico ON voto(pauta_id, associado_id);