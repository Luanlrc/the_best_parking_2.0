INSERT INTO tipo_automovel (nome, valor_hora) VALUES
    ('Carro',     10.00),
    ('Moto',       5.00),
    ('Caminhao',  20.00)
ON CONFLICT DO NOTHING;

INSERT INTO vaga (numero, status) VALUES
    ('A01', 'LIVRE'),
    ('A02', 'LIVRE'),
    ('A03', 'LIVRE'),
    ('B01', 'LIVRE'),
    ('B02', 'LIVRE'),
    ('M01', 'LIVRE'),
    ('M02', 'LIVRE')
ON CONFLICT DO NOTHING;

INSERT INTO cliente (nome, cpf, telefone_principal) VALUES
    ('Joao da Silva',    '111.111.111-11', '(11) 99999-0001'),
    ('Maria Oliveira',   '222.222.222-22', '(11) 99999-0002'),
    ('Carlos Pereira',   '333.333.333-33', '(11) 99999-0003')
ON CONFLICT DO NOTHING;
