package br.uece.alunos.sisreserva.v1.dto.tipoEquipamento;

import br.uece.alunos.sisreserva.v1.domain.tipoEquipamento.TipoEquipamento;

public record TipoEquipamentoRetornoDTO(String id, String nome, boolean isDetalhamentoObrigatorio) {
    public TipoEquipamentoRetornoDTO(TipoEquipamento tipoEquipamento) {
        this(tipoEquipamento.getId(), tipoEquipamento.getNome(), tipoEquipamento.getIsDetalhamentoObrigatorio());
    }
}
