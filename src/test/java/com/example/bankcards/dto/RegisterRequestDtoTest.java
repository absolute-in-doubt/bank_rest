package com.example.bankcards.dto;

import com.example.bankcards.enums.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RegisterRequestDtoTest {

    private final ObjectMapper mapper;
    private final Validator validator;

    @Autowired
    RegisterRequestDtoTest(ObjectMapper mapper, Validator validator) {
        this.mapper = mapper;
        this.validator = validator;
    }

    @Test
    public void testSerialization() throws JsonProcessingException {
        String expected = "{\"roles\":[\"ROLE_USER\"],\"first_name\":\"test_name\",\"last_name\":\"test_surname\",\"username\":\"test_username\",\"password\":\"passwd\"}";

        RegisterRequestDto registerRequest = new RegisterRequestDto();
        registerRequest.setFirstName("test_name");
        registerRequest.setLastName("test_surname");
        registerRequest.setUsername("test_username");
        registerRequest.setPassword("passwd");
        registerRequest.setRoles(List.of(Role.ROLE_USER));

        String json = mapper.writeValueAsString(registerRequest);

        System.out.println(json);
        assertEquals(expected, json);
    }

    @Test
    public void testValidation() throws JsonProcessingException {
        String initial = "{\"roles\":[\"ROLE_USER\"],\"first_name\":\"test_name\",\"last_name\":\"test_surname\",\"username\":\"test_username\",\"password\":\"passwd\"}";

        RegisterRequestDto registerRequest = mapper.readValue(initial, RegisterRequestDto.class);

        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(registerRequest);
        assertTrue(violations.isEmpty());

    }
}