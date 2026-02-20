package com.example.bankcards.dto;

import com.example.bankcards.enums.Role;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RegisterRequestDtoTest {

    private final ObjectMapper mapper;
    private final Validator validator;

    @Autowired
    RegisterRequestDtoTest(ObjectMapper mapper, Validator validator) {
        this.mapper = mapper;
        this.validator = validator;
    }


    @Test
    public void testValidation() throws JsonProcessingException {
        String initial = "{\"roles\":[\"ROLE_USER\"],\"first_name\":\"test_name\",\"last_name\":\"test_surname\",\"username\":\"test_username\",\"password\":\"passwd\"}";

        RegisterRequestDto registerRequest = mapper.readValue(initial, RegisterRequestDto.class);

        Set<ConstraintViolation<RegisterRequestDto>> violations = validator.validate(registerRequest);
        assertTrue(violations.isEmpty());

    }
}