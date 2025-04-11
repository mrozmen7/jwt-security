package com.ozmenyavuz.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping()
public class PublicController {


    @GetMapping("/public")
    public String helloWorld() {
        return "Hello World, from public endpoint";
    }
}
