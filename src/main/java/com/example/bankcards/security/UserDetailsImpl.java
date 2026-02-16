package com.example.bankcards.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {
    private String username;
    private String hashedPassword;
    @Getter @Setter
    private Long userId;

    public UserDetailsImpl(String username, String hashedPassword, Long userId) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.userId = userId;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return hashedPassword;
    }

    @Override
    public String getUsername() {
        return username;
    }
}