-- ============================================================
-- SisOficina - Schema DDL + DML
-- Execute com: psql -U postgres -d sisoficina -f schema.sql
-- ============================================================

-- Limpeza (ordem reversa das dependencias)
DROP TABLE IF EXISTS item_os         CASCADE;
DROP TABLE IF EXISTS ordem_servico   CASCADE;
DROP TABLE IF EXISTS peca            CASCADE;
DROP TABLE IF EXISTS veiculo         CASCADE;
DROP TABLE IF EXISTS mecanico        CASCADE;
DROP TABLE IF EXISTS cliente         CASCADE;
DROP VIEW  IF EXISTS vw_os_completa  CASCADE;
DROP FUNCTION  IF EXISTS fn_calcular_total_os(INT) CASCADE;
DROP PROCEDURE IF EXISTS sp_fechar_os(INT)         CASCADE;

-- ============================================================
-- TABELAS
-- ============================================================

CREATE TABLE cliente (
    id        SERIAL PRIMARY KEY,
    nome      VARCHAR(100) NOT NULL,
    cpf       VARCHAR(14)  UNIQUE NOT NULL,
    telefone  VARCHAR(20),
    email     VARCHAR(100)
);

CREATE TABLE veiculo (
    id         SERIAL PRIMARY KEY,
    placa      VARCHAR(10) UNIQUE NOT NULL,
    modelo     VARCHAR(50) NOT NULL,
    marca      VARCHAR(50) NOT NULL,
    ano        INT,
    cor        VARCHAR(30),
    id_cliente INT NOT NULL REFERENCES cliente(id)
);

CREATE TABLE mecanico (
    id           SERIAL PRIMARY KEY,
    nome         VARCHAR(100) NOT NULL,
    cpf          VARCHAR(14)  UNIQUE NOT NULL,
    especialidade VARCHAR(50),
    telefone     VARCHAR(20)
);

CREATE TABLE peca (
    id                 SERIAL PRIMARY KEY,
    nome               VARCHAR(100)   NOT NULL,
    descricao          VARCHAR(255),
    preco_unitario     NUMERIC(10, 2) NOT NULL,
    quantidade_estoque INT DEFAULT 0
);

CREATE TABLE ordem_servico (
    id              SERIAL PRIMARY KEY,
    data_abertura   TIMESTAMP DEFAULT now(),
    data_fechamento TIMESTAMP,
    status          VARCHAR(20) NOT NULL DEFAULT 'ABERTA'
                    CHECK (status IN ('ABERTA', 'EM_ANDAMENTO', 'CONCLUIDA', 'CANCELADA')),
    valor_mao_obra  NUMERIC(10, 2),
    valor_total     NUMERIC(10, 2),
    id_veiculo      INT NOT NULL REFERENCES veiculo(id),
    id_mecanico     INT NOT NULL REFERENCES mecanico(id)
);

CREATE TABLE item_os (
    id                    SERIAL PRIMARY KEY,
    id_os                 INT            NOT NULL REFERENCES ordem_servico(id) ON DELETE CASCADE,
    id_peca               INT            NOT NULL REFERENCES peca(id),
    quantidade            INT            NOT NULL,
    preco_unitario_momento NUMERIC(10, 2) NOT NULL
);

-- ============================================================
-- VIEW
-- ============================================================

CREATE VIEW vw_os_completa AS
SELECT
    os.id,
    os.status,
    os.data_abertura,
    os.data_fechamento,
    os.valor_mao_obra,
    os.valor_total,
    c.nome  AS cliente,
    v.placa,
    v.modelo,
    m.nome  AS mecanico
FROM ordem_servico os
JOIN veiculo  v ON os.id_veiculo  = v.id
JOIN cliente  c ON v.id_cliente   = c.id
JOIN mecanico m ON os.id_mecanico = m.id;

-- ============================================================
-- FUNCTION
-- ============================================================

