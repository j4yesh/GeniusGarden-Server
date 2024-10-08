package com.geniusgarden.server.Controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class greet {
    @GetMapping("/greet")
    private String greet(){
        return "hello and welcome to geniusgardern server.";
    }
}
