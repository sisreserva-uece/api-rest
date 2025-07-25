package br.uece.alunos.sisreserva.v1.domain.equipamento;

import br.uece.alunos.sisreserva.v1.dto.espaco.EspacoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EquipamentoRepository extends JpaRepository<Equipamento, String>, JpaSpecificationExecutor<Equipamento> {
    @Query("SELECT e FROM Equipamento e ORDER BY e.tombamento ASC")
    Page<Equipamento> findAllOrderedByTombamento(Pageable pageable);

    @Query("SELECT e FROM Equipamento e WHERE LOWER(e.tombamento) = LOWER(:tombamento)")
    Equipamento findByTombamento(String tombamento);

    @Query("SELECT e FROM Equipamento e WHERE LOWER(TRIM(e.tombamento)) IN :tombamentos")
    List<Equipamento> findAllByTombamentosIgnoreCaseAndTrimmed(List<String> tombamentos);

    @Query("SELECT COUNT(*) > 0 FROM Equipamento WHERE tombamento = :tombamento")
    boolean existsByTombamento(String tombamento);

    @Query("""
    SELECT new br.uece.alunos.sisreserva.v1.dto.espaco.EspacoProjection(
        ee.espaco.id
    )
    FROM EquipamentoEspaco ee
    WHERE ee.equipamento.id = :equipamentoId
    """)
    Optional<EspacoProjection> findEspacoIdJoinedByEquipamentoId(String equipamentoId);
}
