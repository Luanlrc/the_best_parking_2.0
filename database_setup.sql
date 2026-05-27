-- ============================================================================
-- The Best Parking — Script de criação e povoamento do banco
-- ----------------------------------------------------------------------------
-- ATENÇÃO: este script APAGA as tabelas existentes e recria tudo do zero.
-- Use para resetar o ambiente de testes. Rode com o banco "estacionamento"
-- já selecionado (ex: no DBeaver/pgAdmin conectado nele, ou:
--   psql -U postgres -d estacionamento -f database_setup.sql
-- ============================================================================

-- 1) LIMPEZA (ordem inversa das dependências de FK) -------------------------
DROP TABLE IF EXISTS movimentacao   CASCADE;
DROP TABLE IF EXISTS automovel       CASCADE;
DROP TABLE IF EXISTS vaga            CASCADE;
DROP TABLE IF EXISTS tipo_automovel  CASCADE;
DROP TABLE IF EXISTS cliente         CASCADE;

-- 2) CRIAÇÃO DAS TABELAS -----------------------------------------------------

CREATE TABLE cliente (
    id_cliente          SERIAL PRIMARY KEY,
    nome                VARCHAR(100) NOT NULL,
    cpf                 VARCHAR(14),
    telefone_principal  VARCHAR(20)
);

CREATE TABLE tipo_automovel (
    id_tipo     SERIAL PRIMARY KEY,
    nome        VARCHAR(50) NOT NULL,
    valor_hora  NUMERIC(10, 2) NOT NULL
);

CREATE TABLE vaga (
    id_vaga  SERIAL PRIMARY KEY,
    numero   VARCHAR(10) NOT NULL,
    status   VARCHAR(20) NOT NULL
);

CREATE TABLE automovel (
    id_automovel  SERIAL PRIMARY KEY,
    placa         VARCHAR(8) NOT NULL UNIQUE,
    modelo        VARCHAR(80),
    cor           VARCHAR(30),
    mensalista    BOOLEAN NOT NULL DEFAULT FALSE,
    id_cliente    INTEGER REFERENCES cliente(id_cliente),
    id_tipo       INTEGER NOT NULL REFERENCES tipo_automovel(id_tipo)
);

CREATE TABLE movimentacao (
    id_movimentacao  SERIAL PRIMARY KEY,
    entrada          TIMESTAMP NOT NULL,
    saida            TIMESTAMP,
    valor_cobrado    NUMERIC(10, 2),
    id_automovel     INTEGER NOT NULL REFERENCES automovel(id_automovel),
    id_vaga          INTEGER NOT NULL REFERENCES vaga(id_vaga)
);

-- 3) POVOAMENTO --------------------------------------------------------------

-- 3.1) Tipos de automóvel (10)
INSERT INTO tipo_automovel (nome, valor_hora) VALUES
    ('Carro',        10.00),
    ('Moto',          5.00),
    ('Caminhao',     25.00),
    ('Van',          18.00),
    ('SUV',          12.00),
    ('Onibus',       30.00),
    ('Caminhonete',  15.00),
    ('Furgao',       20.00),
    ('Utilitario',   16.00),
    ('Esportivo',    22.00);

-- 3.2) Clientes (10)
INSERT INTO cliente (nome, cpf, telefone_principal) VALUES
    ('Joao da Silva',        '111.111.111-11', '(11) 98888-0001'),
    ('Maria Oliveira',       '222.222.222-22', '(11) 98888-0002'),
    ('Carlos Pereira',       '333.333.333-33', '(11) 98888-0003'),
    ('Ana Souza',            '444.444.444-44', '(11) 98888-0004'),
    ('Pedro Santos',         '555.555.555-55', '(11) 98888-0005'),
    ('Juliana Costa',        '666.666.666-66', '(11) 98888-0006'),
    ('Rafael Lima',          '777.777.777-77', '(11) 98888-0007'),
    ('Fernanda Almeida',     '888.888.888-88', '(11) 98888-0008'),
    ('Bruno Rodrigues',      '999.999.999-99', '(11) 98888-0009'),
    ('Camila Ferreira',      '101.010.101-01', '(11) 98888-0010');

