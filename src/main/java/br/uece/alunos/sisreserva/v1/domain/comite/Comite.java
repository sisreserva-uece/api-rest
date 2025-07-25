package br.uece.alunos.sisreserva.v1.domain.comite;

import br.uece.alunos.sisreserva.v1.dto.comite.ComiteAtualizarDTO;
import br.uece.alunos.sisreserva.v1.dto.comite.ComiteDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "comite")
@Entity(name = "Comite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Comite {
    @Id
    @Column(name = "id", nullable = false, length = 36, updatable = false)
    private String id;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "tipo", nullable = false)
    private TipoComite tipo = TipoComite.GESTOR;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Comite(ComiteDTO data) {
        this.descricao = data.descricao();
        this.tipo = data.tipo();
    }

    @PrePersist
    public void onCreate() {
        this.id = UUID.randomUUID().toString().toUpperCase();
        this.createdAt = LocalDateTime.now();
    }

    public void atualizarComite(ComiteAtualizarDTO data) {
        if (data.descricao() != null && !data.descricao().isEmpty()) {
            this.descricao = data.descricao();
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}