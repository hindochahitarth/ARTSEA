package com.art.artsea;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePage {
    @GetMapping("/")
    public String showHome() {
        return "home.html";  // render home.html
    }
}

