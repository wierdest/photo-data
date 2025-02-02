package com.wierdest.photo_data.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wierdest.photo_data.services.HelloService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/hello")
public class HelloController {
    private final HelloService service;

    public HelloController(HelloService service) {
        this.service = service;
    }

    // Accessible as http://localhost:8080/hello/param?message=MessageContent
    @GetMapping("/param")
    public String sayHelloParam(@RequestParam String message) {
        return service.getHelloMessage(message);
    }
    
    // Accessible as http://localhost:8080/hello/path/MessageContent
    @GetMapping("/path/{message}")
    public String sayHelloPath(@PathVariable String message) {
        return service.getHelloMessage(message);
    }

}