package com.example.bankcards.dto;

import com.example.bankcards.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
@Schema(description="Registration request")
public class RegisterRequestDto {
    @Schema(description="First name. Size =< 25.")
    @NotBlank(message="first name shouldn't be empty")
    @Size(max=25, message="first name size shouldn't exceed 25 characters")
    @JsonProperty(value="first_name")
    private String firstName;
    @Schema(description = "Last name. Size =< 25.")
    @NotBlank(message="last name shouldn't be empty")
    @Size(max=25, message="last name size shouldn't exceed 25 characters")
    @JsonProperty(value="last_name")
    private String lastName;
    @Schema(description = "Username. size must be between 6 and 35 characters.")
    @Size(min=6, max=35, message="username size must be between 6 and 35 characters")
    @JsonProperty(value="username")
    private String username;
    @Schema(description = "Password. password must contain at least one letter, one digit and one special symbol. (6 =< password size =< 35).")
    @Size(min=6, max=35, message="password size shouldn't be between 6 and 35 characters")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\\\d)(?=.*[@$!%*#?^&amp;])[A-Za-z\\\\d@$!%*#?^&amp;]{3,}",
            message = "password must contain at least one letter, one digit and one special symbol")
    private String password;
    @Schema(description = "User's roles ('ROLE_USER', 'ROLE_ADMIN').")
    @NotNull(message="roles shouldn't be empty")
    private List<Role> roles;
}
