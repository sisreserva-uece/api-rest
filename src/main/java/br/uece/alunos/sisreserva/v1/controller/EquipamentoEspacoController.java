
package br.uece.alunos.sisreserva.v1.controller;

import br.uece.alunos.sisreserva.v1.dto.equipamentoEspaco.CriarEquipamentoEspacoDTO;
import br.uece.alunos.sisreserva.v1.dto.equipamentoEspaco.EquipamentoEspacoRetornoDTO;
import br.uece.alunos.sisreserva.v1.dto.utils.ApiResponseDTO;
import br.uece.alunos.sisreserva.v1.service.EquipamentoEspacoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/equipamento/espaco")
@Tag(name = "Rotas de equipamentos de um espaço mapeadas no controller")
public class EquipamentoEspacoController {
    @Autowired
    private EquipamentoEspacoService service;

    /*
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponseDTO<GestorEspacoRetornoDTO>> inativarGestorEspaco(@PathVariable String id) {
        gestorEspacoService.inativar(id);
        return ResponseEntity.noContent().build();
    } */

    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<EquipamentoEspacoRetornoDTO>>> obter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String equipamentoId,
            @RequestParam(required = false) String tipoEquipamentoId,
            @RequestParam(required = false) String espacoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(required = false) String tipoEquipamentoNome,
            @RequestParam(required = false) String espacoNome
    ) {
        var fieldToSort = switch (sortField) {
            case "equipamento" -> "equipamento.id";
            case "espaco" -> "espaco.id";
            default -> sortField;
        };

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), fieldToSort));

        Page<EquipamentoEspacoRetornoDTO> pageResultado = service.obter(
                pageable,
                id,
                equipamentoId,
                tipoEquipamentoId,
                espacoId,
                dataInicio,
                dataFim,
                tipoEquipamentoNome,
                espacoNome
        );

        return ResponseEntity.ok(ApiResponseDTO.success(pageResultado));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponseDTO<EquipamentoEspacoRetornoDTO>> cadastrar(
            @RequestBody @Valid CriarEquipamentoEspacoDTO data) {
        var retornoDTO = service.criarEquipamentoAlocandoAoEspaco(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDTO.success(retornoDTO));
    }
}