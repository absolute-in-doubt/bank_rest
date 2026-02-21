package com.example.bankcards.controller;

import com.example.bankcards.dto.AuthResponseDto;
import com.example.bankcards.dto.AuthenticateRequestDto;
import com.example.bankcards.dto.ErrorResponseDto;
import com.example.bankcards.dto.RegisterRequestDto;
import com.example.bankcards.service.AuthService;
import jakarta.validation.Valid;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> authenticate(@RequestBody @Valid AuthenticateRequestDto authRequest){
        String jwt = authService.authenticateAndCreateJwt(authRequest);
        return ResponseEntity.ok(new AuthResponseDto(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid RegisterRequestDto registerRequest){
        log.trace("Received register request: {}", registerRequest);
        String jwt = authService.registerAndCreateJwt(registerRequest);
        return ResponseEntity.ok(new AuthResponseDto(jwt));
    }


    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint(){
        return new ResponseEntity<>("Hello, world!", HttpStatus.OK);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> authenticationExceptionHandler(AuthenticationException e){
        return new ResponseEntity<>(new ErrorResponseDto(e.getMessage()),HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponseDto> authenticationExceptionHandler(BindException e){
        List<String> errorMessages = e.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList();
        return new ResponseEntity<>(new ErrorResponseDto(errorMessages),HttpStatus.BAD_REQUEST);
    }
}
