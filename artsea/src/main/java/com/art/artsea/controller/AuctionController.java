package com.art.artsea.controller;
import com.art.artsea.model.Auction;
import com.art.artsea.model.Category;
import com.art.artsea.model.User;
import com.art.artsea.service.AuctionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class AuctionController {
    @Autowired
    private AuctionService auctionService;

    // Add Category
    @PostMapping("/admin-save-auction")
    public String addAuction(@ModelAttribute Auction auction,
                             @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        if (!imageFile.isEmpty()) {
            File uploadDir = new File("uploads/");
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get("uploads/", fileName);
            Files.write(filePath, imageFile.getBytes());

            auction.setImageUrl("/uploads/" + fileName);
        }

        auctionService.saveAuction(auction);
        return "redirect:/admin-manage-auction";
    }
    //    Helper method
    private boolean isAdmin(HttpSession session) {
        Object user = session.getAttribute("user");
        if (user instanceof User loggedInUser) {
            return "ADMIN".equalsIgnoreCase(loggedInUser.getRole().name());
        }
        return false;
    }

    @PostMapping("/admin-update-auction")
    public String editAuctionForm(@ModelAttribute Auction auction,
                                  @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                  HttpSession session) throws IOException {
        if (!isAdmin(session)) return "redirect:/";

        Auction existingAuction = auctionService.getAuctionById(auction.getAuctionId());
        if (existingAuction == null) return "redirect:/admin-manage-auction";

        // Update normal fields
        existingAuction.setAuctionName(auction.getAuctionName());
        existingAuction.setApplyStartTime(auction.getApplyStartTime());
        existingAuction.setApplyEndTime(auction.getApplyEndTime());
        existingAuction.setStartTime(auction.getStartTime());
        existingAuction.setEndTime(auction.getEndTime());

        // If new image uploaded, replace it
        if (imageFile != null && !imageFile.isEmpty()) {
            File uploadDir = new File("uploads/");
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get("uploads/", fileName);
            Files.write(filePath, imageFile.getBytes());

            existingAuction.setImageUrl("/uploads/" + fileName);
        }

        auctionService.saveAuction(existingAuction);
        return "redirect:/admin-manage-auction";
    }

    @DeleteMapping("/admin-auctions/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteAuction(@PathVariable("id") Long id, HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        if (user instanceof User) {
            User loggedInUser = (User) user;
            if (!"ADMIN".equalsIgnoreCase(loggedInUser.getRole().name())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Forbidden");
            }
        }

        try {
            auctionService.deleteAuction(id);
            return ResponseEntity.ok("Auction deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting auction: " + e.getMessage());
        }
    }


}
