SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE projeto_profissionais;
TRUNCATE TABLE projetos;

INSERT INTO projetos (id, nome, descricao, status, prazo, valor_contratado, data_criacao, responsavel_id)
VALUES
  (1, 'Site Institucional', 'Criação do site da empresa', 'EM_ANDAMENTO', '2025-12-31 18:00:00', 15000.00, NOW(), 1),
  (2, 'App Mobile', 'Desenvolvimento do app Android e iOS', 'EM_PLANEJAMENTO', '2026-03-01 18:00:00', 28000.00, NOW(), 1),
  (3, 'Sistema ERP', 'Implantação do ERP interno', 'CONCLUIDO', '2024-06-30 18:00:00', 50000.00, NOW(), 1);

INSERT INTO projeto_profissionais (projeto_id, profissional_id)
VALUES
  (1, 3),
  (1, 4),
  (2, 3),
  (3, 4);

SET FOREIGN_KEY_CHECKS = 1;