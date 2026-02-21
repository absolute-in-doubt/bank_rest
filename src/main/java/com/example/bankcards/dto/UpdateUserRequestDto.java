package com.example.bankcards.dto;

import com.example.bankcards.enums.Role;
import com.example.bankcards.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
@Schema(description = "Update user request")
public class UpdateUserRequestDto {
    @Schema(description="The ID of the user we want to edit.")
    @NotBlank(message="User id cannot be null")
    @Positive(message = "User id must be positive")
    @JsonProperty(value="user_id")
    private Long userId;
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
    @JsonProperty(value="username")
    private String username;
    @Schema(description = "User's roles ('ROLE_USER', 'ROLE_ADMIN').")
    @NotNull(message="roles shouldn't be empty")
    private List<Role> roles;
    @Schema(description="User's status ('ACTIVE', 'BLOCKED'")
    @NotNull(message="status shouldn't be null")
    private UserStatus status;
}