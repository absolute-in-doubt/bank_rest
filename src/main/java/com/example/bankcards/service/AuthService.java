package com.example.bankcards.service;

import com.example.bankcards.dto.AuthenticateRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;

@Service
public class AuthService {

    private final JwtEncoder encoder;
    private final AuthenticationManager authManager;
    @Value("${spring.security.jwt.ttl_minutes}")
    private int jwtTTLMinutes;

    @Autowired
    public AuthService(JwtEncoder encoder, AuthenticationManager authManager) {
        this.encoder = encoder;
        this.authManager = authManager;
    }

    public String formJwt(UserDetails userDetails){
        List<String> authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        Instant now = Instant.now();
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuedAt(now)
                .issuer("bank_rest")
                .subject(userDetails.getUsername())
                .claim("roles", authorities)
                .expiresAt(now.plus(jwtTTLMinutes, ChronoUnit.MINUTES))
                .build();
        return encoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    public void setJwtTTLMinutes(int jwtTTLMinutes) {
        this.jwtTTLMinutes = jwtTTLMinutes;
    }
}
