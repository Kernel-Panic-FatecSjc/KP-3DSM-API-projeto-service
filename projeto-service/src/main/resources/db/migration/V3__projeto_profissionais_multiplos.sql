CREATE TABLE IF NOT EXISTS projeto_profissionais (
    projeto_id BIGINT NOT NULL,
    profissional_id BIGINT NOT NULL
);

DROP PROCEDURE IF EXISTS ajustar_projeto_profissionais_multiplos;

CREATE PROCEDURE ajustar_projeto_profissionais_multiplos()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE idx_name VARCHAR(255);
    DECLARE idx_cursor CURSOR FOR
        SELECT s.index_name
        FROM information_schema.statistics s
        WHERE s.table_schema = DATABASE()
          AND s.table_name = 'projeto_profissionais'
          AND s.non_unique = 0
        GROUP BY s.index_name
        HAVING COUNT(*) = 1
           AND MAX(s.column_name = 'projeto_id') = 1;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    IF EXISTS (
        SELECT 1
        FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu
          ON tc.constraint_schema = kcu.constraint_schema
         AND tc.table_name = kcu.table_name
         AND tc.constraint_name = kcu.constraint_name
        WHERE tc.constraint_schema = DATABASE()
          AND tc.table_name = 'projeto_profissionais'
          AND tc.constraint_type = 'PRIMARY KEY'
        GROUP BY tc.constraint_name
        HAVING COUNT(*) = 1
           AND MAX(kcu.column_name = 'projeto_id') = 1
    ) THEN
        ALTER TABLE projeto_profissionais DROP PRIMARY KEY;
    END IF;

    OPEN idx_cursor;

    read_loop: LOOP
        FETCH idx_cursor INTO idx_name;
        IF done THEN
            LEAVE read_loop;
        END IF;

        IF idx_name <> 'PRIMARY' THEN
            SET @sql = CONCAT('ALTER TABLE projeto_profissionais DROP INDEX `', idx_name, '`');
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;
    END LOOP;

    CLOSE idx_cursor;


    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'projeto_profissionais'
          AND index_name = 'uk_projeto_profissional'
    ) THEN
        ALTER TABLE projeto_profissionais
        ADD CONSTRAINT uk_projeto_profissional UNIQUE (projeto_id, profissional_id);
    END IF;
END;

CALL ajustar_projeto_profissionais_multiplos();

DROP PROCEDURE ajustar_projeto_profissionais_multiplos;