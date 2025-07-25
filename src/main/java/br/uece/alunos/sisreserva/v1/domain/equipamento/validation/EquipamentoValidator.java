package br.uece.alunos.sisreserva.v1.domain.equipamento.validation;

import br.uece.alunos.sisreserva.v1.domain.equipamento.EquipamentoRepository;
import br.uece.alunos.sisreserva.v1.domain.tipoEquipamento.TipoEquipamento;
import br.uece.alunos.sisreserva.v1.dto.equipamento.EquipamentoDTO;
import br.uece.alunos.sisreserva.v1.infra.exceptions.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class EquipamentoValidator {

    @Autowired
    private EquipamentoRepository repository;

    public void validarDadosObrigatorios(EquipamentoDTO data, TipoEquipamento tipoEquipamento) {
        if (tipoEquipamento.getIsDetalhamentoObrigatorio()) {
            if (!StringUtils.hasText(data.tombamento()) || !StringUtils.hasText(data.descricao())) {
                throw new ValidationException("Para o tipo de equipamento '" + tipoEquipamento.getNome() +
                        "', os campos 'tombamento' e 'descrição' são obrigatórios.");
            }
        }
    }

    public void validarTombamentoUnico(String tombamento) {
        if (tombamento == null || tombamento.trim().isEmpty()) {
            return;
        }

        if (repository.existsByTombamento(tombamento.trim())) {
            throw new ValidationException("Já existe um equipamento com o tombamento informado.");
        }
    }

    public void validarEquipamentoId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("O ID do equipamento não pode ser nulo ou vazio.");
        }

        if (!repository.existsById(id)) {
            throw new ValidationException("Equipamento com o ID fornecido não existe.");
        }
    }
}
