package com.example.bankcards.dto;

import com.example.bankcards.enums.Role;
import com.example.bankcards.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class UpdateRequestDto {

    @NotBlank(message="User id cannot be null")
    @Positive(message = "User id must be positive")
    @JsonProperty(value="user_id")
    private Long userId;
    @NotBlank(message="first name shouldn't be empty")
    @Size(max=25, message="first name size shouldn't exceed 25 characters")
    @JsonProperty(value="first_name")
    private String firstName;
    @NotBlank(message="last name shouldn't be empty")
    @Size(max=25, message="last name size shouldn't exceed 25 characters")
    @JsonProperty(value="last_name")
    private String lastName;
    @Size(min=6, max=35, message="username size shouldn't be between 6 and 35 characters")
    @JsonProperty(value="username")
    private String username;
    @NotNull(message="roles shouldn't be empty")
    private List<Role> roles;
    @NotNull(message="status shouldn't be null")
    private UserStatus status;
}