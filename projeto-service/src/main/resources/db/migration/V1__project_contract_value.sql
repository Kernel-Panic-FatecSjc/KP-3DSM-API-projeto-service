DROP PROCEDURE IF EXISTS add_projeto_valor_contratado_if_missing;

CREATE PROCEDURE add_projeto_valor_contratado_if_missing()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = 'projetos'
          AND column_name = 'valor_contratado'
    ) THEN
        ALTER TABLE projetos ADD COLUMN valor_contratado DECIMAL(14,2) NULL;
    END IF;
END;

CALL add_projeto_valor_contratado_if_missing();

DROP PROCEDURE add_projeto_valor_contratado_if_missing;
