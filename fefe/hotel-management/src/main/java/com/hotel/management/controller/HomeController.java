package com.hotel.management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String root() {
        return "forward:/docs/instrucoes.html";
    }

    @GetMapping("/docs")
    public String docsRoot() {
        return "forward:/docs/instrucoes.html";
    }
}