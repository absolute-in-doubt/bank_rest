package com.example.bankcards;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class ApplicationTest {


    @Test
    void contextLoads() {
    }

    @Test
    void testRun() {
        SpringApplication.run(Application.class, new String[] {});
    }
}
