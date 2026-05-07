package com.example.journal.service;

import com.example.journal.dto.AuthRequest;
import com.example.journal.dto.AuthResponse;
import com.example.journal.dto.RefreshTokenRequest;
import com.example.journal.entity.RefreshToken;
import com.example.journal.entity.User;
import com.example.journal.repository.RefreshTokenRepository;
import com.example.journal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtEncoder jwtEncoder;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.access-token-minutes}")
    private long accessTokenMinutes;

    @Value("${app.jwt.refresh-token-days}")
    private long refreshTokenDays;

    @Transactional
    public AuthResponse login(AuthRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password are required");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        String accessToken = createAccessToken(user);
        String refreshToken = createRefreshTokenForUser(user);

        return new AuthResponse(accessToken, refreshToken, "Bearer", accessTokenMinutes * 60);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token is required");
        }

        String tokenHash = sha256(request.getRefreshToken());

        RefreshToken storedToken = refreshTokenRepository
                .findByTokenHashAndRevokedFalseAndExpiresAtAfter(tokenHash, Instant.now())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        User user = storedToken.getUser();

        storedToken.setRevoked(true);

        String newAccessToken = createAccessToken(user);
        String newRefreshToken = createRefreshTokenForUser(user);

        return new AuthResponse(newAccessToken, newRefreshToken, "Bearer", accessTokenMinutes * 60);
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            return;
        }

        String tokenHash = sha256(request.getRefreshToken());
        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(token -> token.setRevoked(true));
    }

    private String createAccessToken(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plus(accessTokenMinutes, ChronoUnit.MINUTES))
                .id(UUID.randomUUID().toString())
                .claim("userId", user.getId())
                .claim("authorities", List.of(user.getRole().name()))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    private String createRefreshTokenForUser(User user) {
        String rawToken = generateRefreshTokenValue();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(sha256(rawToken));
        refreshToken.setExpiresAt(Instant.now().plus(refreshTokenDays, ChronoUnit.DAYS));
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);
        user.getRefreshTokens().add(refreshToken);

        return rawToken;
    }

    private String generateRefreshTokenValue() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to hash token", ex);
        }
    }
}