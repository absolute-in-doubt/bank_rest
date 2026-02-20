package com.example.bankcards.util;

import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

import java.util.ArrayList;

@Component
public class UserDtoMapper {
    public UserDto toDto(User user){
        return UserDto.builder()
                .roles(user.getRoles())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .build();
    }

    public List<UserDto> toDtoList(List<User> users){
        List<UserDto> userDtos = new ArrayList<>();
        for(User user: users){
            userDtos.add(this.toDto(user));
        }
        return userDtos;
    }
}
