package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class AuthenticateRequestDto {
    @NotBlank
    @Size(max=35)
    @JsonProperty("username")
    private String username;
    @NotBlank
    @Size(min=6, max=35)
    @JsonProperty("password")
    private String password;
}
