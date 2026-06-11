# SisOficina — Sistema de Gestão de Oficina Mecânica

Projeto acadêmico Java com PostgreSQL para gerenciar clientes, veículos, mecânicos, peças e ordens de serviço de uma oficina mecânica.

---

## Como Executar

### Pré-requisitos

- Java 17+
- PostgreSQL (rodando localmente na porta 5432)
- Maven 3.6+

### Passo a passo

**1. Criar o banco de dados**

```sql
CREATE DATABASE sisoficina;
```

**2. Executar o script SQL**

O arquivo `src/main/resources/banco/schema.sql` contém DDL (tabelas, view, function, procedures) e DML (dados de exemplo) em um único script.

```bash
psql -U postgres -d sisoficina -f src/main/resources/banco/schema.sql
```

**3. Configurar as credenciais**

Abra `src/main/java/conectaBD/testeConexaoBD.java` e ajuste as variáveis no início do `main`:

```java
String url     = "jdbc:postgresql://localhost:5432/sisoficina";
String usuario = "postgres";
String senha   = "123456";  // altere para a senha do seu PostgreSQL
```

**4. Compilar o projeto**

```bash
mvn compile
```

**5. Executar**

Pelo IntelliJ: botão direito em `testeConexaoBD.java` → *Run*.

Pela linha de comando:

```bash
mvn exec:java -Dexec.mainClass="conectaBD.testeConexaoBD"
```

---

## Descrição do Sistema

**SisOficina** é uma aplicação de terminal (CLI) que gerencia o fluxo completo de uma oficina mecânica: cadastro de clientes e seus veículos, controle de mecânicos, estoque de peças e ciclo de vida das ordens de serviço — da abertura ao fechamento com cálculo automático de valor total.

| Item | Detalhe |
|---|---|
| Linguagem | Java 17 |
| Banco de dados | PostgreSQL |
| Acesso ao banco | JDBC puro (sem ORM) |
| Interface | Terminal via `Scanner` |
| Build | Maven |
| Dependência principal | `postgresql` JDBC 42.7.3 |

---

## Arquitetura do Código

O projeto segue uma arquitetura em camadas dentro do pacote `conectaBD`:

```
conectaBD/
├── testeConexaoBD.java       ← ponto de entrada, abre a conexão JDBC
├── model/                    ← POJOs das entidades
├── repository/               ← SQL direto via JDBC (acesso ao BD)
├── service/                  ← regras de negócio
└── menu/                     ← interface com o usuário (Scanner)
```

### Módulos do menu principal

| Opção | Módulo | Operações disponíveis |
|---|---|---|
| 1 | Clientes | Listar, buscar, cadastrar, atualizar, excluir |
| 2 | Veículos | Listar, buscar, cadastrar, atualizar, excluir |
| 3 | Mecânicos | Listar, buscar, cadastrar, atualizar, excluir |
| 4 | Peças | Listar, buscar, cadastrar, atualizar, excluir |
| 5 | Ordens de Serviço | Listar, buscar, cadastrar, atualizar, excluir, adicionar/remover peça, fechar, cancelar |
| 6 | Relatórios | Todas as OS, OS por cliente, OS por mecânico, peças de uma OS |

---

## Banco de Dados

### Tabelas

| Tabela | Campos principais |
|---|---|
| `cliente` | id, nome, cpf (único), telefone, email |
| `veiculo` | id, placa (única), modelo, marca, ano, cor, id_cliente (FK) |
| `mecanico` | id, nome, cpf (único), especialidade, telefone |
| `peca` | id, nome, descricao, preco_unitario, quantidade_estoque |
| `ordem_servico` | id, data_abertura, data_fechamento, status, valor_mao_obra, valor_total, id_veiculo (FK), id_mecanico (FK) |
| `item_os` | id, id_os (FK), id_peca (FK), quantidade, preco_unitario_momento |

O campo `status` da OS aceita somente: `ABERTA`, `EM_ANDAMENTO`, `CONCLUIDA`, `CANCELADA`.

O campo `preco_unitario_momento` em `item_os` registra o preço da peça no momento da inclusão, preservando o histórico mesmo que o preço mude depois.

### Relacionamentos

```
cliente (1) ──→ (N) veiculo
veiculo  (1) ──→ (N) ordem_servico
mecanico (1) ──→ (N) ordem_servico
ordem_servico (1) ──→ (N) item_os
peca     (1) ──→ (N) item_os
```

### View

**`vw_os_completa`** — consolida em uma única consulta os dados de `ordem_servico`, `veiculo`, `cliente` e `mecanico`. Usada nos relatórios.

```sql
SELECT os.id, os.status, os.data_abertura, os.data_fechamento,
       os.valor_mao_obra, os.valor_total,
       c.nome AS cliente, v.placa, v.modelo, m.nome AS mecanico
FROM ordem_servico os
JOIN veiculo v  ON os.id_veiculo  = v.id
JOIN cliente c  ON v.id_cliente   = c.id
JOIN mecanico m ON os.id_mecanico = m.id;
```

### Function

**`fn_calcular_total_os(p_id_os INT) RETURNS NUMERIC`**

Calcula o valor total de uma OS somando `(quantidade × preco_unitario_momento)` de todos os itens mais o `valor_mao_obra`. Retorna `0` se não houver itens.

### Procedures

**`sp_fechar_os(p_id_os INT)`**

Encerra uma OS com status `ABERTA` ou `EM_ANDAMENTO`. Operações realizadas em sequência:
1. Valida que o status permite fechamento (lança exceção caso contrário).
2. Decrementa `quantidade_estoque` de cada peça usada.
3. Chama `fn_calcular_total_os` para calcular o valor final.
4. Atualiza o status para `CONCLUIDA`, registra `data_fechamento` e grava `valor_total`.

**`sp_atualizar_total_os(p_id_os INT)`**

Recalcula e atualiza `valor_total` de uma OS sem fechá-la. Útil após adicionar ou remover peças de uma OS em andamento.
