-- Carga inicial opcional (nao usada na demonstracao do video)
INSERT INTO dbo.TB_CLIENTE (NOME, CPF, EMAIL)
VALUES (N'Cliente Exemplo', '000.000.000-00', N'exemplo@dimdim.com');

INSERT INTO dbo.TB_CONTA (ID_CLIENTE, NUMERO_CONTA, TIPO_CONTA, SALDO)
VALUES (1, '0001-1', 'CORRENTE', 100.00);