-- 3.3) Vagas (10) — A01..A05 e B01..B05.
--      As vagas A01, A02 e A03 começam OCUPADAS (têm veículo no pátio).
INSERT INTO vaga (numero, status) VALUES
    ('A01', 'OCUPADA'),
    ('A02', 'OCUPADA'),
    ('A03', 'OCUPADA'),
    ('A04', 'LIVRE'),
    ('A05', 'LIVRE'),
    ('B01', 'LIVRE'),
    ('B02', 'LIVRE'),
    ('B03', 'LIVRE'),
    ('B04', 'LIVRE'),
    ('B05', 'LIVRE');

-- 3.4) Automóveis (10)
--      Os 3 primeiros são os que estão no pátio agora.
INSERT INTO automovel (placa, modelo, cor, mensalista, id_cliente, id_tipo) VALUES
    ('ABC1D23', 'Fiat Uno',          'Branco',   FALSE, 1, 1),
    ('DEF4G56', 'Honda CG 160',      'Preto',    FALSE, 2, 2),
    ('GHI7J89', 'VW Saveiro',        'Prata',    TRUE,  3, 7),
    ('JKL1M23', 'Toyota Corolla',    'Cinza',    FALSE, 4, 1),
    ('NOP4Q56', 'Hyundai HB20',      'Vermelho', FALSE, 5, 1),
    ('RST7U89', 'Ford Transit',      'Branco',   TRUE,  6, 4),
    ('VWX1Y23', 'Yamaha Fazer',      'Azul',     FALSE, 7, 2),
    ('ZAB4C56', 'Jeep Compass',      'Preto',    FALSE, 8, 5),
    ('DEF7G89', 'Mercedes Sprinter', 'Branco',   TRUE,  9, 8),
    ('HIJ1K23', 'Porsche 911',       'Amarelo',  FALSE, 10, 10);

-- 3.5) Movimentações (10)
--      3 ABERTAS (veículos 1,2,3 no pátio, sem saída) +
--      7 FECHADAS (histórico, com saída e valor cobrado).

-- Abertas (entraram há poucas horas, sem saída)
INSERT INTO movimentacao (entrada, saida, valor_cobrado, id_automovel, id_vaga) VALUES
    (NOW() - INTERVAL '2 hours',  NULL, NULL, 1, 1),
    (NOW() - INTERVAL '45 minutes', NULL, NULL, 2, 2),
    (NOW() - INTERVAL '5 hours',  NULL, NULL, 3, 3);

-- Fechadas (histórico)
INSERT INTO movimentacao (entrada, saida, valor_cobrado, id_automovel, id_vaga) VALUES
    (NOW() - INTERVAL '2 days'  - INTERVAL '3 hours', NOW() - INTERVAL '2 days',  30.00, 4, 4),
    (NOW() - INTERVAL '2 days'  - INTERVAL '1 hours', NOW() - INTERVAL '2 days',  10.00, 5, 5),
    (NOW() - INTERVAL '1 days'  - INTERVAL '5 hours', NOW() - INTERVAL '1 days',   0.00, 6, 6),  -- mensalista
    (NOW() - INTERVAL '1 days'  - INTERVAL '2 hours', NOW() - INTERVAL '1 days',  10.00, 7, 7),
    (NOW() - INTERVAL '8 hours',  NOW() - INTERVAL '5 hours',  36.00, 8, 8),
    (NOW() - INTERVAL '6 hours',  NOW() - INTERVAL '4 hours',   0.00, 9, 9),                      -- mensalista
    (NOW() - INTERVAL '3 hours',  NOW() - INTERVAL '1 hours',  44.00, 10, 10);

-- ============================================================================
-- FIM. Conferência rápida:
--   SELECT 'clientes' t, COUNT(*) FROM cliente
--   UNION ALL SELECT 'tipos',         COUNT(*) FROM tipo_automovel
--   UNION ALL SELECT 'vagas',         COUNT(*) FROM vaga
--   UNION ALL SELECT 'automoveis',    COUNT(*) FROM automovel
--   UNION ALL SELECT 'movimentacoes', COUNT(*) FROM movimentacao;
-- ============================================================================
