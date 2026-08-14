package com.sparkco.lab2_api.features.greeting;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {

    public GreetingResponse getGreeting() {
        return new GreetingResponse("Hello from feature-based CA!", 2026);
    }
}
