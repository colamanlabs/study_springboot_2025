package com.colamanlabs.springboot2025.test_20251030_0001;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController
{

    @GetMapping("/user")
    public Map<String, String> getUser()
    {
        return Map.of("name", "colamanlabs", "role", "developer");
    }
}