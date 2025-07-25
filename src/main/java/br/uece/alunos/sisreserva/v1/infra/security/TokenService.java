package br.uece.alunos.sisreserva.v1.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.uece.alunos.sisreserva.v1.domain.usuario.Usuario;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class TokenService {

    @Value("${api.security.access.secret}")
    private String accessSecret;

    @Value("${api.security.refresh.secret}")
    private String refreshSecret;

    public String generateAccessToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(accessSecret);

            String token = JWT.create()
                    .withIssuer("sisreserva-api")
                    .withSubject(usuario.getEmail())
                    .withClaim("id", usuario.getId())
                    .withClaim("role", usuario.getRoles().toString())
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(accessTokenExpirationDate())
                    .sign(algorithm);

            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro enquanto gerava o token JWT de acesso.", exception);
        }
    }

    public String generateRefreshToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(refreshSecret);
            var builder = JWT.create()
                    .withIssuer("sisreserva-api")
                    .withSubject(usuario.getEmail())
                    .withClaim("refreshId", UUID.randomUUID().toString())
                    .withIssuedAt(Instant.now());

            if (usuario.isRefreshTokenEnabled()) {
                builder.withExpiresAt(refreshTokenExpirationDate());
            }

            return builder.sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro enquanto gerava o token JWT de persistência (refresh).", exception);
        }
    }

    public boolean isAccessTokenValid(String tokenJwt) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(accessSecret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("sisreserva-api")
                    .build();

            DecodedJWT jwt = verifier.verify(tokenJwt);

            return true;
        } catch (JWTVerificationException | IllegalArgumentException exception) {
            // IllegalArgumentException é lançada se o tokenJwt for nulo ou vazio
            return false;
        }
    }

    public boolean isRefreshTokenValid(String refreshToken) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(refreshSecret);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer("sisreserva-api").build();
            DecodedJWT jwt = verifier.verify(refreshToken);

            return true;
        } catch (JWTVerificationException | IllegalArgumentException exception) {
            return false;
        }
    }

    public String getSubject(String tokenJwt) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(accessSecret);
            String emailUsuarioLogado = JWT.require(algorithm)
                    .withIssuer("sisreserva-api")
                    .build()
                    .verify(tokenJwt)
                    .getSubject();

            return emailUsuarioLogado;
        } catch (JWTVerificationException exception){
            throw new RuntimeException("Token JWT inválido ou expirado.");
        }
    }

    public String getIdClaim(String tokenJwt) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(accessSecret);
            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .withIssuer("sisreserva-api")
                    .build()
                    .verify(tokenJwt);

            return decodedJWT.getClaim("id").asString();
        } catch (JWTVerificationException exception){
            throw new RuntimeException("Token JWT inválido ou expirado.");
        }
    }

    public DecodedJWT parseClaims(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(refreshSecret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("sisreserva-api")
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT;
        } catch (JWTVerificationException | IllegalArgumentException e) {
            throw new RuntimeException("Token inválido ou expirado.", e);
        }
    }

    private Instant accessTokenExpirationDate() {
        return LocalDateTime.now().plusMinutes(15).toInstant(ZoneOffset.of("-03:00"));
    }

    private Instant refreshTokenExpirationDate() {
        //se alterar aqui tem que levar em consideração o expiration date do cookie http em cookieManager
        return LocalDateTime.now().plusDays(30).toInstant(ZoneOffset.of("-03:00"));
    }
}
