package com.example.bankcards.service;

import com.example.bankcards.dto.AuthenticateRequestDto;
import com.example.bankcards.dto.RegisterRequestDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.Role;
import com.example.bankcards.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;

@Service
@Slf4j
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    @Value("${spring.security.jwt.ttl_minutes}")
    private int jwtTTLMinutes;

    @Autowired
    public AuthService(PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder, AuthenticationManager authManager, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.authManager = authManager;
        this.userRepository = userRepository;
    }

    public String authenticateAndCreateJwt(AuthenticateRequestDto authRequest) throws AuthenticationException{
        String hashedPassword = passwordEncoder.encode(authRequest.getPassword());
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), hashedPassword));
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        List<String> authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return formJwt(userDetails.getUsername(), authorities);
    }

    public String registerAndCreateJwt(RegisterRequestDto registerRequest){
        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
        log.debug("hashedPassword length: " + hashedPassword.length());
        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .username(registerRequest.getUsername())
                .passwordHash(hashedPassword)
                .build();
        userRepository.save(user);
        List<String> authorities = registerRequest.getRoles().stream().map(Role::toString).toList();
        return formJwt(registerRequest.getUsername(), authorities);
    }

    public String formJwt(String username, List<String> authorities){
        String authoritiesString = String.join(" ", authorities);
        Instant now = Instant.now();
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuedAt(now)
                .issuer("bank_rest")
                .subject(username)
                .claim("roles", authoritiesString)
                .expiresAt(now.plus(jwtTTLMinutes, ChronoUnit.MINUTES))
                .build();
        var jwtEncoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claimsSet);
        return jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
    }
}
