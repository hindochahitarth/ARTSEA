package com.art.artsea.controller;
import com.art.artsea.model.Auction;
import com.art.artsea.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

}
