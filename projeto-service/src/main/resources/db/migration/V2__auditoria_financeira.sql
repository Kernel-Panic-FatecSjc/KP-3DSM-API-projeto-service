CREATE TABLE IF NOT EXISTS auditoria_financeira (
    id BIGINT NOT NULL AUTO_INCREMENT,
    data_hora DATETIME(6) NOT NULL,
    projeto_id BIGINT NULL,
    projeto_nome VARCHAR(100) NULL,
    tipo VARCHAR(40) NOT NULL,
    descricao VARCHAR(300) NULL,
    impacto DECIMAL(14,2) NULL,
    PRIMARY KEY (id)
);
