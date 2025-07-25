package br.uece.alunos.sisreserva.v1.service.impl;

import br.uece.alunos.sisreserva.v1.domain.usuario.useCase.*;
import br.uece.alunos.sisreserva.v1.dto.usuario.*;
import br.uece.alunos.sisreserva.v1.dto.utils.TokenDTO;
import br.uece.alunos.sisreserva.v1.dto.utils.AuthTokensDTO;
import br.uece.alunos.sisreserva.v1.dto.utils.MessageResponseDTO;
import br.uece.alunos.sisreserva.v1.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AtualizaTokenAcesso atualizaTokenAcesso;
    private final AtualizarUsuario atualizarUsuario;
    private final CriarUsuario criarUsuario;
    private final EsqueciSenha esqueciSenha;
    private final ObterUsuarioPorJWT obterUsuarioPorJWT;
    private final ObterUsuarios obterUsuarios;
    private final RealizarLogin realizarLogin;
    private final RealizarLogout realizarLogout;
    private final TrocarSenha trocarSenha;

    @Override
    public TokenDTO atualizarToken(String refreshToken) {
        return atualizaTokenAcesso.atualizaToken(refreshToken);
    }

    @Override
    public UsuarioRetornoDTO atualizarUsuario(AtualizarUsuarioDTO data, String idUsuario) {
        return atualizarUsuario.atualizarUsuario(data, idUsuario);
    }

    @Override
    public UsuarioRetornoDTO criarUsuario(UsuarioDTO data) {
        return criarUsuario.criar(data);
    }

    @Override
    public MessageResponseDTO esqueciMinhaSenha(UsuarioEmailDTO data) {
        return esqueciSenha.esqueciMinhaSenha(data);
    }

    @Override
    public AuthTokensDTO login(UsuarioLoginDTO data, HttpServletRequest request) {
        return realizarLogin.login(data, request);
    }

    @Override
    public UsuarioRetornoDTO obterPorTokenJwt(String tokenJWT) {
        return obterUsuarioPorJWT.obterPorTokenJwt(tokenJWT);
    }

    @Override
    public Page<UsuarioRetornoDTO> obter(Pageable pageable, String id, Integer matricula, String email, String documentoFiscal, String instituicaoId, String cargoId, String nome) {
        return obterUsuarios.obter(pageable, id, matricula, email, documentoFiscal, instituicaoId, cargoId, nome);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        realizarLogout.logout(request, response);
    }

    @Override
    public MessageResponseDTO resetarSenha(UsuarioTrocarSenhaDTO data) {
        return trocarSenha.resetarSenha(data);
    }
}
