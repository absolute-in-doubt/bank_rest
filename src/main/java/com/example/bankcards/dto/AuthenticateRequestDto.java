package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticateRequestDto {
    private String username;
    private char[] password;

    public AuthenticateRequestDto(@JsonProperty("username") String username,
                                  @JsonProperty("password") char[] password){
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }
}
