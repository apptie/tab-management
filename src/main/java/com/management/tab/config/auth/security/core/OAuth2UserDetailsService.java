package com.management.tab.config.auth.security.core;

import com.management.tab.domain.auth.PrivateClaims;
import com.management.tab.domain.auth.TokenDecoder;
import com.management.tab.config.auth.security.enums.TokenType;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class OAuth2UserDetailsService implements UserDetailsService {

    private final TokenDecoder tokenDecoder;

    @Override
    public OAuth2UserDetails loadUserByUsername(String token) throws UsernameNotFoundException {
        return tokenDecoder.decode(TokenType.ACCESS, token)
                           .map(this::convert)
                           .orElse(null);
    }

    private OAuth2UserDetails convert(PrivateClaims privateClaims) {
        return new OAuth2UserDetails(
                privateClaims.userId(),
                Set.of(new SimpleGrantedAuthority(String.valueOf(privateClaims.userId())))
        );
    }
}
