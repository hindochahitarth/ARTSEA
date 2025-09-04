package com.art.artsea.controller;

import com.art.artsea.model.*;
import com.art.artsea.service.ArtworkService;
import com.art.artsea.service.CategoryService;
import com.art.artsea.service.AuctionService; // You need this service or use AuctionRepository
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class AuctionApplicationController {

    @Autowired
    private ArtworkService artworkService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AuctionService auctionService; // or use AuctionRepository

    private static final String UPLOAD_DIR = "uploads/";

    // Show form
    @GetMapping("/seller-auction-apply/{auctionId}")
    public String showAuctionApplyForm(@PathVariable Long auctionId, Model model, HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj == null) {
            return "redirect:/seller"; // redirect to seller login
        }

        if (userObj instanceof User loggedInUser) {
            if (!"SELLER".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/"; // redirect non-sellers
            }

            Artwork artwork = new Artwork();
            artwork.setUser(loggedInUser);
            model.addAttribute("artwork", artwork);
        } else {
            return "redirect:/seller";
        }

        model.addAttribute("auctionId", auctionId);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "seller/auction-apply";
    }

    // Handle submission
    @PostMapping("/seller-auction-apply/{auctionId}")
    public String applyToAuction(@PathVariable Long auctionId,
                                 @ModelAttribute Artwork artwork,
                                 @RequestParam("imageFile") MultipartFile imageFile,
                                 HttpSession session) {

        Object userObj = session.getAttribute("user");
        if (userObj == null) {
            return "redirect:/seller";
        }

        if (userObj instanceof User loggedInUser) {
            if (!"SELLER".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return "redirect:/";
            }
            artwork.setUser(loggedInUser);
        }

        try {
            // ✅ Fetch category
            if (artwork.getCategory() != null && artwork.getCategory().getCategoryId() != null) {
                Category category = categoryService.getCategoryById(artwork.getCategory().getCategoryId());
                artwork.setCategory(category);
            }

            // ✅ Fetch auction using auctionId from URL
            Auction auction = auctionService.getAuctionById(auctionId);
            artwork.setAuction(auction);

            // ✅ Handle image upload
            if (!imageFile.isEmpty()) {
                File uploadDir = new File("uploads/");
                if (!uploadDir.exists()) uploadDir.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get("uploads/", fileName);
                Files.write(filePath, imageFile.getBytes());

                artwork.setImageUrl("/uploads/" + fileName);
            }


            artworkService.saveArtwork(artwork);
            return "redirect:/artwork-applications";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/error";
        }
    }
}
