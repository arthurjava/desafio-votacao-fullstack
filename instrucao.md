# Instruções de Execução - Sistema de Votação Fullstack

## Índice

1. [Pré-requisitos](#1-pré-requisitos)
2. [Como Obter o Sistema](#2-como-obter-o-sistema)
3. [Opção 1: Execução via Docker (Recomendada)](#3-opção-1-execução-via-docker)
4. [Opção 2: Execução Local (Maven + Node.js)](#4-opção-2-execução-local-maven--nodejs)
5. [Acesso à Aplicação](#5-acesso-à-aplicação)
6. [Solução de Problemas](#6-solucão-de-problemas)
7. [Verificação Pós-Execução](#7-verificação-pós-execução)

---

## 1. Pré-requisitos

### Software Obrigatório

| Ferramenta | Versão Mínima | Finalidade |
|------------|---------------|------------|
| **Git** | 2.0+ | Clonar o repositório |
| **Java JDK** | 25 | Backend Spring Boot |
| **Maven** | 3.9+ | Build do backend |
| **Node.js** | 20+ | Frontend Angular |
| **npm** | 10+ | Gerenciador de pacotes Node |
| **Docker** | 20.10+ | Containerização (opcional) |
| **Docker Compose** | 2.0+ | Orquestração de containers |
| **PostgreSQL** | 16 (local) | Banco de dados (se não usar Docker) |

### Hardware

- **RAM**: Mínimo 4GB (8GB recomendado)
- **Disco**: 2GB livres para build e containers
- **Sistema**: Windows 10/Linux/macOS

---

## 2. Como Obter o Sistema

### Via Git (Recomendado)

```bash
# Clonar o repositório
git clone https://github.com/seu-usuario/desafio-votacao-fullstack.git

# Entrar no diretório
cd desafio-votacao-fullstack

# Verificar arquivos
ls -la
```

### Via Download direto

1. Acessar o repositório no GitHub
2. Clicar em "Code" → "Download ZIP"
3. Extrair o arquivo `desafio-votacao-fullstack.zip`
4. Entrar na pasta extraída no terminal

---

## 3. Opção 1: Execução via Docker (Mais Simples)

### 3.1. Levantar os Containers

```bash
# Na raiz do projeto
docker compose up --build
```

### 3.2. Aguardar Inicialização

O processo levará aproximadamente 2-3 minutos na primeira execução:

1. **PostgreSQL**: Inicializa e configura o banco (porta 5432)
2. **Backend**: Compila Java, build Spring Boot, aplica Flyway migrations
3. **Frontend**: Instala dependências Angular, inicia development server

### 3.2.1. Verificar Status

```bash
# Listar containers rodando
docker compose ps

# Ver logs do backend
docker compose logs backend

# Ver logs do frontend
docker compose logs frontend
```

### 3.3. Testes Rápidos

```bash
# Testar API
curl http://localhost:8080/api/v1/pautas

# Testar Swagger UI
open http://localhost:8080/swagger-ui.html

# Testar Frontend
open http://localhost:4200
```

---

## 4. Opção 2: Execução Local (Maven + Node.js)

### 4.1. Backend (Java Spring Boot)

#### 4.1.1. Configurar Banco de Dados

```bash
# Criar banco PostgreSQL
createdb votacao

# Ou via linha de comando
psql -U postgres -c "CREATE DATABASE votacao;"
```

#### 4.1.2. Configurar Credenciais

O sistema já vem com configurações padrão. Editar se necessário:

```bash
# Arquivo: src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/votacao
    username: votacao
    password: votacao123
```

#### 4.1.3. Executar Backend

```bash
# Entrar no diretório do projeto
cd D:\Projetos\db\desafio-votacao-fullstack

# Compilar
mvn compile

# Executar
mvn spring-boot:run
```

#### 4.1.4. Verificar Backend

```bash
# A aplicação iniciará na porta 8080
# Verificar se está rodando
curl -s http://localhost:8080/actuator/health
```

### 4.2. Frontend (Angular)

#### 4.2.1. Instalar Dependências

```bash
# Entrar no diretório frontend
cd frontend

# Instalar npm packages
npm install
```

#### 4.2.2. Executar Frontend

```bash
# Iniciar servidor de desenvolvimento
ng serve

# Ou com hot reload
ng serve --host 0.0.0.0 --port 4200
```

#### 4.2.2.1. Configurar Angular Proxy (Opcional)

Para evitar problemas de CORS, criar `frontend/src/proxy.conf.json`:

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false
  }
}

# Executar com proxy
ng serve --configuration development --proxy-config proxy.conf.json
```

#### 4.2.3. Verificar Frontend

Acessar: `http://localhost:4200`

---

## 5. Estrutura de Execução

### 5.1. Via Docker (Completo)

```
┌──────────────────────────────────────────┐
│              DOCKER COMPOSE              │
├──────────────────────────────────────────┤
│  ┌──────────────────────────────────────┐│
│  │    FRONTEND (Angular 19)             ││
│  │  Porto: 4200 │ Material Design       ││
│  └───────────────┬──────────────────────┘│
│                  │  HTTP                 │
│                  ▼                       │
│   ┌───────────────┴─────────────────────┐│
│   │  SHARED DOCKER NETWORK              ││
│   └───────────────┬─────────────────────┘│
│                   │                      │
│  SQL Queries      │   REST API (HTTP)    │
│                   ▼                      │
│   ┌───────────────┴─────────────────────┐│
│   │    POSTGRES 16                      ││
│   │  Porto: 5432 | Dados: pauta, voto   ││
│   └─────────────────────────────────────┘│
│                 ▲                        │
│                 │  Volumes:              │
│   ┌─────────────┴───────────────────────┐│
│   │ postgres_data (dados persistentes)  ││
│   │ node_modules (dependências frontend)││
│   │ backend jar / código fonte          ││
│   └─────────────────────────────────────┘│
│                                          │
├──────────────────────────────────────────┤
│  Ciclo: docker compose up --build        │
│  ← Recria todos os containers            │
└──────────────────────────────────────────┘
```

### 5.2. Via Local (Separado)

```
┌───────────────────────────────┐      ┌─────────────────────────┐
│   BACKEND (Local)             │      │     FRONTEND (Local)    │
│  (porta 8080)                 │      │   (porta 4200)          │
│  mvn spring-boot:run          │      │  ng serve --host 0.0.0.0│
│  (Java 25 + Spring)           │      │  (Angular 19 + Material)│
├────────────────────────────── ┼──────┤─────────────────────────┤
│   Comunicação via             │      │   Acessar em:           │
│  http://localhost:8080/api/v1 │      │   http://localhost:4200 │
└───────────────────────────────┘      └─────────────────────────┘
```

---

## 6. Acesso à Aplicação

### 6.1. URLs Principais

| Serviço | URL | Descrição |
|---------|-----|-----------|
| **API REST** | `http://localhost:8080/api/v1/pautas` | Endpoints da votação |
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` | Documentação interativa |
| **API Docs (JSON)** | `http://localhost:8080/api-docs` | Especificação OpenAPI JSON |
| **Frontend** | `http://localhost:4200` | Interface Angular |
| **Health Check** | `http://localhost:8080/actuator/health` | Status do serviço |

### 6.2. Exemplos de API

#### Criar Pauta

```bash
curl -X POST http://localhost:8080/api/v1/pautas \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Pauta Teste","descricao":"Descrição de teste"}'
```

#### Abrir Sessão

```bash
curl -X POST http://localhost:8080/api/v1/pautas/1/sessao \
  -H "Content-Type: application/json" \
  -d '{"duracaoEmSegundos":60}'
```

#### Registrar Voto

```bash
curl -X POST http://localhost:8080/api/v1/pautas/1/votos \
  -H "Content-Type: application/json" \
  -d '{"associadoId":"12345678901","voto":"SIM"}'
```

#### Obter Resultado

```bash
curl -X GET http://localhost:8080/api/v1/pautas/1/
```

---

## 7. Solução de Problemas

### 7.1. Problemas Comuns - Docker

| Problema | Solução |
|----------|---------|
| `docker compose up` falha na inicialização | `docker compose down` → `docker compose up --build` |
| PostgreSQL não conecta | Verificar `docker compose logs postgres` |
| Backend dá erro de "no main manifest attribute" | `docker compose down` → `docker compose up --build --force-rm` |
| Frontend dá erro "Cannot find module '/app/ng'" | `docker compose down` → `docker compose up --build` |

### 7.2. Problemas Comuns - Local

| Problema | Solução |
|----------|---------|
| `mvn compile` falha por Java version | Usar Java 25 (Adoptium, OpenJDK 25) |
| `mvn spring-boot:run` dá erro de conexão DB | Verificar se PostgreSQL está rodando e credenciais |
| `ng serve` dá erro de CORS | Adicionar proxy config ou usar `http://localhost:8080` direto |
| Erro "porta 8080 already in use" | Matar processo na porta ou usar porta diferente |

### 7.3. Credenciais Padrão

| Componente | Usuário | Senha | Banco |
|------------|---------|-------|-------|
| **PostgreSQL (Docker)** | `postgres` | `senha_padrao` | `votacao` |
| **PostgreSQL (Local)** | `votacao` | `votacao123` | `votacao` |
| **API** | - | - | - |

---

## 7. Verificação Pós-Execução

### 7.1. Verificar se Tudo Funciona

```bash
# 1. Testar se o backend responde
curl -s http://localhost:8080/actuator/health

# 2. Testar se a API de pautas responde
curl -s http://localhost:8080/api/v1/pautas

# 3. Verificar se o frontend carrega
# Acessar http://localhost:4200 no navegador

# 4. Verificar Swagger
open http://localhost:8080/swagger-ui.html
```

### 7.2. Expected Outputs

```
# Health Check
{"status":"UP"}

# Listar Pautas (vazio inicialmente)
[]

# Swagger UI
# Página com todos os endpoints listados
```

---

## 8. Estrutura de Pastas

```
desafio-votacao-fullstack/
├── .gitignore              # Arquivos a ignorar no Git
├── Dockerfile              # Imagem do backend
├── pom.xml               # Maven config
├── README.md             # Documentação completa
├── plano.md              # Plano de correções
├── desafio.md            # Requisitos originais
├── docker-compose.yml    # Orquestração Docker
├── frontend/             # Aplicação Angular
│   ├── src/              # Fonte TypeScript
│   ├── package.json      # Dependências Node
│   └── angular.json      # Config Angular CLI
├── src/                  # Código Java
│   ├── main/             # Fontes principais
│   │   ├── java/         # Código Java
│   │   └── resources/    # Configurações
│   │       └── db/migration/  # Flyway V1-V3
│   └── test/             # Testes unitários
└── temp_mvn.txt          # Log temporário do Maven
```

---

## 9. Próximos Passos Depois de Executado

### 9.1. Testar as Funcionalidades

1. **Criar uma pauta**: POST /api/v1/pautas
2. **Abrir sessão**: POST /api/v1/pautas/{id}/sessao
3. **Registrar voto**: POST /api/v1/pautas/{id}/votos
4. **Ver resultado**: GET /api/v1/pautas/{id}/

### 9.2. Testar os Bônus

1. **Bônus 1 (Fake Associado)**:
   - CPF inválido → Deve retornar 404
   - CPF válido → Pode votar ou não (aleatório)

2. **Bônus 2 (Performance)**:
   - Testar com muitas requisições simultâneas
   - Verificar tempos de resposta

3. **Bônus 3 (Versionamento)**:
   - Testar endpoints /api/v1/
   - Verificar que está usando v1 da API

### 9.3. Verificar Métricas

```bash
# Health detalhes
curl http://localhost:8080/actuator/health

# Métricas Prometheus (se habilitado)
curl http://localhost:8080/actuator/prometheus
```

---

## 9. Suporte

Para problemas durante a execução:

1. **Verificar logs**: `docker compose logs [serviço]`
2. **Limpar e reiniciar**: `docker compose down && docker compose up --build`
3. **Limpar cache local**: `mvn clean && rm -rf ~/.m2/repository/org/mockito/`
4. **Verificar versões**: `java -version`, `node -v`, `mvn -v`

---

## 📋 Resumo Rápido

```bash
# Docker (recomendado)
docker compose up --build

# Ou local
mvn spring-boot:run  # Backend (porta 8080)
cd frontend && ng serve  # Frontend (porta 4200)
```

**Pronto para uso em até 5 minutos com Docker!**

---
*Documentação gerada em 02/09/2026 para o desafio de votação fullstack*