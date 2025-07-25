package br.uece.alunos.sisreserva.v1.dto.equipamentoEspaco;

import br.uece.alunos.sisreserva.v1.domain.equipamentoEspaco.EquipamentoEspaco;
import br.uece.alunos.sisreserva.v1.dto.equipamento.EquipamentoRetornoDTO;
import br.uece.alunos.sisreserva.v1.dto.espaco.EspacoRetornoDTO;

import java.time.LocalDateTime;

public record EquipamentoEspacoRetornoDTO(String id, EquipamentoRetornoDTO equipamento,
                                          EspacoRetornoDTO espaco, LocalDateTime dataAlocacao, LocalDateTime dataRemocao) {
    public EquipamentoEspacoRetornoDTO(EquipamentoEspaco e) {
        this(e.getId(), new EquipamentoRetornoDTO(e.getEquipamento()),
                new EspacoRetornoDTO(e.getEspaco()), e.getDataAlocacao(), e.getDataRemocao());
    }

}
