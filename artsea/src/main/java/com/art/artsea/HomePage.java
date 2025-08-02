package com.art.artsea;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePage {

    @GetMapping("/")
    public String showHome() {
        return "home";
    }

    @GetMapping("/buy")
    public String showBuy() {
        return "buy";
    }

    @GetMapping("/sell")
    public String showSell() {
        return "sell";
    }

    @GetMapping("/cookie-policy")
    public String showCookiePolicy() {
        return "cookie-policy";
    }

    @GetMapping("/faqs")
    public String showFaqs() {
        return "faqs";
    }

    @GetMapping("/privacy-policy")
    public String showPrivacyPolicy() {
        return "privacy-policy";
    }

    @GetMapping("/reach-us")
    public String showReachUs() {
        return "reach-us";
    }

    @GetMapping("/terms-and-conditions")
    public String showTermsAndConditions() {
        return "terms-and-conditions";
    }

    @GetMapping("/who-we-are")
    public String showWhoWeAre() {
        return "who-we-are";
    }

    @GetMapping("/auctions-result")
    public String showAuctionsResult() {
        return "auction-results";
    }

    @GetMapping("/upcoming-auctions")
    public String showUpcomingAuctions() {
        return "upcoming-auctions";
    }

    @GetMapping("/private-services")
    public String showPrivateServices() {
        return "private-services";
    }

    @GetMapping("/storage-services")
    public String showStorageServices() {
        return "storage-services";
    }

    @GetMapping("/collection-services")
    public String showCollectionServices() {
        return "collection-services";
    }

    @GetMapping("/post-sale-services")
    public String showPostSaleServices() {
        return "post-sales-services";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @GetMapping("/signup")
    public String showSignUp() {
        return "login";
    }
}
