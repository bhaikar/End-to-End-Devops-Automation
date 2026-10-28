package com.example.javaapp;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private final String greeting;

    public HelloController(@Value("${app.greeting:Hello from Java DevOps!}") String greeting) {
        this.greeting = greeting;
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        // HOSTNAME is the pod name inside Kubernetes, handy to see load balancing
        String host = System.getenv().getOrDefault("HOSTNAME", "local");
        return Map.of("message", greeting, "host", host);
    }
}