CREATE OR REPLACE FUNCTION fn_calcular_total_os(p_id_os INT)
RETURNS NUMERIC AS $$
DECLARE
    v_total    NUMERIC;
    v_mao_obra NUMERIC;
BEGIN
    SELECT COALESCE(SUM(quantidade * preco_unitario_momento), 0)
    INTO v_total
    FROM item_os
    WHERE id_os = p_id_os;

    SELECT COALESCE(valor_mao_obra, 0)
    INTO v_mao_obra
    FROM ordem_servico
    WHERE id = p_id_os;

    RETURN v_total + v_mao_obra;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- PROCEDURE
-- ============================================================

CREATE OR REPLACE PROCEDURE sp_fechar_os(p_id_os INT)
LANGUAGE plpgsql AS $$
DECLARE
    v_status VARCHAR;
    v_total  NUMERIC;
    rec      RECORD;
BEGIN
    SELECT status INTO v_status
    FROM ordem_servico
    WHERE id = p_id_os;

    IF v_status NOT IN ('ABERTA', 'EM_ANDAMENTO') THEN
        RAISE EXCEPTION 'OS % nao pode ser fechada. Status atual: %', p_id_os, v_status;
    END IF;

    FOR rec IN
        SELECT id_peca, quantidade FROM item_os WHERE id_os = p_id_os
    LOOP
        UPDATE peca
        SET quantidade_estoque = quantidade_estoque - rec.quantidade
        WHERE id = rec.id_peca;
    END LOOP;

    v_total := fn_calcular_total_os(p_id_os);

    UPDATE ordem_servico SET
        status          = 'CONCLUIDA',
        data_fechamento = now(),
        valor_total     = v_total
    WHERE id = p_id_os;
END;
$$;

-- ============================================================
-- DML — Dados de exemplo
-- ============================================================

INSERT INTO cliente (nome, cpf, telefone, email) VALUES
    ('Joao Silva',     '123.456.789-00', '(11) 99999-0001', 'joao@email.com'),
    ('Maria Santos',   '234.567.890-11', '(11) 99999-0002', 'maria@email.com'),
    ('Pedro Oliveira', '345.678.901-22', '(11) 99999-0003', 'pedro@email.com');

INSERT INTO veiculo (placa, modelo, marca, ano, cor, id_cliente) VALUES
    ('ABC-1234', 'Civic',  'Honda',       2020, 'Preto',  1),
    ('DEF-5678', 'Corolla','Toyota',      2019, 'Branco', 2),
    ('GHI-9012', 'Gol',    'Volkswagen',  2018, 'Prata',  3);

INSERT INTO mecanico (nome, cpf, especialidade, telefone) VALUES
    ('Carlos Menezes', '456.789.012-33', 'Motor',     '(11) 98888-0001'),
    ('Antonio Lima',   '567.890.123-44', 'Suspensao', '(11) 98888-0002');

INSERT INTO peca (nome, descricao, preco_unitario, quantidade_estoque) VALUES
    ('Filtro de Oleo',       'Filtro de oleo para motor',       35.90,  50),
    ('Vela de Ignicao',      'Vela de ignicao NGK',             25.50, 100),
    ('Correia Dentada',      'Correia dentada kit completo',   189.00,  20),
    ('Amortecedor Dianteiro','Amortecedor dianteiro Monroe',   320.00,  15),
    ('Pastilha de Freio',    'Pastilha de freio dianteira Bosch', 85.00, 40);

INSERT INTO ordem_servico (status, valor_mao_obra, id_veiculo, id_mecanico) VALUES
    ('EM_ANDAMENTO', 150.00, 1, 1),
    ('ABERTA',         0.00, 2, 2);

INSERT INTO item_os (id_os, id_peca, quantidade, preco_unitario_momento) VALUES
    (1, 1, 2, 35.90),
    (1, 2, 4, 25.50),
    (2, 3, 1, 189.00);
