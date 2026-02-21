package com.example.bankcards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@Schema(description="Login request")
public class AuthenticateRequestDto {
    @Schema(description = "Username. size must be between 6 and 35 characters.")
    @Size(min=6, max=35, message="username size shouldn't be between 6 and 35 characters")
    private String username;
    @Schema(description = "Password. password must contain at least one letter, one digit and one special symbol. (6 =< password size =< 35).")
    @Size(min=6, max=35, message="password size shouldn't be between 6 and 35 characters")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\\\d)(?=.*[@$!%*#?^&amp;])[A-Za-z\\\\d@$!%*#?^&amp;]{3,}",
            message = "password must contain at least one letter, one digit and one special symbol")
    private String password;
}
