package com.example.bankcards.controller;

import com.example.bankcards.dto.ErrorResponseDto;
import com.example.bankcards.dto.RegisterRequestDto;
import com.example.bankcards.dto.UpdateUserRequestDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.exception.ServerBusyException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
public class UserController {

    private final AuthService authService;
    private final UserService userService;

    public UserController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/users/new")
    public ResponseEntity<Void> createNewUser(@RequestBody @Valid RegisterRequestDto request){
        authService.register(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/users/new")
    public ResponseEntity<Void> updateUser(@RequestBody @Valid UpdateUserRequestDto request) throws UserNotFoundException, ServerBusyException {
        userService.updateUser(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users/{user_id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("user_id") Long userId) throws UserNotFoundException {
        return new ResponseEntity<>(userService.getUser(userId),HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() throws UserNotFoundException {
        return new ResponseEntity<>(userService.getAllUsers(),HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/users/{user_id}/delete")
    public ResponseEntity<Void> deleteUser(@PathVariable("user_id") Long userId) throws UserNotFoundException {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<ErrorResponseDto> notFoundExceptionsHandler(Exception e){
        return new ResponseEntity<>(new ErrorResponseDto(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ServerBusyException.class)
    public ResponseEntity<ErrorResponseDto> serverBusyExceptionHandler(ServerBusyException e){
        return new ResponseEntity<>(new ErrorResponseDto(e.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
    }
}
