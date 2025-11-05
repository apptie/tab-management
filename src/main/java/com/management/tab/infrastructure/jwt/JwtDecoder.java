package com.management.tab.infrastructure.jwt;

import com.management.tab.domain.auth.PrivateClaims;
import com.management.tab.domain.auth.TokenDecoder;
import com.management.tab.config.properties.TokenProperties;
import com.management.tab.config.auth.security.enums.TokenType;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtDecoder implements TokenDecoder {

    private static final String CLAIM_ID = "id";

    private final Clock clock;
    private final JWEDecrypter jweDecrypter;
    private final JwsVerifierFinder jwsVerifierFinder;
    private final TokenProperties tokenProperties;

    @Override
    public Optional<PrivateClaims> decode(TokenType tokenType, String token) {
        validateToken(token);

        return this.parse(tokenType, token)
                   .map(this::convert);
    }

    private void validateToken(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidTokenException("토큰이 존재하지 않거나 길이가 부족합니다.");
        }
    }

    private Optional<JWTClaimsSet> parse(TokenType tokenType, String token) {
        try {
            return extractClaimsSet(tokenType, token);
        } catch (JOSEException e) {
            throw new InvalidTokenException("토큰 디코딩에 실패했습니다", e);
        } catch (ParseException e) {
            throw new InvalidTokenException("유효한 토큰이 아닙니다.", e);
        }
    }

    private Optional<JWTClaimsSet> extractClaimsSet(
            TokenType tokenType, String token
    ) throws ParseException, JOSEException {
        JWTClaimsSet claimsSet = findJWTClaimsSet(tokenType, token);

        validateIssuer(claimsSet);

        return findClaimsSet(claimsSet);
    }

    private JWTClaimsSet findJWTClaimsSet(TokenType tokenType, String token) throws ParseException, JOSEException {
        JWEObject jweObject = findJWEObject(token);
        SignedJWT signedJwt = findSignedJWT(jweObject);
        JWSVerifier jwsVerifier = findJWSVerifier(tokenType);

        validateSign(signedJwt, jwsVerifier);

        return signedJwt.getJWTClaimsSet();
    }

    private JWEObject findJWEObject(String token) throws ParseException, JOSEException {
        JWEObject jweObject = JWEObject.parse(token);

        jweObject.decrypt(jweDecrypter);
        return jweObject;
    }

    private SignedJWT findSignedJWT(JWEObject jweObject) {
        return jweObject.getPayload()
                        .toSignedJWT();
    }

    private JWSVerifier findJWSVerifier(TokenType tokenType) {
        return jwsVerifierFinder.findByTokenType(tokenType);
    }

    private void validateSign(SignedJWT signedJwt, JWSVerifier jwsVerifier) throws JOSEException {
        if (!signedJwt.verify(jwsVerifier)) {
            throw new InvalidTokenException("위변조된 토큰입니다.");
        }
    }

    private boolean isExpiredToken(Date expirationTime) {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime expirationDate = LocalDateTime.ofInstant(expirationTime.toInstant(), ZoneId.systemDefault());

        return expirationDate.isBefore(now);
    }

    private void validateIssuer(JWTClaimsSet claimsSet) {
        if (!tokenProperties.issuer().equals(claimsSet.getIssuer())) {
            throw new InvalidTokenException("서비스에서 발급한 토큰이 아닙니다.");
        }
    }

    private Optional<JWTClaimsSet> findClaimsSet(JWTClaimsSet claimsSet) {
        if (isExpiredToken(claimsSet.getExpirationTime())) {
            return Optional.empty();
        }

        return Optional.of(claimsSet);
    }

    private PrivateClaims convert(JWTClaimsSet claims) {
        Date issueTime = claims.getIssueTime();

        try {
            return new PrivateClaims(
                    claims.getLongClaim(CLAIM_ID),
                    LocalDateTime.ofInstant(issueTime.toInstant(), ZoneId.systemDefault())
            );
        } catch (ParseException e) {
            throw new InvalidTokenException("유효한 형식의 토큰이 아닙니다.");
        }
    }

    public static class InvalidTokenException extends IllegalArgumentException {

        public InvalidTokenException(String s) {
            super(s);
        }

        public InvalidTokenException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
