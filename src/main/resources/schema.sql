CREATE TABLE IF NOT EXISTS cliente (
    id_cliente          SERIAL PRIMARY KEY,
    nome                VARCHAR(100) NOT NULL,
    cpf                 VARCHAR(14),
    telefone_principal  VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS tipo_automovel (
    id_tipo     SERIAL PRIMARY KEY,
    nome        VARCHAR(50) NOT NULL,
    valor_hora  NUMERIC(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS vaga (
    id_vaga  SERIAL PRIMARY KEY,
    numero   VARCHAR(10) NOT NULL,
    status   VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS automovel (
    id_automovel  SERIAL PRIMARY KEY,
    placa         VARCHAR(8) NOT NULL UNIQUE,
    modelo        VARCHAR(80),
    cor           VARCHAR(30),
    mensalista    BOOLEAN NOT NULL DEFAULT FALSE,
    id_cliente    INTEGER REFERENCES cliente(id_cliente),
    id_tipo       INTEGER NOT NULL REFERENCES tipo_automovel(id_tipo)
);

CREATE TABLE IF NOT EXISTS movimentacao (
    id_movimentacao  SERIAL PRIMARY KEY,
    entrada          TIMESTAMP NOT NULL,
    saida            TIMESTAMP,
    valor_cobrado    NUMERIC(10, 2),
    id_automovel     INTEGER NOT NULL REFERENCES automovel(id_automovel),
    id_vaga          INTEGER NOT NULL REFERENCES vaga(id_vaga)
);
