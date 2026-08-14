package com.sparkco.lab2_api.features.greeting;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GreetingServiceTest {

    @Test
    void shouldReturnGreetingResponse() {
        GreetingService service = new GreetingService();

        GreetingResponse response = service.getGreeting();

        assertEquals("Hello from feature-based CA!", response.getMessage());
        assertEquals(2026, response.getYear());
    }
}
