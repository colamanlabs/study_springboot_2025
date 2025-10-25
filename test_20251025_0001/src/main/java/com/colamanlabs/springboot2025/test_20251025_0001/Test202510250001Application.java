package com.colamanlabs.springboot2025.test_20251025_0001;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@Slf4j
public class Test202510250001Application
{

    public static void main(String[] args)
    {
        SpringApplication.run(Test202510250001Application.class, args);
    }


    @EventListener(ApplicationReadyEvent.class)
    public void init()
    {
        log.info(String.format("[Test202510250001Application/init] BEGIN"));
        log.info(String.format("[Test202510250001Application/init] END"));
    }
}
