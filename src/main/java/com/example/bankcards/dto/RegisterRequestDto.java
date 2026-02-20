package com.example.bankcards.dto;

import com.example.bankcards.enums.Role;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class RegisterRequestDto {
    @NotBlank(message="first name shouldn't be empty")
    @Size(max=25, message="first name size shouldn't exceed 25 characters")
    private String firstName;
    @NotBlank(message="last name shouldn't be empty")
    @Size(max=25, message="last name size shouldn't exceed 25 characters")
    private String lastName;
    @Size(min=6, max=35, message="username size shouldn't be between 6 and 35 characters")
    private String username;
    @Size(min=6, max=35, message="password size shouldn't be between 6 and 35 characters")
    private String password;
    @NotNull(message="roles shouldn't be empty")
    private List<Role> roles;


    @JsonSetter(value="first_name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonSetter(value="last_name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonSetter(value="username")
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonSetter(value="password")
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonSetter(value="roles")
    public void setRoles(List<Role> roles){
        this.roles = roles;
    }
}
