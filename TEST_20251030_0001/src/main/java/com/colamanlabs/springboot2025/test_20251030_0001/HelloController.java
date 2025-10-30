package com.colamanlabs.springboot2025.test_20251030_0001;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController
{

    @GetMapping("/hello")
    public String hello()
    {
        return "Hello, Spring Web!";
    }
}