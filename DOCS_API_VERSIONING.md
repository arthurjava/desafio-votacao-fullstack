# Estratégia de Versionamento da API

## Visão Geral

Este documento descreve a estratégia de versionamento adotada para a API do Sistema de Votação, garantindo compatibilidade, evolução controlada e clareza para consumidores da API.

---

## Estratégia Adotada: Versionamento por URI (Path Versioning)

A API utiliza **versionamento por caminho (URI Path Versioning)** como estratégia principal, onde a versão é parte explícita do endpoint:

```
/api/v1/pautas
/api/v1/pautas/{id}/sessao
/api/v1/pautas/{id}/votos
/api/v1/pautas/{id}/resultado
```

### Vantagens desta abordagem:

1. **Visibilidade imediata** - A versão é clara na URL
2. **Facilidade de roteamento** - Gateways e proxies podem rotear por versão
3. **Cacheabilidade** - URLs diferentes = caches diferentes
4. **Debugging simples** - Logs mostram claramente qual versão foi chamada
5. **Compatibilidade com ferramentas** - Swagger/OpenAPI, Postman, etc. funcionam nativamente

---

## Regras de Versionamento

### Quando criar uma nova versão (v2, v3, etc.):

| Cenário | Ação |
|---------|------|
| Remoção de campo obrigatório no request/response | Nova versão |
| Mudança no formato de dados (ex: `String` → `Object`) | Nova versão |
| Alteração na semântica de um endpoint (comportamento diferente) | Nova versão |
| Remoção de endpoint | Nova versão |
| Mudança nos códigos de status HTTP de erro | Nova versão |

### Quando NÃO criar nova versão (manter v1):

| Cenário | Ação |
|---------|------|
| Adição de campo opcional no response | Manter v1 |
| Adição de novo endpoint | Manter v1 |
| Adição de query parameter opcional | Manter v1 |
| Melhoria de performance sem mudança de contrato | Manter v1 |
| Correção de bug que não altera contrato | Manter v1 |

---

## Estrutura de Endpoints v1

### Pautas
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/pautas` | Criar nova pauta |
| GET | `/api/v1/pautas/{id}` | Buscar pauta por ID |
| POST | `/api/v1/pautas/{id}/sessao` | Abrir sessão de votação |
| POST | `/api/v1/pautas/{id}/votos` | Registrar voto |
| GET | `/api/v1/pautas/{id}/resultado` | Obter resultado |

### Códigos de Status HTTP

| Código | Significado |
|--------|-------------|
| 200 | Sucesso (GET, POST com retorno) |
| 201 | Criado (POST de criação) |
| 400 | Requisição inválida (validação) |
| 404 | Recurso não encontrado |
| 409 | Conflito (duplicidade, sessão existente) |
| 403 | Associado não habilitado para votar |
| 422 | Opção de voto inválida |

---

## Formato de Dados

### Request/Response JSON

Todos os endpoints usam `application/json` com codificação UTF-8.

**Exemplo - Criar Pauta:**
```json
POST /api/v1/pautas
{
  "titulo": "Pauta Exemplo",
  "descricao": "Descrição da pauta"
}
```

**Response:**
```json
{
  "id": 1,
  "titulo": "Pauta Exemplo",
  "descricao": "Descrição da pauta",
  "criadaEm": "2026-09-06T10:00:00"
}
```

### Datas
Formato ISO 8601: `yyyy-MM-dd'T'HH:mm:ss` (ex: `2026-09-06T10:00:00`)

### Enums
Valores em maiúsculo: `SIM`, `NAO`, `APROVADA`, `REPROVADA`, `EMPATE`, `INDEFINIDA`

---

## Depreciação e Ciclo de Vida

### Política de Suporte

| Versão | Status | Suporte Até |
|--------|--------|-------------|
| v1 | Ativa | Indeterminado (enquanto houver consumidores) |
| v2 | Planejada | - |

### Processo de Depreciação

1. **Anúncio** - 6 meses antes via headers `Deprecation: true` e `Sunset: <data>`
2. **Período de Transição** - Ambas versões funcionam simultaneamente
3. **Remoção** - Após período de transição, com comunicação prévia

### Headers de Depreciação (futuro v2)

```
Deprecation: true
Sunset: Sat, 01 Jan 2027 00:00:00 GMT
Link: <https://api.exemplo.com/api/v2/pautas>; rel="successor-version"
```

---

## Documentação OpenAPI/Swagger

A documentação interativa está disponível em:
- **Desenvolvimento**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### Anotações Utilizadas

- `@Tag` - Agrupamento por recurso (ex: "pauta")
- `@Operation` - Descrição do endpoint
- `@ApiResponse` - Códigos de resposta esperados
- `@Schema` - Documentação de modelos

---

## Exemplos de Evolução (v1 → v2)

### Cenário: Adicionar campo `autor` na pauta

**v1 (atual):**
```json
{
  "id": 1,
  "titulo": "Pauta",
  "descricao": "Desc",
  "criadaEm": "2026-09-06T10:00:00"
}
```

**v2 (futuro):**
```json
{
  "id": 1,
  "titulo": "Pauta",
  "descricao": "Desc",
  "criadaEm": "2026-09-06T10:00:00",
  "autor": {
    "id": 123,
    "nome": "João Silva"
  }
}
```

**Migração:**
1. Criar `/api/v2/pautas` com novo formato
2. Manter `/api/v1/pautas` inalterado
3. Documentar migração no changelog
4. Comunicar consumidores com 6 meses de antecedência

---

## Boas Práticas para Consumidores

1. **Sempre especifique a versão** na URL (`/api/v1/...`)
2. **Trate códigos de erro** 4xx/5xx adequadamente
3. **Use `Accept: application/json`** header
4. **Implemente retry com backoff** para 5xx
5. **Monitore headers `Deprecation`** para migração proativa

---

## Changelog

| Versão | Data | Alterações |
|--------|------|------------|
| v1.0.0 | 2026-09-06 | Versão inicial - CRUD de pautas, sessões, votos e resultados |

---

## Contato

Para dúvidas sobre versionamento ou migração:
- **Email**: api@votacao.example.com
- **Issues**: GitHub Repository Issues