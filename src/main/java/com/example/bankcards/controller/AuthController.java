package com.example.bankcards.controller;

import com.example.bankcards.dto.AuthenticateRequestDto;
import com.example.bankcards.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final AuthService authService;

    @Autowired
    public AuthController(AuthenticationManager authManager, AuthService authService) {
        this.authManager = authManager;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authenticate(AuthenticateRequestDto authRequest){

        try {
            Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            String jwt = authService.formJwt((UserDetails) auth.getPrincipal());
            return new ResponseEntity<>(Map.of("message", "success", "jwt", jwt), HttpStatus.OK);
        } catch (AuthenticationException e){
            return new ResponseEntity<>(Map.of("error", e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    //TODO add registration


    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint(){
        return new ResponseEntity<>("Hello, world!", HttpStatus.OK);
    }
}
