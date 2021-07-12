package com.bookportal.api.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProfanityServiceTest {

    @Autowired
    ProfanityService profanityService;

    @Test
    void isProfanity() {
        boolean amk = profanityService.isProfanity("f##k");
        assertTrue(amk);
    }
    @Test
    void isProfanity_false() {
        boolean response = profanityService.isProfanity("hello");
        assertFalse(response);
    }
}