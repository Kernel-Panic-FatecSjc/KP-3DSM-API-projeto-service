package com.kernelpanic.projeto_service.repositorios;

import java.math.BigDecimal;

public interface ProjetoFinanceiroProjection {
    Long getId();
    String getNome();
    String getStatus();
    BigDecimal getValorContratado();
}
