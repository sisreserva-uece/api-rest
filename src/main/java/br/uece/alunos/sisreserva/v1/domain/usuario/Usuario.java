package br.uece.alunos.sisreserva.v1.domain.usuario;

import br.uece.alunos.sisreserva.v1.domain.cargo.Cargo;
import br.uece.alunos.sisreserva.v1.domain.instituicao.Instituicao;
import br.uece.alunos.sisreserva.v1.dto.usuario.AtualizarUsuarioDTO;
import br.uece.alunos.sisreserva.v1.dto.usuario.UsuarioDTO;
import br.uece.alunos.sisreserva.v1.dto.usuario.UsuarioEsqueciSenhaDTO;
import br.uece.alunos.sisreserva.v1.domain.usuarioCargo.UsuarioCargo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Table(name = "usuario")
@Entity(name = "Usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails {

    @Id
    @Column(name = "id", nullable = false, length = 36, updatable = false)
    private String id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "senha", nullable = false, length = 100)
    private String senha;

    @Column(name = "matricula", nullable = false)
    private Integer matricula;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "documento_fiscal", nullable = false, length = 18)
    private String documentoFiscal;

    @Column(name = "foto_perfil", length = 100)
    private String fotoPerfil;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instituicao_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Instituicao instituicao;

    @Column(name = "access_failed_count")
    private int accessFailedCount = 0;

    @Column(name = "lockout_enabled")
    private boolean lockoutEnabled = false;

    @Column(name = "lockout_end")
    private LocalDateTime lockoutEnd;

    @Column(name = "token_expiration")
    private LocalDateTime tokenExpiration;

    @Column(name = "token_mail")
    private String tokenMail;

    @Column(name = "refresh_token_enabled")
    private boolean refreshTokenEnabled = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UsuarioCargo> usuarioCargos = new ArrayList<>();

    public Usuario(UsuarioDTO data, Instituicao instituicao) {
        this.nome = data.nome();
        this.senha = data.senha();
        this.email = data.email();
        this.documentoFiscal = data.documentoFiscal();
        this.fotoPerfil = data.fotoPerfil();
        this.matricula = data.matricula();
        this.telefone = data.telefone();
        this.instituicao = instituicao;
        this.refreshTokenEnabled = data.refreshTokenEnabled();
    }

    public List<String> getRoles() {
        return usuarioCargos.stream()
                .map(UsuarioCargo::getCargo)
                .filter(Objects::nonNull)
                .map(Cargo::getNome)
                .distinct()
                .toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toSet());

        if (authorities.stream().noneMatch(auth -> auth.getAuthority().equals("ROLE_USER"))) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return authorities;
    }

    public void atualizarUsuario(AtualizarUsuarioDTO data, Instituicao instituicao) {
        if (data.nome() != null && !data.nome().isBlank()) {
            this.nome = data.nome();
        }

        if (data.fotoPerfil() != null && !data.fotoPerfil().isBlank()) {
            this.fotoPerfil = data.fotoPerfil();
        }

        if (data.matricula() != null && data.matricula() > 0) {
            this.matricula = data.matricula();
        }

        if (data.telefone() != null && !data.telefone().isBlank()) {
            this.telefone = data.telefone();
        }

        if (instituicao != null) {
            this.instituicao = instituicao;
        }

        if (data.refreshTokenEnabled() != null) {
            this.refreshTokenEnabled = data.refreshTokenEnabled();
        }
    }

    public void resetAccessCount() {
        this.accessFailedCount = 0;
        this.setLockoutEnabled(false);
        this.setLockoutEnd(null);
    }

    public void esqueciSenha(UsuarioEsqueciSenhaDTO data) {
        this.tokenMail = data.tokenMail();
        this.tokenExpiration = data.tokenExpiration();
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !lockoutEnabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !lockoutEnabled;
    }

    @PrePersist
    public void onCreate() {
        this.id = UUID.randomUUID().toString().toUpperCase();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
