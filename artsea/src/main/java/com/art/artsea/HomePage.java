package com.art.artsea;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePage {
    @GetMapping("/")
    public String showHome() {
        return "home";  // render home.html
    }

}

@Controller
 class AuctionPage {
    @GetMapping("/auctions")
    public String showAuctions() {
        return "auctions";  // This renders auctions.html from /templates
    }
}

