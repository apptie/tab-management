package com.management.tab.config.auth.security.config;

import com.management.tab.config.auth.security.config.properties.TokenProperties;
import com.management.tab.config.auth.security.jwt.JwsSignerFinder;
import com.management.tab.config.auth.security.jwt.JwsVerifierFinder;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(TokenProperties.class)
public class SecurityConfig {

    private static final String HMAC_SHA_256 = "HmacSHA256";

    private final TokenProperties tokenProperties;

    @Bean
    public JwsVerifierFinder jwsVerifierFinder(
            SecretKey accessTokenSecretKey,
            SecretKey refreshTokenSecretKey
    ) throws JOSEException {
        JWSVerifier accessTokenJwsVerifier = new MACVerifier(accessTokenSecretKey);
        JWSVerifier refreshTokenJwsVerifier = new MACVerifier(refreshTokenSecretKey);

        return new JwsVerifierFinder(accessTokenJwsVerifier, refreshTokenJwsVerifier);
    }

    @Bean
    public JwsSignerFinder jwsSignerFinder(
            SecretKey accessTokenSecretKey,
            SecretKey refreshTokenSecretKey
    ) throws KeyLengthException {
        MACSigner accessTokenSigner = new MACSigner(accessTokenSecretKey);
        MACSigner refreshTokenSigner = new MACSigner(refreshTokenSecretKey);

        return new JwsSignerFinder(accessTokenSigner, refreshTokenSigner);
    }

    @Bean
    public SecretKey accessTokenSecretKey() {
        byte[] accessTokenKeyBytes = tokenProperties.accessKey().getBytes(StandardCharsets.UTF_8);

        return new SecretKeySpec(accessTokenKeyBytes, HMAC_SHA_256);
    }

    @Bean
    public SecretKey refreshTokenSecretKey() {
        byte[] refreshTokenKeyBytes = tokenProperties.refreshKey().getBytes(StandardCharsets.UTF_8);

        return new SecretKeySpec(refreshTokenKeyBytes, HMAC_SHA_256);
    }
}
