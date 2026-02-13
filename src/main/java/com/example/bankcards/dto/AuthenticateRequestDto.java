package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthenticateRequestDto {
    @NotBlank
    @Size(max=35)
    private String username;
    @NotBlank
    @Size(min=6, max=35)
    private String password;

    public AuthenticateRequestDto(@JsonProperty("username") String username,
                                  @JsonProperty("password") String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
